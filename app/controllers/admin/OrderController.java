package controllers.admin;

import com.avaje.ebean.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import constan.DatatablesConstant;
import helper.Util;
import models.*;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import request.OrderRequest;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class OrderController extends Controller {
    public Result index() {
        return ok(views.html.admin.order.list.render());
    }

    public Result add() {
        Form<Order> data = Form.form(Order.class);
        List<Customer> customers = Customer.find.all();
        List<Product> products = Product.find.all();
        return ok(views.html.admin.order.form.render("Order", "Add", routes.OrderController.store(), data, customers, products));
    }

    public Result store() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode json = request().body().asJson();
            OrderRequest node = mapper.convertValue(json, OrderRequest.class);
            Order order = new Order();

            order.no_transaksi = node.getNoTransaksi();
            order.customer = Customer.find.byId(node.getCustomerID());
            order.createdAt = new Date();
            order.save();

            node.getDetail().forEach(x -> {
                        DetailsOrder detailsOrder = new DetailsOrder();

                        detailsOrder.order = Order.find.byId(order.id);
                        detailsOrder.product = Product.find.byId(x.getProductID());
                        detailsOrder.price = x.getPrice();
                        detailsOrder.quantity = x.getQuantity();
                        detailsOrder.sub_total = x.getPrice() * x.getQuantity();

                        detailsOrder.save();
                    }
            );

            Order updateOrder = Order.find.byId(order.id);

            updateOrder.total = Util.sumTotal(order.id);

            updateOrder.update();

            return ok(Json.toJson("Data Berhasil Disimpan"));

        } catch (Exception e) {
            e.printStackTrace();
            return badRequest(e.getMessage());
        }
    }

    public Result listOrder() {
        try {
            Map<String, String[]> parameters = request().queryString();

            int totalNumberOfOrder = Order.find.findRowCount();
            String searchParam = parameters.get(DatatablesConstant.DATATABLE_PARAM_SEARCH)[0];
            int pageSize = Integer.parseInt(parameters.get(DatatablesConstant.DATATABLE_PARAM_DISPLAY_LENGTH)[0]);
            int page = Integer.valueOf(parameters.get(DatatablesConstant.DATATABLE_PARAM_DISPLAY_START)[0]) / pageSize;
            String sortBy = "id";
            String order = parameters.get(DatatablesConstant.DATATABLE_PARAM_SORTING_ORDER)[0];
            Integer sortingColumnId = Integer.valueOf(parameters.get(DatatablesConstant.DATATABLE_PARAM_SORTING_COLUMN)[0]);

            switch (sortingColumnId) {

                case 2:
                    sortBy = "createdAt";
                    break;
                case 3:
                    sortBy = "no_transaksi";
                    break;
                case 4:
                    sortBy = "customerID";
                    break;
            }

            PagedList<Order> orderPage = Order.page(page, pageSize, sortBy, order, searchParam);

            ObjectNode result = Json.newObject();

            result.put("draw", parameters.get(DatatablesConstant.DATATABLE_PARAM_DRAW)[0]);
            result.put("recordsTotal", totalNumberOfOrder);
            result.put("recordsFiltered", orderPage.getTotalRowCount());

            ArrayNode an = result.putArray("data");
            int num = Integer.valueOf(parameters.get("start")[0]) + 1;
            for (Order c : orderPage.getList()) {
                ObjectNode row = Json.newObject();
                String action = "";
                action += "&nbsp;<a href=\"order/" + c.id + "/show" + "\"><i class=\"fa fa-check\"></i>Show</a>";
                action += "&nbsp;<a href=\"order/" + c.id + "/edit" + "\"><i class =\"fa fa-edit\"></i>Edit</a>";
                action += "&nbsp;<a href=\"javascript:deleteDataOrder(" + c.id + ");\"><i class=\"fa fa-remove\"></i>Delete</a>&nbsp;";

                row.put("0", num);
                row.put("1", Util.convertDate(c.createdAt));
                row.put("2", c.no_transaksi);
                row.put("3", c.customer.name);
                row.put("4", c.total);
                row.put("5", action);
                an.add(row);
                num++;
            }

            return ok(Json.toJson(result));

        } catch (NumberFormatException e) {
            return badRequest();
        }
    }

    public Result edit(Long id) {
        Order order = Order.find.byId(id);
        List<Customer> customers = Customer.find.all();
        List<Product> products = Product.find.all();
        List<DetailsOrder> detailsOrders = DetailsOrder.find.where().eq("order_id", order.id).findList();

        Form<Order> orderForm;
        if (order != null) {
            orderForm = Form.form(Order.class).fill(order);
        } else {
            flash("error", "failed to edit data");
            return redirect(routes.OrderController.index());
        }
        return ok(views.html.admin.order.editForm.render("Order", "Edit", routes.OrderController.update(),
                orderForm, customers, products, detailsOrders));
    }

    public Result update() {
        ObjectMapper mapper = new ObjectMapper();
        try{
            JsonNode json = request().body().asJson();
            OrderRequest node = mapper.convertValue(json, OrderRequest.class);
            Order order = Order.find.byId(node.getId());

            order.no_transaksi = node.getNoTransaksi();
            order.customer = Customer.find.byId(node.getCustomerID());
            order.updateAt = new Date();
            order.update();

            List<DetailsOrder> deleteDetailsOrder = DetailsOrder.find.where().eq("order_id", order.id).findList();
            deleteDetailsOrder.forEach(Model::delete);

            node.getDetail().forEach(x->{
                DetailsOrder detailsOrder = new DetailsOrder();
                detailsOrder.order = Order.find.byId(order.id);
                detailsOrder.product = Product.find.byId(x.getProductID());
                detailsOrder.price = x.getPrice();
                detailsOrder.quantity = x.getQuantity();
                detailsOrder.sub_total = x.getPrice() * x.getQuantity();
                detailsOrder.save();
            });

            Order updateOrder = Order.find.byId(order.id);
            updateOrder.total = Util.sumTotal(order.id);
            updateOrder.update();

            return ok(Json.toJson("Data berhasil diubah"));
        }catch (Exception e){
            e.printStackTrace();
            return badRequest(e.getMessage());
        }
    }

    public Result delete(Long id) {
        Order order = Order.find.byId(id);
        int status = 0;
        if (order != null) {
            order.delete();
            status = 1;
        }
        String message = status == 1 ? "Order success deleted" : "Order failed deleted";

        return ok(message);
    }

    public Result showDetail(Long id){
        List<DetailsOrder> detail = DetailsOrder.find.where().eq("order_id", id).findList();

        return ok(views.html.admin.order.show.render(detail,id));
    }

}

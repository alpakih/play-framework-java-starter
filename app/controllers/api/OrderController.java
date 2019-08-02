package controllers.api;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Customer;
import models.DetailsOrder;
import models.Order;
import models.Product;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import request.OrderRequest;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrderController extends Controller {

    public Result getOrderAll() {
        List<Order> order = Order.find.all();

        ObjectNode nodeOrder = Json.newObject();
        ArrayNode arrayNode = nodeOrder.putArray("order");

        order.forEach(x->{
            ObjectNode rowOrder = Json.newObject();
            rowOrder.put("id", x.id);
            rowOrder.put("no_transaksi", x.no_transaksi);
            rowOrder.put("customer", x.customer.name);

            ObjectNode nodeDetail = Json.newObject();
            ArrayNode arrayNodeDetail = nodeDetail.putArray("detail");

            x.detail.forEach(i->{
                ObjectNode rowDetail = Json.newObject();
                rowDetail.put("product", i.product.name);
                rowDetail.put("price", i.product.price);
                rowDetail.put("quantity", i.quantity);
                rowDetail.put("sub_total", i.sub_total);
                arrayNodeDetail.add(rowDetail);
            });

            rowOrder.put("detail", arrayNodeDetail);
            rowOrder.put("total", x.total);
            arrayNode.add(rowOrder);
            });

        return ok(Json.toJson(arrayNode));
    }

    public Result getOrderList(Long id) {
        List<Order> order = Order.find.where().eq("id", id).findList();

        List<DetailsOrder> detailsOrders = DetailsOrder.find.where().eq("order_id", id).findList();

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("order_id", order.get(0).id);
        map.put("no_transaksi", order.get(0).no_transaksi);
        map.put("customer", order.get(0).customer.name);
        ObjectNode objectNode = Json.newObject();
        ArrayNode arrayNode = objectNode.putArray("detail");
        detailsOrders.forEach(x -> {
            ObjectNode row = Json.newObject();
            row.put("product", x.product.name);
            row.put("price", x.product.price);
            row.put("quantity", x.quantity);
            row.put("sub_total", x.sub_total);
            arrayNode.add(row);
        });
        map.put("detail", arrayNode);
        map.put("total", order.get(0).total);

        return ok(Json.toJson(map));
    }

    public Result createOrder() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode json = request().body().asJson();
            OrderRequest node = mapper.convertValue(json, OrderRequest.class);
            Order order = new Order();

            order.no_transaksi = node.getNoTransaksi();
            order.customer = Customer.find.byId(node.getCustomerID());
            order.createdAt = new Date();

            order.save();

            node.getDetail().forEach(x ->{
                DetailsOrder detailsOrder = new DetailsOrder();
                detailsOrder.order = Order.find.byId(order.id);
                detailsOrder.product = Product.find.byId(x.getProductID());
                detailsOrder.price = x.getPrice();
                detailsOrder.quantity = x.getQuantity();
                detailsOrder.sub_total = x.getPrice() * x.getQuantity();

                detailsOrder.save();
            });

           Order updateOrder = Order.find.byId(order.id);
           updateOrder.total = sumTotal(order.id);
           updateOrder.update();

            Map<String,Object> map = new LinkedHashMap<>();

            map.put("order_id",updateOrder.id);
            map.put("no_transaksi",updateOrder.no_transaksi);
            map.put("customer", updateOrder.customer.name);
            ObjectNode objectNode = Json.newObject();
            ArrayNode arrayNode = objectNode.putArray("detail");
            updateOrder.detail.forEach(x->{
                ObjectNode row = Json.newObject();
                row.put("product", x.product.name);
                row.put("price", x.product.price);
                row.put("quantity", x.quantity);
                row.put("sub_total", x.sub_total);
                arrayNode.add(row);
            });

            map.put("detail", arrayNode);
            map.put("total", updateOrder.total);

            return ok(Json.toJson(map));

        } catch (Exception e) {
            e.printStackTrace();
            return badRequest();
        }
    }

    public Result updateOrder(Long id) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode json = request().body().asJson();
            OrderRequest node = mapper.convertValue(json, OrderRequest.class);
            Order order = Order.find.byId(id);

            order.no_transaksi = node.getNoTransaksi();
            order.customer = Customer.find.byId(node.getCustomerID());
            order.updateAt = new Date();

            order.update();

            List<DetailsOrder> deleteDetailsOrder = DetailsOrder.find.where().eq("order_id", order.id).findList();
            deleteDetailsOrder.forEach(Model::delete);

            node.getDetail().forEach(x -> {
                DetailsOrder detailsOrder = new DetailsOrder();
                detailsOrder.order = Order.find.byId(order.id);
                detailsOrder.product = Product.find.byId(x.getProductID());
                detailsOrder.price = x.getPrice();
                detailsOrder.quantity = x.getQuantity();
                detailsOrder.sub_total = x.getPrice() * x.getQuantity();

                detailsOrder.save();
            });

            Order updateOrder = Order.find.byId(order.id);

            updateOrder.total = sumTotal(order.id);

            updateOrder.update();

            Map<String, Object> map = new LinkedHashMap<>();
            map.put("order.id", updateOrder.id);
            map.put("no_transaksi", updateOrder.no_transaksi);
            map.put("customer", updateOrder.customer.name);
            ObjectNode objectNode = Json.newObject();
            ArrayNode arrayNode = objectNode.putArray("detail");
            updateOrder.detail.forEach(x -> {
                ObjectNode row = Json.newObject();
                row.put("productID", x.product.name);
                row.put("price", x.product.price);
                row.put("quantity", x.quantity);
                row.put("sub_total", x.sub_total);
                arrayNode.add(row);
            });
            map.put("detail", arrayNode);
            map.put("total", updateOrder.total);

            return ok(Json.toJson(map));

        } catch (Exception e) {
            e.printStackTrace();
            return badRequest(e.getMessage());
        }
    }

    public Result deleteOrder(Long id) {
        Order order = Order.find.byId(id);
        try {
            if (order != null) {
                order.delete();
            } else {
                return ok("data not found");
            }

            return ok(Json.toJson("delete success"));
        } catch (Exception e) {
            return badRequest("delete failed");
        }

    }

    private static Double sumTotal(Long orderId) {
        String sql = "SELECT SUM(sub_total) as total FROM details_order WHERE order_id = '" + orderId + "'";

        SqlQuery sqlQuery = Ebean.createSqlQuery(sql);
        SqlRow row = sqlQuery.findUnique();
        return row.getDouble("total");

    }


//    private Map<String, Object> responseOk(Object objects) {
//
//        Map<String, Object> map = new LinkedHashMap<>();
//
//        ObjectNode objectNodeMeta = Json.newObject();
//        objectNodeMeta.put("error", 0);
//        objectNodeMeta.put("status", 200);
//        objectNodeMeta.put("message", "Success execute");
//
//        map.put("meta", objectNodeMeta);
//        map.put("data", objects);
//
//        return map;
//
//    }

}

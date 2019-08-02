package controllers.admin;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.PagedList;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import constan.DatatablesConstant;
import models.Customer;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import scala.util.parsing.combinator.testing.Str;

import java.util.Map;

public class CustomerController extends Controller {
    public Result index(){
        return ok(views.html.admin.customer.list.render());
    }

    public Result add(){
        Form<Customer> data = Form.form(Customer.class);
        return ok(views.html.admin.customer.form.render("Customer", "Add", routes.CustomerController.store(), data));
    }

    public Result store(){
        try{
            Ebean.beginTransaction();

            DynamicForm customerForm = Form.form().bindFromRequest();

            Customer customer = new Customer();

            customer.name = customerForm.data().get("name");
            customer.address = customerForm.data().get("address");

            customer.save();

            Ebean.commitTransaction();
            flash("success", "Customer " + customerForm.data().get("name") + " has been created");
        }catch (Exception e){
            Ebean.rollbackTransaction();
            flash("error", e.getMessage());
        }finally {
            Ebean.endTransaction();
        }
        return redirect(routes.CustomerController.index());
    }

    public Result listCustomer(){
        try{
            Map<String, String[]> parameters = request().queryString();

            int totalNumberOfCustomer = Customer.find.findRowCount();
            String searchParam = parameters.get(DatatablesConstant.DATATABLE_PARAM_SEARCH)[0];
            int pageSize = Integer.parseInt(parameters.get(DatatablesConstant.DATATABLE_PARAM_DISPLAY_LENGTH)[0]);

            int page = Integer.valueOf(parameters.get(DatatablesConstant.DATATABLE_PARAM_DISPLAY_START)[0])/pageSize;
            String sortBy = "id";
            String order = parameters.get(DatatablesConstant.DATATABLE_PARAM_SORTING_ORDER)[0];

            Integer sortingColumnId = Integer.valueOf(parameters.get(DatatablesConstant.DATATABLE_PARAM_SORTING_COLUMN)[0]);

            if (sortingColumnId == 2){
                sortBy = "name";
            }

            PagedList<Customer> customerPage = Customer.page(page, pageSize, sortBy, order, searchParam);

            ObjectNode result = Json.newObject();

            result.put("draw", parameters.get(DatatablesConstant.DATATABLE_PARAM_DRAW)[0]);
            result.put("recordsTotal", totalNumberOfCustomer);
            result.put("recordsFiltered", customerPage.getTotalRowCount());

            ArrayNode an = result.putArray("data");
            int num = Integer.valueOf(parameters.get("start")[0]) + 1;
            for (Customer c : customerPage.getList()){
                ObjectNode row = Json.newObject();
                String action = "";
                action += "&nbsp;<a href=\"customer/" + c.id + "/edit" + "\"><i class =\"fa fa-edit\"></i>Edit</a>";
                action += "&nbsp;<a href=\"javascript:deleteDataCustomer(" + c.id + ");\"><i class=\"fa fa-remove\"></i>Delete</a>&nbsp;";

                row.put("0", num);
                row.put("1", c.name);
                row.put("2", c.address);
                row.put("3", action);
                an.add(row);
                num++;
            }
            return ok(Json.toJson(result));
        }catch (NumberFormatException e){
            return badRequest();
        }
    }

    public Result edit(Long id){
        Customer customer = Customer.find.byId(id);
        Form<Customer> customerForm;
        if(customer != null){
            customerForm = Form.form(Customer.class).fill(customer);
        }else {
            flash("error", "failed to edit data");
            return redirect(routes.CustomerController.index());
        }
        return ok(views.html.admin.customer.form.render("Customer", "Edit", routes.CustomerController.update(), customerForm));
    }

    public Result update(){
        try {
            Ebean.beginTransaction();
            Form<Customer> data = Form.form(Customer.class).bindFromRequest();
            //Validation
            if (data.hasErrors()) {
                flash("error", "Customer " + data.get().name + " failed update");
            }
            Customer customer = data.get();
            customer.update();
            Ebean.commitTransaction();
            flash("success", "Customer " + data.get().name + " success updated");
        }catch (Exception e){
            flash("Error", e.getMessage());
            Ebean.rollbackTransaction();
        }finally {
            Ebean.endTransaction();
        }
        return redirect(routes.CustomerController.index());
    }

    public Result delete(Long id){
        Customer customer = Customer.find.byId(id);
        int status = 0;
        if(customer != null){
            customer.delete();
            status = 1;
        }
        String message = status == 1 ? "Customer success deleted" : "Customer failed deleted";

        return ok(message);
    }
}

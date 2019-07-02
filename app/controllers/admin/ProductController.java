package controllers.admin;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.PagedList;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import constan.DatatablesConstant;
import helper.Util;
import models.Product;
import models.User;
import org.mindrot.jbcrypt.BCrypt;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.util.Map;

public class ProductController extends Controller {

    public Result index() {
        return ok(views.html.admin.product.list.render());
    }

    public Result add() {
        Form<Product> data = Form.form(Product.class);
        return ok(views.html.admin.product.form.render("Product", "Add", routes.ProductController.store(), data));
    }

    public Result listProduct() {

        try {
            Map<String, String[]> parameters = request().queryString();

            int totalNumberOfUser = User.find.findRowCount();
            String searchParam = parameters.get(DatatablesConstant.DATATABLE_PARAM_SEARCH)[0];
            int pageSize = Integer.parseInt(parameters.get(DatatablesConstant.DATATABLE_PARAM_DISPLAY_LENGTH)[0]);

            int page = Integer.valueOf(parameters.get(DatatablesConstant.DATATABLE_PARAM_DISPLAY_START)[0]) / pageSize;

            String sortBy = "id";
            String order = parameters.get(DatatablesConstant.DATATABLE_PARAM_SORTING_ORDER)[0];

            Integer sortingColumnId = Integer.valueOf(parameters.get(DatatablesConstant.DATATABLE_PARAM_SORTING_COLUMN)[0]);

            switch (sortingColumnId) {

                case 2:
                    sortBy = "name";
                    break;
                case 3:
                    sortBy = "price";
                    break;
                case 4:
                    sortBy = "description";
                    break;
            }

            PagedList<Product> productPage = Product.page(page, pageSize, sortBy, order, searchParam);

            ObjectNode result = Json.newObject();

            result.put("draw", parameters.get(DatatablesConstant.DATATABLE_PARAM_DRAW)[0]);
            result.put("recordsTotal", totalNumberOfUser);
            result.put("recordsFiltered", productPage.getTotalRowCount());

            ArrayNode an = result.putArray("data");
            int num = Integer.valueOf(parameters.get("start")[0]) + 1;
            for (Product c : productPage.getList()) {
                ObjectNode row = Json.newObject();
                String action = "";
                action += "&nbsp;<a href=\"product/" + c.id + "/edit" + "\"><i class =\"fa fa-edit\"></i>Edit</a>";
                action += "&nbsp;<a href=\"javascript:deleteDataProduct(" + c.id + ");\"><i class=\"fa fa-remove\"></i>Delete</a>&nbsp;";

                row.put("0", num);
                row.put("1", "&nbsp;<img src=\"" + Util.BASE_IMAGE + "/products/" + c.photo + "\" style=\"width:100px\">");
                row.put("2", c.name);
                row.put("3", c.price);
                row.put("4", c.description);
                row.put("5", action);
                an.add(row);
                num++;
            }

            return ok(Json.toJson(result));

        } catch (NumberFormatException e) {
            Logger.debug("" + e.getMessage());
            return badRequest();
        }
    }

    public Result store() {
        try {
            Ebean.beginTransaction();
            //Binding data from form / view
            Form<Product> productForm = Form.form(Product.class).bindFromRequest();
            //Binding data as multipartFormData to get file
            Http.MultipartFormData multipartFormData = request().body().asMultipartFormData();
            Http.MultipartFormData.FilePart productImage = multipartFormData.getFile("image");
            //Validation
            if (productForm.hasErrors()) {
                flash("error", "Product " + productForm.get().name + " failed created");
            }
            //Save to table
            Product product = productForm.get();
            if (productImage != null) {
                product.photo = Util.saveImage(productImage, "products");
            }
            product.save();
            Ebean.commitTransaction();
            flash("success", "Product " + productForm.get().name + " has been created");
        } catch (Exception e) {
            Ebean.rollbackTransaction();
            flash("error", e.getMessage());
        } finally {
            Ebean.endTransaction();
        }
        return redirect(routes.ProductController.index());
    }

    public Result delete(Long id) {
        Product product = Product.find.byId(id);
        int status = 0;
        if (product != null) {
            product.delete();
            status = 1;
        }
        String message = status == 1 ? "Product success deleted" : "Product failed deleted";

        return ok(message);
    }

    public Result edit(Long id) {
        Product product = Product.find.byId(id);
        Form<Product> productForm;
        if (product != null) {
            productForm = Form.form(Product.class).fill(product);
        } else {
            flash("error", "failed to edit data");
            return redirect(routes.ProductController.index());

        }
        return ok(views.html.admin.product.form.render("Product", "Edit", routes.ProductController.update(), productForm));
    }

    public Result update() {

        try {
            Ebean.beginTransaction();
            //Binding data from form / view
            Form<Product> form = Form.form(Product.class).bindFromRequest();
            //Binding data as multipartFormData to get file
            Http.MultipartFormData multipartFormData = request().body().asMultipartFormData();
            Http.MultipartFormData.FilePart productImage = multipartFormData.getFile("image");

            //Validation
            if (form.hasErrors()) {
                flash("error", "Product " + form.get().name + " failed update");
            }

            Product formData = form.get();
            if (productImage != null) {
                formData.photo = Util.saveImage(productImage, "products");
            }
            formData.update();
            Ebean.commitTransaction();

            flash("success", "Product "
                    + form.get().name + " success updated");

        } catch (Exception e) {
            flash("error", e.getMessage());
            Ebean.rollbackTransaction();
        } finally {
            Ebean.endTransaction();
        }
        return redirect(routes.ProductController.index());
    }
}

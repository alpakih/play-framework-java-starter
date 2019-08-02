package controllers.admin;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.PagedList;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import constan.DatatablesConstant;
import models.Category;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;
import java.util.Map;

public class CategoryController extends Controller {
    public Result index() {
//        List<Category> category = Category.find.all();
        return ok(views.html.admin.category.list.render());
    }

    public Result add() {
        Form<Category> data = Form.form(Category.class);
        return ok(views.html.admin.category.form.render("Catagory", "Add", routes.CategoryController.store(), data));
    }



    public Result listCategoryProduct() {
        try {
            Map<String, String[]> parameters = request().queryString();

            int totalNumberOfCategory = Category.find.findRowCount();
            String searchParam = parameters.get(DatatablesConstant.DATATABLE_PARAM_SEARCH)[0];
            int pageSize = Integer.parseInt(parameters.get(DatatablesConstant.DATATABLE_PARAM_DISPLAY_LENGTH)[0]);
            int page = Integer.valueOf(parameters.get(DatatablesConstant.DATATABLE_PARAM_DISPLAY_START)[0]) / pageSize;
            String sortBy = "id";
            String order = parameters.get(DatatablesConstant.DATATABLE_PARAM_SORTING_ORDER)[0];
            Integer sortingColumnId = Integer.valueOf(parameters.get(DatatablesConstant.DATATABLE_PARAM_SORTING_COLUMN)[0]);

            if (sortingColumnId == 2) {
                sortBy = "name";
            }

            PagedList<Category> categoryPage = Category.page(page, pageSize, sortBy, order, searchParam);

            ObjectNode result = Json.newObject();

            result.put("draw", parameters.get(DatatablesConstant.DATATABLE_PARAM_DRAW)[0]);
            result.put("recordsTotal", totalNumberOfCategory);
            result.put("recordsFiltered", categoryPage.getTotalRowCount());

            ArrayNode an = result.putArray("data");
            int num = Integer.valueOf(parameters.get("start")[0]) + 1;
            for (Category c : categoryPage.getList()) {
                ObjectNode row = Json.newObject();
                String action = "";
                action += "&nbsp;<a href=\"category/" + c.id + "/edit" + "\"><i class =\"fa fa-edit\"></i>Edit</a>";
                action += "&nbsp;<a href=\"javascript:deleteDataCategory(" + c.id + ");\"><i class=\"fa fa-remove\"></i>Delete</a>&nbsp;";

                row.put("0", num);
                row.put("1", c.name);
                row.put("2", action);
                an.add(row);
                num++;
            }
            return ok(Json.toJson(result));
        } catch (NumberFormatException e) {
            return badRequest();
        }
    }

    public Result store() {
        try {
            Ebean.beginTransaction();
            //Binding data from form / view
            DynamicForm categoryForm = Form.form().bindFromRequest();
            Category category = new Category();
            category.name = categoryForm.data().get("name");

            //Save to table
            category.save();
            Ebean.commitTransaction();
            flash("success", "Category " + categoryForm.data().get("name") + " has been created");
        } catch (Exception e) {
            Ebean.rollbackTransaction();
            flash("error", e.getMessage());
        } finally {
            Ebean.endTransaction();
        }
        return redirect(routes.CategoryController.index());
    }

    public Result delete(Long id) {
        Category category = Category.find.byId(id);
        int status = 0;
        if (category != null) {
            category.delete();
            status = 1;
        }
        String message = status == 1 ? "Category success deleted" : "Category failed deleted";

        return ok(message);
    }

    public Result edit(Long id) {
        Category category = Category.find.byId(id);
        Form<Category> categoryForm;
        if (category != null) {
            categoryForm = Form.form(Category.class).fill(category);
        } else {
            flash("error", "failed to edit data");
            return redirect(routes.CategoryController.index());
        }
        return ok(views.html.admin.category.form.render("Category", "Edit", routes.CategoryController.update(), categoryForm));
    }

    public Result update() {
        try {
            Ebean.beginTransaction();
            //Binding data from form / view
            Form<Category> data = Form.form(Category.class).bindFromRequest();

            //Validation
            if (data.hasErrors()) {
                flash("error", "Category " + data.get().name + " failed update");
            }

            Category category = data.get();
            category.update();
            Ebean.commitTransaction();
            flash("success", "Category " + data.get().name + " success updated");
        } catch (Exception e) {
            flash("error", e.getMessage());
            Ebean.rollbackTransaction();
        } finally {
            Ebean.endTransaction();
        }
        return redirect(routes.CategoryController.index());
    }
}

package controllers.admin;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.PagedList;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import constan.DatatablesConstant;
import models.User;
import org.mindrot.jbcrypt.BCrypt;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Map;

public class UserController extends Controller {

    public Result index() {
        return ok(views.html.admin.users.list.render());
    }

    public Result add() {
        Form<User> data = Form.form(User.class);
        return ok(views.html.admin.users.form.render("User", "Add", routes.UserController.store(), data));
    }

    public Result edit(Long id) {
        User user = User.find.byId(id);
        Form<User> userForm = null;
        if (user != null) {
            userForm = Form.form(User.class).fill(user);
        } else {
            return redirect(routes.UserController.index());

        }
        return ok(views.html.admin.users.form.render("User", "Edit", routes.UserController.update(), userForm));
    }

    public Result update() {

        try {
            Ebean.beginTransaction();
            Form<User> form = Form.form(User.class).bindFromRequest();
            if (form.hasErrors()) {
                flash("error", "User " + form.get().name + " failed created");

            } else {
                User formData = form.get();
                if (formData != null) {
                    formData.name = form.get().name;
                    formData.email = form.get().email;
                    formData.password = BCrypt.hashpw(form.get().password, BCrypt.gensalt());
                    formData.phoneNumber = form.get().phoneNumber;

                    formData.update();
                    Ebean.commitTransaction();

                    flash("success", "User "
                            + form.get().name + " success created");
                } else {
                    flash("error", "Error!");
                }
            }
        } catch (Exception e) {
            Ebean.rollbackTransaction();
        } finally {
            Ebean.endTransaction();
        }
        return redirect(routes.UserController.index());
    }

    public Result store() {
        try {
            Ebean.beginTransaction();
            Form<User> userForm = Form.form(User.class).bindFromRequest();
            if (userForm.hasErrors()) {
                flash("error", "User " + userForm.get().name + " failed created");
            } else if (!userForm.get().password.equals(userForm.get().confirmPassword)) {
                flash("error", "Wrong confirm password");
            }
            User user = new User();
            user.name = userForm.get().name;
            user.email = userForm.get().email;
            user.password = BCrypt.hashpw(userForm.get().password, BCrypt.gensalt());
            user.phoneNumber = userForm.get().phoneNumber;

            user.save();

            Ebean.commitTransaction();
            flash("success", "User " + userForm.get().name + " has been created");
        } catch (Exception e) {
            Ebean.rollbackTransaction();
            flash("error", e.getMessage());
        } finally {
            Ebean.endTransaction();
        }
        return redirect(routes.UserController.index());
    }

    public Result listUser() {

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

                case 1:
                    sortBy = "name";
                    break;
                case 2:
                    sortBy = "email";
                    break;
                case 3:
                    sortBy = "phoneNumber";
                    break;

            }

            PagedList<User> userPage = User.page(page, pageSize, sortBy, order, searchParam);

            ObjectNode result = Json.newObject();

            result.put("draw", parameters.get(DatatablesConstant.DATATABLE_PARAM_DRAW)[0]);
            result.put("recordsTotal", totalNumberOfUser);
            result.put("recordsFiltered", userPage.getTotalRowCount());

            ArrayNode an = result.putArray("data");
            int num = Integer.valueOf(parameters.get("start")[0]) + 1;
            for (User c : userPage.getList()) {
                ObjectNode row = Json.newObject();
                String action = "";
                action += "&nbsp;<a href=\"users/" + c.id + "/edit" + "\"><i class =\"fa fa-edit\"></i>Edit</a>";
                action += "&nbsp;<a href=\"javascript:deleteDataUser(" + c.id + ");\"><i class=\"fa fa-remove\"></i>Delete</a>&nbsp;";

                row.put("0", num);
                row.put("1", c.name);
                row.put("2", c.email);
                row.put("3", c.phoneNumber);
                row.put("4", action);
                an.add(row);
                num++;
            }

            return ok(Json.toJson(result));

        } catch (NumberFormatException e) {
            Logger.debug("" + e.getMessage());
            return badRequest();
        }
    }

    public Result delete(Long id) {
        User user = User.find.byId(id);
        int status = 0;
        if (user != null) {
            user.delete();
            status = 1;
        }
        String message = status == 1 ? "User success deleted" : "User failed deleted";

        return ok(message);
    }
}

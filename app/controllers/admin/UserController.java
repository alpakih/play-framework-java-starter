package controllers.admin;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.PagedList;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import constan.DatatablesConstant;
import helper.Util;
import models.User;
import org.mindrot.jbcrypt.BCrypt;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.util.Map;

/**
 * User Controller
 * index,add,store,edit,update and delete method
 */
public class UserController extends Controller {

    /**
     * Method index
     *
     * @return view
     */
    public Result index() {

        return ok(views.html.admin.users.list.render());
    }

    /**
     * Method Add
     *
     * @return view form
     */
    public Result add() {
        Form<User> data = Form.form(User.class);
        return ok(views.html.admin.users.form.render("User", "Add", routes.UserController.store(), data));
    }

    /**
     * Method Edit
     *
     * @parameters  id
     * @return view form
     */

    public Result edit(Long id) {
        User user = User.find.byId(id);
        Form<User> userForm;
        if (user != null) {
            userForm = Form.form(User.class).fill(user);
        } else {
            flash("error", "failed to edit data");
            return redirect(routes.UserController.index());

        }
        return ok(views.html.admin.users.form.render("User", "Edit", routes.UserController.update(), userForm));
    }

    /**
     * Mehthod for update data
     *
     * @return result
     */
    public Result update() {

        try {
            Ebean.beginTransaction();
            //Binding data from form / view
            Form<User> form = Form.form(User.class).bindFromRequest();
            //Binding data as multipartFormData to get file
            Http.MultipartFormData multipartFormData = request().body().asMultipartFormData();
            Http.MultipartFormData.FilePart userImage = multipartFormData.getFile("image");

            //Validation
            if (form.hasErrors()) {
                flash("error", "User " + form.get().name + " failed update");
            } else if (!form.get().password.equals(form.get().confirmPassword)) {
                flash("error", "Wrong confirm password");
                return redirect(routes.UserController.index());
            } else {
                User formData = form.get();
                if (formData != null) {
                    formData.password = BCrypt.hashpw(form.get().password,BCrypt.gensalt());
                    if (userImage!=null){
                        formData.image=Util.saveImage(userImage);
                    }
                    formData.update();
                    Ebean.commitTransaction();

                    flash("success", "User "
                            + form.get().name + " success updated");
                } else {
                    flash("error", "Error!");
                }
            }
        } catch (Exception e) {
            flash("error", e.getMessage());
            Ebean.rollbackTransaction();
        } finally {
            Ebean.endTransaction();
        }
        return redirect(routes.UserController.index());
    }

    /**
     * Method store for save data
     *
     * @return result
     */
    public Result store() {
        try {
            Ebean.beginTransaction();
            //Binding data from form / view
            Form<User> userForm = Form.form(User.class).bindFromRequest();
            //Binding data as multipartFormData to get file
            Http.MultipartFormData multipartFormData = request().body().asMultipartFormData();
            Http.MultipartFormData.FilePart userImage = multipartFormData.getFile("image");
            //Validation
            if (userForm.hasErrors()) {
                flash("error", "User " + userForm.get().name + " failed created");
            } else if (!userForm.get().password.equals(userForm.get().confirmPassword)) {
                flash("error", "Wrong confirm password");
                return redirect(routes.UserController.index());
            }
            //Save to table
            User user = userForm.get();
            user.password = BCrypt.hashpw(userForm.get().password,BCrypt.gensalt());
            if (userImage!=null){
                user.image=Util.saveImage(userImage);
            }
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

    /**
     * Mehode listUser for list datatables
     *
     * @return json
     */
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

                case 2:
                    sortBy = "name";
                    break;
                case 3:
                    sortBy = "email";
                    break;
                case 4:
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
                row.put("1", "&nbsp;<img src=\"" + Util.BASE_IMAGE + "/avatars/" + c.image + "\" style=\"width:100px\">");
                row.put("2", c.name);
                row.put("3", c.email);
                row.put("4", c.phoneNumber);
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

    /**
     * Method delete for delete data
     *
     * @parameters id
     * @return result
     */
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

package controllers.admin;

import com.avaje.ebean.PagedList;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.User;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Map;

public class UserController extends Controller {
    private static final String DATATABLE_PARAM_DRAW = "draw";
    private static final String DATATABLE_PARAM_SEARCH = "search[value]";
    private static final String DATATABLE_PARAM_DISPLAY_LENGTH = "length";
    private static final String DATATABLE_PARAM_DISPLAY_START = "start";
    private static final String DATATABLE_PARAM_SORTING_COLUMN = "order[0][column]";
    private static final String DATATABLE_PARAM_SORTING_ORDER = "order[0][dir]";

    public Result index() {
        return ok(views.html.admin.users.list.render());
    }

    public Result listUser() {

        try {
            Map<String, String[]> parameters = request().queryString();

            int totalNumberOfUser = User.find.findRowCount();
            String searchParam = parameters.get(DATATABLE_PARAM_SEARCH)[0];
            int pageSize = Integer.parseInt(parameters.get(DATATABLE_PARAM_DISPLAY_LENGTH)[0]);

            int page = Integer.valueOf(parameters.get(DATATABLE_PARAM_DISPLAY_START)[0]) / pageSize;


            String sortBy = "id";
            String order = parameters.get(DATATABLE_PARAM_SORTING_ORDER)[0];

            Integer sortingColumnId = Integer.valueOf(parameters.get(DATATABLE_PARAM_SORTING_COLUMN)[0]);


            switch (sortingColumnId) {

                case 0:
                    sortBy = "name";
                    break;
                case 1:
                    sortBy = "email";
                    break;
                case 2:
                    sortBy = "phoneNumber";
                    break;

            }

            PagedList<User> userPage = User.page(page, pageSize, sortBy, order, searchParam);

            ObjectNode result = Json.newObject();

            result.put("draw", parameters.get(DATATABLE_PARAM_DRAW)[0]);
            result.put("recordsTotal", totalNumberOfUser);
            result.put("recordsFiltered", userPage.getTotalRowCount());

            ArrayNode an = result.putArray("data");

            for (User c : userPage.getList()) {
                ObjectNode row = Json.newObject();
                String action = "";
                action += "&nbsp;<a href=\"user/" + c.id + "/edit" + "\"><i data-feather =\"edit\"></i>Edit</a>";
                action += "&nbsp;<a href=\"javascript:deleteDataUser(" + c.id + ");\"><i data-feather=\"delete\"></i>Delete</a>&nbsp;";

                row.put("0", c.name);
                row.put("1", c.email);
                row.put("2", c.phoneNumber);
                row.put("3", action);
                an.add(row);
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

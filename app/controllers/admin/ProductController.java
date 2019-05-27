package controllers.admin;

import play.mvc.Controller;
import play.mvc.Result;

public class ProductController extends Controller {

    public Result index() {
        return ok(views.html.admin.product.list.render());
    }

}

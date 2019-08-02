package controllers.admin;

import helper.PDF;
import models.DetailsOrder;
import models.Order;
import play.mvc.Controller;
import play.mvc.Result;

public class ReportController extends Controller {
    public Result document() {
        return PDF.ok(views.html.example.render(Order.find.all()));
    }

    public Result documentDetail(){
        return PDF.ok(views.html.detail.render(DetailsOrder.find.all()));
    }
}

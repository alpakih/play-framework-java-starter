package controllers.admin;

import constan.DateHelper;
import helper.PDF;
import models.DetailsOrder;
import models.Order;
import play.mvc.Controller;
import play.mvc.Result;

public class ReportController extends Controller {
    public Result document() {
        DateHelper dateHelper = new DateHelper();
        return PDF.ok(views.html.example.render(Order.find.all(),dateHelper));
    }

    public Result documentDetail(Long id){
        return PDF.ok(views.html.detail.render(DetailsOrder.find.where().eq("order_id", id).findList()));
    }
}

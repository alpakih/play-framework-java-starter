package controllers;

import models.User;
import org.mindrot.jbcrypt.BCrypt;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;


public class LoginController extends Controller {

    public Result login(){
        if (session().get("id")==null){
            return ok(views.html.login.render());
        }else {
            return redirect(routes.HomeController.index());
        }
    }

    public Result submit(){
        DynamicForm form = Form.form().bindFromRequest();
        String email = form.get("email");
        String password = form.get("password");

        try{
            User user = User.find.where().eq("email", email)
                    .findUnique();

            if (user == null){
                flash("error", "Missing username/password");
                return redirect(routes.LoginController.login());
            }

            if (BCrypt.checkpw(password, user.password)){
                session().put("email", user.email);
                flash("success", "Login success");

                return redirect(routes.HomeController.index());

            }else{
                flash("error", "Wrong username/password");
                return redirect(routes.LoginController.login());
            }

        }catch (Exception e){
            flash("error", "User not found");
            return redirect(routes.LoginController.login());
        }
    }

    public Result logout(){
        session().clear();
        flash("success", "Logout success");
        return redirect(routes.LoginController.login());
    }
}

package controllers.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.User;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class LoginController extends Controller {

    public Result getLogin(){
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode json = request().body().asJson();
            JsonNode node = mapper.readValue(json.toString(), JsonNode.class);
            User user = User.find.where().eq("email", node.get("email").asText())
                    .where().eq("password", node.get("password").asText()).findUnique();

            return ok(Json.toJson(user));
        }catch (Exception e){
            return badRequest();
        }
    }

}

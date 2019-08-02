package controllers.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.User;
import play.mvc.Result;
import play.libs.Json;
import play.mvc.Controller;

import java.util.List;

public class UserController extends Controller {
    public Result getListUser(){
        List<User> user = User.find.all();
        return ok(Json.toJson(user));
    }

    public Result createUser(){
        ObjectMapper mapper = new ObjectMapper();

        try{
            JsonNode json = request().body().asJson();
            JsonNode node = mapper.readValue(json.toString(), JsonNode.class);
            User user = new User();

            user.name = node.get("name").asText();
            user.email = node.get("email").asText();
            user.password = node.get("password").asText();
            user.phoneNumber = node.get("phoneNumber").asText();

            user.save();

            return ok(Json.toJson(user));
        }catch (Exception e){
            return badRequest("create failed");
        }
    }

    public Result updateUser(Long id){
        ObjectMapper mapper = new ObjectMapper();
        try {
            User user = User.find.byId(id);
            JsonNode json = request().body().asJson();
            JsonNode node = mapper.readValue(json.toString(), JsonNode.class);

            if (user==null){
                return notFound("data not found");
            }else {
                user.name = node.get("name").asText();
                user.email = node.get("email").asText();
                user.password = node.get("password").asText();
                user.phoneNumber = node.get("phoneNumber").asText();

                user.update();

                return ok(Json.toJson(user));
            }
        }catch (Exception e){
            return badRequest("update failed");
        }
    }

    public Result deleteUser(Long id){
        User user = User.find.byId(id);
        try{
            if(user!=null){
                user.delete();
            }else {
                return ok("data not found");
            }
            return ok(Json.toJson(user));
        }catch (Exception e){
            return badRequest("delete failed");
        }
    }

}

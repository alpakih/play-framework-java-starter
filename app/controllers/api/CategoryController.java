package controllers.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Category;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

public class CategoryController extends Controller {
    public Result getCategoryList(){
        List<Category> category = Category.find.all();

        return ok(Json.toJson(category));
    }

    public Result createCategory(){

            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode json = request().body().asJson();
                JsonNode node = mapper.readValue(json.toString(), JsonNode.class);
                Category category = new Category();

                category.name = node.get("name").asText();

                category.save();

                return ok(Json.toJson(category));

            } catch (Exception e) {
                return badRequest();
            }
    }

    public Result deleteCategory(Long id){
        Category category = Category.find.byId(id);
        if(category!=null){
            category.delete();
        }else {
            return ok("data tidak ditemukan");
        }
        return ok(Json.toJson(category));
    }

    public Result updateCategory(Long id){
        ObjectMapper mapper = new ObjectMapper();
        try{
            JsonNode json = request().body().asJson();
            JsonNode node = mapper.readValue(json.toString(), JsonNode.class);
            Category category = Category.find.byId(id);

            category.name = node.get("name").asText();

            category.update();

            return ok(Json.toJson(category));
        }catch (Exception e){
            return badRequest();
        }

    }
}

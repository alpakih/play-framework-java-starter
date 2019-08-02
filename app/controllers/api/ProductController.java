package controllers.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Category;
import models.Product;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

public class ProductController extends Controller {

    public Result getProductList(){
        List<Product> product = Product.find.all();

        return ok(Json.toJson(product));
    }

    public Result createProduct(){
        ObjectMapper mapper = new ObjectMapper();
        try{
            JsonNode json = request().body().asJson();
            JsonNode node = mapper.readValue(json.toString(), JsonNode.class);
            Product product = new Product();

            product.photo=node.get("photo").asText();
            product.name=node.get("name").asText();
            product.price=node.get("price").asDouble();
            product.description=node.get("description").asText();
            product.category = Category.find.byId(Long.parseLong(node.get("categoryID").asText()));

            product.save();

            return ok(Json.toJson(product));
        }catch (Exception e){
            return badRequest("create failed");
        }
    }

    public Result updateProduct(Long id){
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode json = request().body().asJson();
            JsonNode node = mapper.readValue(json.toString(), JsonNode.class);
            Product product = Product.find.byId(id);

            if (product == null){
                return notFound("data not found");
            }else{
                product.photo = node.get("photo").asText();
                product.name = node.get("name").asText();
                product.price = node.get("price").asDouble();
                product.description = node.get("description").asText();
                product.category = Category.find.byId(Long.parseLong(node.get("categoryID").asText()));

                product.update();

                return ok(Json.toJson(product));
            }
        }catch (Exception e){
            return badRequest("update failed");
        }
    }


    public Result deleteProduct(Long id) {
        Product product = Product.find.byId(id);
        try {
            if (product != null) {
                product.delete();
            } else {
                return ok("data not found");
            }
            return ok(Json.toJson(product));
        }catch (Exception e){
            return badRequest("delete failed");
        }
    }


    public Result getProductById(Long id) {
        Product product = Product.find.byId(id);

        return ok(Json.toJson(product));
    }

}

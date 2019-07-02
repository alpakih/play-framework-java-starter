package controllers.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Product;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Currency;
import java.util.List;

public class ProductController extends Controller {

    public Result getProductList(){
        List<Product> product = Product.find.all();

        return ok(Json.toJson(product));
    }

    public Result deleteProduct(Long id){
        Product product = Product.find.byId(id);
        if(product !=null){
            product.delete();
        }else{
            return ok("data tidak ditemukan");
        }

        return ok(Json.toJson(product));
    }

    public Result updateProduct(Long id){
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode json = request().body().asJson();
            JsonNode node = mapper.readValue(json.toString(), JsonNode.class);
            Product product = Product.find.byId(id);

            if (product == null){
                return notFound("Product tidak ditemukan");
            }else{
                product.name=node.get("name").asText();
                product.price=node.get("price").asDouble();
                product.update();


                return ok(Json.toJson(product));
            }
        }catch (Exception e){
            return badRequest();
        }
    }

    public Result createProduct(){
        ObjectMapper mapper = new ObjectMapper();
        try{
            JsonNode json = request().body().asJson();
            JsonNode node = mapper.readValue(json.toString(), JsonNode.class);
            Product product = new Product();

            product.name=node.get("name").asText();
            product.price=node.get("price").asDouble();
            product.description=node.get("description").asText();

            product.save();
            return ok(Json.toJson(product));
        }catch (Exception e){
            return badRequest();
        }
    }
}

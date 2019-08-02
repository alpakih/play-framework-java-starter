package controllers.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Customer;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

public class CustomerController extends Controller {
    public Result getCustomerList(){
        List<Customer> customer = Customer.find.all();
        return ok(Json.toJson(customer));
    }

    public Result createCustomer(){
        ObjectMapper mapper = new ObjectMapper();
        try{
            JsonNode json = request().body().asJson();
            JsonNode node = mapper.readValue(json.toString(), JsonNode.class);
            Customer customer = new Customer();

            customer.name = node.get("name").asText();
            customer.address = node.get("address").asText();

            customer.save();

            return ok(Json.toJson(customer));
        }catch (Exception e){
            return badRequest("create failed");
        }
    }

    public Result updateCustomer(Long id){
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode json = request().body().asJson();
            JsonNode node = mapper.readValue(json.toString(), JsonNode.class);
            Customer customer = Customer.find.byId(id);

            if (customer==null){
                return notFound("data not found");
            }else {

                customer.name = node.get("name").asText();
                customer.address = node.get("address").asText();

                customer.update();

                return ok(Json.toJson(customer));
            }
        }catch (Exception e){
            return badRequest("update failed");
        }
    }

    public Result deleteCustomer(Long id){
        Customer customer = Customer.find.byId(id);
        try{
            if (customer!=null){
                customer.delete();
            }else {
                return ok("data not found");
            }
            return ok(Json.toJson(customer));
        }catch (Exception e){
            return badRequest("delete failed");
        }
    }
}

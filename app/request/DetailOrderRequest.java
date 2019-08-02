package request;

import com.fasterxml.jackson.annotation.JsonProperty;
import models.Order;

import java.io.Serializable;

public class DetailOrderRequest implements Serializable {

    @JsonProperty("orderID")
    private Long orderID;
    @JsonProperty("productID")
    private Long productID;
    @JsonProperty("price")
    private Double price;
    @JsonProperty("quantity")
    private Integer quantity;

    public Long getOrderID() {

        return orderID;
    }

    public void setOrderID(Long orderID) {

        this.orderID = orderID;
    }

    public Long getProductID() {

        return productID;
    }

    public void setProductID(Long productID) {

        this.productID = productID;
    }

    public Double getPrice() {

        return price;
    }

    public void setPrice(Double price) {

        this.price = price;
    }

    public Integer getQuantity() {

        return quantity;
    }

    public void setQuantity(Integer quantity) {

        this.quantity = quantity;
    }

    public DetailOrderRequest() {

    }
}

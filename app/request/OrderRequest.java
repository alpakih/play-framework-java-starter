package request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties
public class OrderRequest implements Serializable {

    @JsonProperty("id")
    private  Long id;

    @JsonProperty("no_transaksi")
    private String noTransaksi;
    @JsonProperty("customerID")
    private Long customerID;
    @JsonProperty("tanggal")
    private Date tanggal;

    @JsonProperty("detail")
    private List<DetailOrderRequest> detail = null;

    public OrderRequest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNoTransaksi() {
        return noTransaksi;
    }

    public void setNoTransaksi(String noTransaksi) {
        this.noTransaksi = noTransaksi;
    }

    public Long getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Long customerID) {
        this.customerID = customerID;
    }

    public List<DetailOrderRequest> getDetail() {
        return detail;
    }

    public void setDetail(List<DetailOrderRequest> detail) {

        this.detail = detail;
    }
}

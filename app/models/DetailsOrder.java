package models;

import com.avaje.ebean.Model;
import com.avaje.ebean.PagedList;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;

@Entity
@Table(name = "details_order")
public class DetailsOrder extends Model {

    @Id
    @SequenceGenerator(name = "name_details_order_seq", sequenceName = "details_order_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "name_details_order_seq")
    public Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    @JsonBackReference
    public Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    public Product product;

    @Column(name = "price")
    public Double price;

    @Column(name = "quantity")
    public int quantity;

    @Column(name = "sub_total")
    public Double sub_total;

    @Transient
    public Long productID;

    public static Finder<Long, DetailsOrder> find = new Finder<>(Long.class, DetailsOrder.class);

    public static PagedList<DetailsOrder> page(int page, int pageSize, String sortBy, String order, String filter) {
        return
                find.where()
                .ilike("product.name", "%" + filter + "%")
                .orderBy(sortBy + " " + order)
                .findPagedList(page, pageSize);
    }
}

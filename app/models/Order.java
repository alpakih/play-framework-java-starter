package models;

import com.avaje.ebean.Model;
import com.avaje.ebean.PagedList;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "order_product")
public class Order extends Model {
    @Id
    @SequenceGenerator(name = "name_order_seq", sequenceName = "order_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "name_order_seq")
    public Long id;

    @Column(name = "no_transaksi")
    public String no_transaksi;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    public Customer customer;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    public Date updateAt;

    @Column(name = "total")
    public Double total;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    public List<DetailsOrder> detail;

    @Transient
    public Long customerID;

    public static Finder<Long, Order> find = new Finder<>(Long.class, Order.class);

    public static PagedList<Order> page(int page, int pageSize, String sortBy, String order, String filter) {
        return
                find.where()
                .ilike("customer.name", "%" + filter + "%")
                .orderBy(sortBy + " " + order)
                .findPagedList(page, pageSize);
    }
}

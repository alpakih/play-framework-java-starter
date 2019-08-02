package models;

import com.avaje.ebean.Model;
import com.avaje.ebean.PagedList;

import javax.persistence.*;

@Entity
@Table(name = "customer")
public class Customer extends Model {
    @Id
    @SequenceGenerator(name = "name_customer_seq", sequenceName = "customer_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "name_customer_seq")
    public Long id;

    @Column(name = "name")
    public String name;

    @Column(name = "address")
    public String address;

    public static Finder<Long, Customer> find = new Finder<>(Long.class, Customer.class);

    public static PagedList<Customer> page(int page, int pageSize, String sortBy, String order, String filter) {
        return
                find.where()
                        .ilike("name", "%" + filter + "%")
                        .orderBy(sortBy + " " + order)
                        .findPagedList(page, pageSize);
    }
}

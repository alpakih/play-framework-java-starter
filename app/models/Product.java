package models;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Model;
import com.avaje.ebean.PagedList;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "product")
public class Product extends Model {

    @Id
    @SequenceGenerator(name = "name_product_seq", sequenceName = "product_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "name_product_seq")
    public Long id;

    @Column(name = "image", columnDefinition = "TEXT")
    public String photo;

    @Column(name = "name")
    public String name;

    @Column(name = "price")
    public Double price;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    public Category category;

    @Column(name = "description")
    public String description;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    public Date updatedAt;

    @Transient
    public Long categoryID;

    public static Finder<Long, Product> find = new Finder<>(Long.class, Product.class);


    public static PagedList<Product> page(int page, int pageSize, String sortBy, String order, String filter) {
        return
                find.fetch("category")
                        .where()
                        .or(
                                Expr.ilike("name", "%" + filter + "%"),
                                Expr.ilike("description", "%" + filter + "%")

                        )
                        .orderBy(sortBy + " " + order)
                        .findPagedList(page, pageSize);
    }

}

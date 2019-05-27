package models;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "product")
public class Product extends Model {
    @Id
    @SequenceGenerator(name = "name_product_seq", sequenceName = "product_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "name_product_seq")
    public Long id;

    @Column(name = "photo", columnDefinition = "TEXT")
    public String photo;

    @Column(name = "name")
    public String name;

    @Column(name = "price")
    public Double price;

    @Column(name = "description")
    public String description;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    public Date updatedAt;

    public static Finder<Long, Product> find = new Finder<>(Long.class, Product.class);

}

package models;

import com.avaje.ebean.Model;
import com.avaje.ebean.PagedList;

import javax.persistence.*;

@Entity
@Table(name = "category")
public class Category extends Model {

    @Id
    @SequenceGenerator(name = "name_category_seq", sequenceName = "category_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "name_category_seq")
    public Long id;

    @Column (name = "name")
    public String name;

    public static Finder<Long, Category> find = new Finder<>(Long.class, Category.class);

    public static PagedList<Category> page(int page, int pageSize, String sortBy, String order, String filter) {
        return
                find.where()
                        .ilike("name", "%" + filter + "%")
                        .orderBy(sortBy + " " + order)
                        .findPagedList(page, pageSize);
    }
}

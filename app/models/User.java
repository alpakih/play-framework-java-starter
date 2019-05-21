package models;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Model;
import com.avaje.ebean.PagedList;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "users")
public class User extends Model {

    @Id
    @SequenceGenerator(name = "name_user_seq", sequenceName = "user_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "name_user_seq")
    public Long id;

    @Constraints.Pattern(value = "^[a-zA-Z \\\\._\\\\-]+$", message = "name hanya diperbolehkan abjad")
    @Constraints.MinLength(value = 2, message = "Minimum value 2")
    @Constraints.MaxLength(value = 45, message = "Maximum value 50")
    public String name;

    @Column(name = "email", unique = true)
    public String email;

    @Column(name = "password")
    public String password;

    @Constraints.MinLength(value = 11, message = "Minimum value 11")
    @Constraints.MaxLength(value = 12, message = "Maximum value 12")
    @Column(name = "phone_number", nullable = false, length = 45)
    public String phoneNumber;

    @Column(name = "image", columnDefinition = "TEXT")
    public String image;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    public Date updatedAt;

    public static Finder<Long, User> find = new Finder<>(Long.class, User.class);


    /**
     * Return a page of computer
     *
     * @param page     Page to display
     * @param pageSize Number of computers per page
     * @param sortBy   Computer property used for sorting
     * @param order    Sort order (either or asc or desc)
     * @param filter   Filter applied on the name column
     */
    public static PagedList<User> page(int page, int pageSize, String sortBy, String order, String filter) {
        return
                find.where()
                        .or(
                                Expr.ilike("name", "%" + filter + "%"),
                                Expr.or(
                                        Expr.ilike("email", "%" + filter + "%"),
                                        Expr.ilike("phoneNumber", "%" + filter + "%")
                                )
                        )
                        .orderBy(sortBy + " " + order)
                        .findPagedList(page, pageSize);
    }
}

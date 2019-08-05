package constan;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {

    public String convertDate(Date date){

        SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");
        return dt1.format(date);
    }
}

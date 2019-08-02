package helper;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import play.Play;
import play.mvc.Http;
import play.mvc.Result;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
    public static final String BASE_IMAGE = Play.application().configuration().getString("application.base.image");

    public static String saveImage(Http.MultipartFormData.FilePart filePart, String type)
            throws IOException {

        String fileName = null;
        String generateImageId = null;
        if (filePart != null) {
            fileName = filePart.getFilename();
            File file = filePart.getFile();
            generateImageId = RandomStringUtils.random(10, false, true);
            FileUtils.moveFile(file, getFileUser(fileName, generateImageId, type));
        }
        return generateImageId + "." + FilenameUtils.getExtension(fileName);
    }

    private static File getFileUser(String fileName, String id, String type) {
        return new File("public/images/upload/" + type,id + "." + FilenameUtils.getExtension(fileName));
    }

    public static String convertDate(Date date){

        SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");
        return dt1.format(date);
    }

    public static Double sumTotal(Long orderId) {
        String sql = "SELECT SUM(sub_total) as total FROM details_order Where order_id = '" + orderId + "'";

        SqlQuery sqlQuery = Ebean.createSqlQuery(sql);
        SqlRow row = sqlQuery.findUnique();
        return row.getDouble("total");
    }

}

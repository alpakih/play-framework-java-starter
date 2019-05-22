package helper;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import play.Play;
import play.mvc.Http;

import java.io.File;
import java.io.IOException;

public class Util {
    public static final String BASE_IMAGE = Play.application().configuration().getString("application.base.image");

    public static String saveImage( Http.MultipartFormData.FilePart filePart)
            throws IOException {

        String fileName = null;
        String generateImageId = null;
        if (filePart != null) {
            fileName = filePart.getFilename();
            File file = filePart.getFile();
            generateImageId = RandomStringUtils.random(10, false, true);
            FileUtils.moveFile(file, getFileUser(fileName, generateImageId));

        }
        return generateImageId + "." + FilenameUtils.getExtension(fileName);
    }

    private static File getFileUser(String fileName, String id) {
        return new File("public/images/upload/avatars",
                id + "." + FilenameUtils.getExtension(fileName));
    }
}

package controllers.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import helper.Util;
import models.User;
import org.apache.commons.lang3.RandomStringUtils;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.xml.bind.DatatypeConverter;
import java.io.*;

public class UploadController extends Controller {

    public Result uploadImage() throws IOException {

        ObjectNode result = Json.newObject();

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart picture = body.getFile("image");

        User user = new User();
        if (picture != null) {
            user.image = Util.saveImage(picture, "avatars");
            result.put("name", picture.getFilename());
            result.put("status", "success");
            result.put("message", "File uploaded!");
            return ok(Json.toJson(result));
        } else {
            result.put("status", "error");
            result.put("message", "File cannot be uploaded!");
            return badRequest(result);
        }

    }

    public Result uploadImage2() throws IOException {
        JsonNode json = request().body().asJson();

        String base64String = json.get("image").asText();

        String[] strings = base64String.split(",");
        String extension;
        switch (strings[0]) {//check image's extension
            case "data:image/jpeg;base64":
                extension = "jpeg";
                break;
            case "data:image/png;base64":
                extension = "png";
                break;
            default://should write cases for more images types
                extension = "jpg";
                break;
        }

        //convert base64 string to binary data
        byte[] data = DatatypeConverter.parseBase64Binary(strings[1]);
        String generateImageId = RandomStringUtils.random(10, false, true);

        String path = "public/images/upload/" + generateImageId + "." + extension;
        File file = new File(path);
        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
        outputStream.write(data);

        return ok(Json.toJson("uploaded success "+file.getName()));
    }
}

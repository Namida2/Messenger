package tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

import static tools.Const.ERROR;

public class Base64 {
    public static String toBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return bytes.length > 1048487 ? ERROR : android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT);
    }

    public static Bitmap fromBase64(String image) {
        byte[] decodedString = android.util.Base64.decode(image, android.util.Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}

package com.simboorm.simboorm;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class Converter {
    private Converter() {}

    public static String imageToString(MultipartFile image) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("data:image/png;base64,");
        stringBuilder.append(StringUtils.newStringUtf8(Base64.encodeBase64(image.getBytes())));
        return stringBuilder.toString();
    }

    public static int stringToInt(String idText, int fallback) {
        try {
            return Integer.parseInt(idText);
        } catch(Exception ignored) {
            return fallback;
        }
    }
}

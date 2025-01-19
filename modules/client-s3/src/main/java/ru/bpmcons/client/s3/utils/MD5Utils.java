package ru.bpmcons.client.s3.utils;

import lombok.experimental.UtilityClass;

import javax.validation.constraints.NotNull;
import java.util.Base64;

@UtilityClass
public class MD5Utils {
    private static final int MD5_DIGEST_LEN = 16;

    public static boolean isValidHash(@NotNull String hash) {
        try {
            byte[] decoded = Base64.getDecoder().decode(hash);
            return decoded.length == MD5_DIGEST_LEN;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

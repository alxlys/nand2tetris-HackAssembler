package utils;

import org.apache.commons.lang3.StringUtils;

public class BinaryUtil {

    public static String toBinaryString(Long value) {
        return StringUtils.leftPad(
                Long.toBinaryString(value), 16, "0");
    }

}

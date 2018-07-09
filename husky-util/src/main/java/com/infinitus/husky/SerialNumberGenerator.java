package com.infinitus.husky;

import java.util.Date;
import java.util.UUID;

public class SerialNumberGenerator {

    public static String dateFormatGenerator() {
        return DateFormatUtils.formatDate2yyyyMMddHHmmssSSS(new Date());
    }

    public static String uuIdGenerator() {
        return UUID.randomUUID().toString();
    }
}

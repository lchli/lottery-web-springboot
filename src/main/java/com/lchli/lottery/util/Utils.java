package com.lchli.lottery.util;

import org.springframework.util.StringUtils;

import java.util.UUID;

public class Utils {

    public static boolean isEmpty(Object str) {
        return StringUtils.isEmpty(str);
    }

    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    public static String buildFileDownloadUrl(String fileId) {

        return String.format("%s/api/public/file/download/%s", Constants.HOST, fileId);

    }
}

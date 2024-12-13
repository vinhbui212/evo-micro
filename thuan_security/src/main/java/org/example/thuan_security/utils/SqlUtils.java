package org.example.thuan_security.utils;

public class SqlUtils {

    public static String encodeKeyword(String keyword) {
        if (keyword == null) {
            return "";
        }
        keyword = keyword.replaceAll("([%_])", "\\\\$1"); // Escape các ký tự %, _
        return "%" + keyword.toLowerCase() + "%";  // Thêm dấu % để tìm kiếm kiểu LIKE
    }
}


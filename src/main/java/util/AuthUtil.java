package util;

import javax.servlet.http.HttpServletRequest;


public class AuthUtil {


    public static boolean isCustomerCookie(HttpServletRequest request) {
        String cookieHeader = request.getHeader("Cookie");
        if (cookieHeader == null) {
            return false;
        }
        // Header chỉ chứa một cookie: "name=value"
        String trimmedHeader = cookieHeader.trim();
        String[] parts = trimmedHeader.split("=", 2);
        return "customer".equals(parts[0]);
    }


    public static boolean isAdminCookie(HttpServletRequest request) {
        String cookieHeader = request.getHeader("Cookie");
        if (cookieHeader == null) {
            return false;
        }
        // Header chỉ chứa một cookie: "name=value"
        String trimmedHeader = cookieHeader.trim();
        String[] parts = trimmedHeader.split("=", 2);
        return "admin".equals(parts[0]);
    }


    public static String getCookieValue(HttpServletRequest request) {
        String cookieHeader = request.getHeader("Cookie");
        if (cookieHeader == null) {
            return null;
        }

        // Header chỉ chứa một cookie: "name=value"
        String trimmedHeader = cookieHeader.trim();
        String[] parts = trimmedHeader.split("=", 2);
        return parts.length == 2 ? parts[1] : null;
    }
}
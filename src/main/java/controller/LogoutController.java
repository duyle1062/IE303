package controller;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/logout")
public class LogoutController extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            // Tạo cookie với tên "user" và đặt Max-Age = 0 để hủy
            Cookie adminCookie = new Cookie("admin", "");
            adminCookie.setHttpOnly(true);
            adminCookie.setPath("/");
            adminCookie.setMaxAge(0);
            resp.addCookie(adminCookie);

            // Xóa cookie "customer"
            Cookie customerCookie = new Cookie("customer", "");
            customerCookie.setHttpOnly(true);
            customerCookie.setPath("/");
            customerCookie.setMaxAge(0);
            resp.addCookie(customerCookie);

            // Trả về phản hồi thành công
            sendJsonResponse(resp, "Logout successful");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendJsonResponse(resp, "Error during logout: " + e.getMessage());
        }
    }
}

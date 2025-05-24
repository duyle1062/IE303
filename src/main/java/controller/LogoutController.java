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
            Cookie userCookie = new Cookie("user", "");
            userCookie.setHttpOnly(true); // Giữ HttpOnly như khi thiết lập
            userCookie.setPath("/"); // Đảm bảo Path khớp với cookie gốc
            userCookie.setMaxAge(0); // Hủy cookie ngay lập tức
            resp.addCookie(userCookie);

            // Trả về phản hồi thành công
            sendJsonResponse(resp, "Logout successful");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendJsonResponse(resp, "Error during logout: " + e.getMessage());
        }
    }
}

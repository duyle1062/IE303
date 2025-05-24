package controller.admin;

import model.CustomerModel;
import service.AdminService;
import model.AdminModel;
import controller.BaseServlet;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import com.google.gson.Gson;
import java.io.BufferedReader;

@WebServlet("/api/admin/login")
public class AdminLoginController extends BaseServlet {
    private static final Gson gson = new Gson();
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            BufferedReader reader = req.getReader();
            CustomerModel cus = gson.fromJson(reader, CustomerModel.class);

            String username = cus.getUsername();
            String password = cus.getPassword();

            // Kiểm tra dữ liệu đầu vào
            if (username == null || password == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendJsonResponse(resp, "Missing required fields: username or password");
                return;
            }

            // Gọi AdminService để lấy tất cả admin
            AdminService adminService = new AdminService();
            Optional<AdminModel> foundAdmin = adminService.getAllAdmins().stream()
                    .filter(admin -> admin.getUsername().equals(username) && admin.getPassword().equals(password))
                    .findFirst();

            // Kiểm tra thông tin đăng nhập
            if (!foundAdmin.isPresent()) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                sendJsonResponse(resp, "Invalid username or password");
                return;
            }

            // Thêm cookie user=Admin
            Cookie userCookie = new Cookie("user", "Admin");
            userCookie.setHttpOnly(true); // Bảo mật, ngăn JavaScript truy cập
            userCookie.setPath("/"); // Có hiệu lực trên toàn ứng dụng
            userCookie.setMaxAge(60 * 60); // Hết hạn sau 1 giờ
            resp.addCookie(userCookie);

            // Trả về phản hồi thành công
            sendJsonResponse(resp, "Admin login successful for user: " + username);
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendJsonResponse(resp, "Error during admin login: " + e.getMessage());
        }
    }
}

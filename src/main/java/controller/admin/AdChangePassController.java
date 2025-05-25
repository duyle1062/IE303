package controller.admin;

import controller.BaseServlet;
import model.AdminModel;
import service.ChangePasswordService;
import util.AuthUtil;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@WebServlet("/api/admin/change-password")
public class AdChangePassController extends BaseServlet {
    private final ChangePasswordService changePasswordService = new ChangePasswordService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            // Kiểm tra cookie admin
            if (!AuthUtil.isAdminCookie(req)) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                sendJsonResponse(resp, "Admin cookie not found");
                return;
            }

            // Lấy admin_id từ cookie
            String adminIdStr = AuthUtil.getCookieValue(req);
            int adminId;

            try {
                adminId = Integer.parseInt(adminIdStr);
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendJsonResponse(resp, "Invalid admin ID in cookie");
                return;
            }

            // Parse JSON body
            AdminModel ad = parseJsonRequest(req, AdminModel.class);
            String newPassword = ad.getPassword();

            if (newPassword == null || newPassword.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendJsonResponse(resp, "New password is required");
                return;
            }

            // Thay đổi mật khẩu
            boolean success = changePasswordService.changeAdminPassword(adminId, newPassword);
            Map<String, String> response = new HashMap<>();
            response.put("message", success ? "Admin password changed successfully" : "Admin not found");

            sendJsonResponse(resp, response);
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendJsonResponse(resp, "Invalid request: " + e.getMessage());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendJsonResponse(resp, "Error changing admin password: " + e.getMessage());
        }
    }
}

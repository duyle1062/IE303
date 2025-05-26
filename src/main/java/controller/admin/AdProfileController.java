package controller.admin;

import controller.BaseServlet;
import model.AdminModel;
import DAO.AdminDAO;
import util.AuthUtil;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@WebServlet("/api/admin/profile")
public class AdProfileController extends BaseServlet {
    private final AdminDAO adminDAO = new AdminDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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

            AdminDAO adminDAO = new AdminDAO();
            Optional<AdminModel> foundAdmin = adminDAO.getAllAdmins().stream()
                    .filter(admin -> String.valueOf(admin.getAdminId()).equals(adminIdStr))
                    .findFirst();

            if (foundAdmin.isPresent()) {
                AdminModel admin = foundAdmin.get();
                model.AdminDTO adminDTO = new model.AdminDTO(admin); // Bỏ password
                // Trả về dữ liệu admin dưới dạng JSON
                sendJsonResponse(resp, adminDTO);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                Map<String, String> response = new HashMap<>();
                response.put("message", "Admin not found");
                sendJsonResponse(resp, response);
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendJsonResponse(resp, "Error retrieving admin profile: " + e.getMessage());
        }
    }
}

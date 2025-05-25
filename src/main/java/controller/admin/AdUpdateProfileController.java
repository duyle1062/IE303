package controller.admin;

import controller.BaseServlet;
import model.AdminModel;
import service.UpdateProfileService;
import util.AuthUtil;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@WebServlet("/api/admin/update-profile")
public class AdUpdateProfileController extends BaseServlet {
    private final UpdateProfileService updateProfileService = new UpdateProfileService();

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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
            if (ad == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendJsonResponse(resp, "Invalid request body");
                return;
            }

            // Lấy các giá trị từ AdminModel
            String username = ad.getUsername();
            String email = ad.getEmail();
            String firstName = ad.getFirstName();
            String lastName = ad.getLastName();
            String phone = ad.getPhone();
            String address = ad.getAddress();
            java.util.Date hireDate = ad.getHireDate();
            String department = ad.getDepartment();

            // Kiểm tra xem có ít nhất một trường không null để cập nhật
            if (username == null && email == null && firstName == null && lastName == null &&
                    phone == null && address == null && hireDate == null && department == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendJsonResponse(resp, "No fields provided for update");
                return;
            }

            // Cập nhật profile
            boolean success = updateProfileService.updateAdminProfile(adminId, ad);
            Map<String, String> response = new HashMap<>();
            response.put("message", success ? "Admin profile updated successfully" : "Admin not found");
            sendJsonResponse(resp, response);
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendJsonResponse(resp, "Invalid request: " + e.getMessage());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendJsonResponse(resp, "Error updating admin profile: " + e.getMessage());
        }
    }
}

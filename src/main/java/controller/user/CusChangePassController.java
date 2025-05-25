package controller.user;

import controller.BaseServlet;
import model.CustomerModel;
import service.ChangePasswordService;
import util.AuthUtil;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@WebServlet("/api/customer/change-password")
public class CusChangePassController extends BaseServlet {
    private final ChangePasswordService changePasswordService = new ChangePasswordService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            // Kiểm tra cookie customer
            if (!AuthUtil.isCustomerCookie(req)) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                sendJsonResponse(resp, "Customer cookie not found");
                return;
            }

            // Lấy customer_id từ cookie
            String customerIdStr = AuthUtil.getCookieValue(req);
            int customerId;
            try {
                assert customerIdStr != null;
                customerId = Integer.parseInt(customerIdStr);
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendJsonResponse(resp, "Invalid customer ID in cookie");
                return;
            }

            // Parse JSON body
            CustomerModel cus = parseJsonRequest(req, CustomerModel.class);
            String newPassword = cus.getPassword();

            if (newPassword == null || newPassword.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendJsonResponse(resp, "New password is required");
                return;
            }

            // Thay đổi mật khẩu
            boolean success = changePasswordService.changeCustomerPassword(customerId, newPassword);
            Map<String, String> response = new HashMap<>();
            response.put("message", success ? "Customer password changed successfully" : "Customer not found");

            sendJsonResponse(resp, response);
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendJsonResponse(resp, "Invalid request: " + e.getMessage());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendJsonResponse(resp, "Error changing customer password: " + e.getMessage());
        }
    }
}

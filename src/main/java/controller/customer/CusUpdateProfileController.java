package controller.customer;

import controller.BaseServlet;
import model.CustomerModel;
import service.UpdateProfileService;
import util.AuthUtil;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet for updating customer profile data based on customer_id from cookie.
 */
@WebServlet("/api/customer/update-profile")
public class CusUpdateProfileController extends BaseServlet {
    private final UpdateProfileService updateProfileService = new UpdateProfileService();

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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
                customerId = Integer.parseInt(customerIdStr);
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendJsonResponse(resp, "Invalid customer ID in cookie");
                return;
            }

            // Parse JSON body
            CustomerModel customer = parseJsonRequest(req, CustomerModel.class);
            if (customer == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendJsonResponse(resp, "Invalid request body");
                return;
            }

            // Lấy các giá trị từ CustomerModel
            String username = customer.getUsername();
            String email = customer.getEmail();
            String firstName = customer.getFirstName();
            String lastName = customer.getLastName();
            String phone = customer.getPhone();
            String address = customer.getAddress();

            // Kiểm tra xem có ít nhất một trường không null để cập nhật
            if (username == null && email == null && firstName == null && lastName == null && phone == null && address == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendJsonResponse(resp, "No fields provided for update");
                return;
            }

            // Cập nhật profile
            boolean success = updateProfileService.updateCustomerProfile(customerId, customer);
            Map<String, String> response = new HashMap<>();
            response.put("message", success ? "Customer profile updated successfully" : "Customer not found");
            sendJsonResponse(resp, response);
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendJsonResponse(resp, "Invalid request: " + e.getMessage());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendJsonResponse(resp, "Error updating customer profile: " + e.getMessage());
        }
    }
}

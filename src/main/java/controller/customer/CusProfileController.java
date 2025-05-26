package controller.customer;

import controller.BaseServlet;
import model.CustomerModel;
import model.CustomerDTO;
import DAO.CustomerDAO;
import util.AuthUtil;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@WebServlet("/api/customer/profile")
public class CusProfileController extends BaseServlet {
    private final CustomerDAO customerDAO = new CustomerDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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

            // Lấy thông tin customer từ cơ sở dữ liệu
            // Gọi CustomerService để lấy tất cả khách hàng
            CustomerDAO customerDAO = new CustomerDAO();
            Optional<CustomerModel> foundCustomer = customerDAO.getAllCustomers().stream()
                    .filter(customer -> String.valueOf(customer.getCustomerId()).equals(customerIdStr))
                    .findFirst();


            if (foundCustomer.isPresent()) {
                CustomerModel customer = foundCustomer.get();
                CustomerDTO customerDTO = new CustomerDTO(customer); // Bỏ password
                // Trả về dữ liệu customer dưới dạng JSON
                sendJsonResponse(resp, customerDTO);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                Map<String, String> response = new HashMap<>();
                response.put("message", "Customer not found");
                sendJsonResponse(resp, response);
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendJsonResponse(resp, "Error retrieving customer profile: " + e.getMessage());
        }
    }
}

package controller.customer;

import controller.BaseServlet;
import DAO.CustomerDAO;
import model.CustomerModel;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@WebServlet("/api/customer/login")
public class CusLoginController extends BaseServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            CustomerModel cus = parseJsonRequest(req, CustomerModel.class);

            String username = cus.getUsername();
            String password = cus.getPassword();


            // Kiểm tra dữ liệu đầu vào
            if (username == null || password == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendJsonResponse(resp, "Missing required fields: username or password");
                return;
            }

            // Gọi CustomerService để lấy tất cả khách hàng
            CustomerDAO customerDAO = new CustomerDAO();
            Optional<CustomerModel> foundCustomer = customerDAO.getAllCustomers().stream()
                    .filter(customer -> customer.getUsername().equals(username) && customer.getPassword().equals(password))
                    .findFirst();

            // Kiểm tra thông tin đăng nhập
            if (!foundCustomer.isPresent()) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                sendJsonResponse(resp, "Invalid username or password");
                return;
            }

            int cus_id = foundCustomer.get().getCustomerId();

            // Thêm cookie user=Customer
            Cookie userCookie = new Cookie("customer", String.valueOf(cus_id));
            userCookie.setHttpOnly(true); // Bảo mật, ngăn JavaScript truy cập
            userCookie.setPath("/"); // Có hiệu lực trên toàn ứng dụng
            userCookie.setMaxAge(60 * 60); // Hết hạn sau 1 giờ (3600 giây)
            resp.addCookie(userCookie);

            // Trả về phản hồi thành công
            sendJsonResponse(resp, "Login successful for user: " + username);
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendJsonResponse(resp, "Error during login: " + e.getMessage());
        }
    }
}

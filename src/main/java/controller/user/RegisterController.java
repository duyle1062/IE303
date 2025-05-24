package controller.user;

import controller.BaseServlet;
import model.CustomerModel;
import service.CustomerService;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

import com.google.gson.Gson;


@WebServlet("/api/register")
public class RegisterController extends BaseServlet {
    private static final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            BufferedReader reader = req.getReader();
            CustomerModel cus = gson.fromJson(reader, CustomerModel.class);
            System.out.println(cus.getEmail());

            String username = cus.getUsername();
            String password = cus.getPassword();
            String email = cus.getEmail();
            String firstName = cus.getFirstName();
            String lastName = cus.getLastName();
            String phone = cus.getPhone();
            String address = cus.getAddress();


            // Gọi CustomerService để đăng ký
            CustomerService CustomerService = new CustomerService();
            CustomerService.registerCustomer(username, password, email, firstName, lastName, phone, address);

            // Trả về phản hồi thành công
            sendJsonResponse(resp, "Customer registered successfully");
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendJsonResponse(resp, "Error registering customer: " + e.getMessage());
        }
    }
}

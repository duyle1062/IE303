package controller.customer;

import controller.BaseServlet;
import model.ReservationRequest;
import service.ReservationService;
import util.AuthUtil;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/customer/reservation")
public class CusReservController extends BaseServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!AuthUtil.isCustomerCookie(req)) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Customer cookie not found");
            sendJsonResponse(resp, error);
            return;
        }

        // Get customer_id from cookie
        String customerIdStr = AuthUtil.getCookieValue(req);
        int customerId;
        try {
            assert customerIdStr != null;
            customerId = Integer.parseInt(customerIdStr);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid customer ID in cookie");
            sendJsonResponse(resp, error);
            return;
        }

        ReservationRequest request = parseJsonRequest(req, ReservationRequest.class);
        if (request.getBookName() == null || request.getBookName().trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            Map<String, String> error = new HashMap<>();
            error.put("error", "bookName is required");
            sendJsonResponse(resp, error);
            return;
        }

        ReservationService reservationService = new ReservationService();
        Map<String, Object> response = reservationService.createReservation(customerId, request.getBookName());
        sendJsonResponse(resp, response);
    }
}
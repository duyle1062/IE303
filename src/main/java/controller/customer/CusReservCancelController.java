package controller.customer;

import controller.BaseServlet;
import model.ReservCancelRequest;
import DAO.ReservationDAO;
import util.AuthUtil;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/customer/reservation/cancel")
public class CusReservCancelController extends BaseServlet {
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

        ReservCancelRequest request = parseJsonRequest(req, ReservCancelRequest.class);
        if (request.getReservationId() <= 0) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            Map<String, String> error = new HashMap<>();
            error.put("error", "reservationId is required");
            sendJsonResponse(resp, error);
            return;
        }

        ReservationDAO reservationDAO = new ReservationDAO();
        Map<String, Object> response = reservationDAO.cancelReservation(customerId, request.getReservationId());
        sendJsonResponse(resp, response);
    }
}
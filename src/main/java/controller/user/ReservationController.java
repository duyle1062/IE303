package controller.user;

import controller.BaseServlet;
import model.ReservationRequest;
import service.ReservationService;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/reservation")
public class ReservationController extends BaseServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ReservationRequest request = parseJsonRequest(req, ReservationRequest.class);
        if (request.getCustomerId() <= 0 || request.getBookName() == null || request.getBookName().trim().isEmpty()) {
            resp.setStatus(400);
            Map<String, String> error = new HashMap<>();
            error.put("error", "customerId and bookName are required");
            sendJsonResponse(resp, error);
            return;
        }
        ReservationService reservationService = new ReservationService();
        Map<String, Object> response = reservationService.createReservation(request.getCustomerId(), request.getBookName());
        sendJsonResponse(resp, response);
    }
}
package controller.customer;

import controller.BaseServlet;
import model.BorrowHistoryResponse;
import service.BorrowingService;
import util.AuthUtil;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/customer/borrowing/history")
public class CusBorrowHistoryController extends BaseServlet {
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

        BorrowingService borrowingService = new BorrowingService();
        List<BorrowHistoryResponse> history = borrowingService.getBorrowingHistory(customerId);
        sendJsonResponse(resp, history);
    }
}
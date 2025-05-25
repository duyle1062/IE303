package controller.user;

import controller.BaseServlet;
import model.BorrowHistoryRequest;
import model.BorrowHistoryResponse;
import service.BorrowingService;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/borrowing/history")
public class BorrowHistoryController extends BaseServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BorrowHistoryRequest request = parseJsonRequest(req, BorrowHistoryRequest.class);
        if (request.getCustomerId() <= 0) {
            resp.setStatus(400);
            Map<String, String> error = new HashMap<>();
            error.put("error", "customerId is required");
            sendJsonResponse(resp, error);
            return;
        }
        BorrowingService borrowingService = new BorrowingService();
        List<BorrowHistoryResponse> history = borrowingService.getBorrowingHistory(request.getCustomerId());
        sendJsonResponse(resp, history);
    }
}
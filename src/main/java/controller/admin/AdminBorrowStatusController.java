package controller.admin;

import controller.BaseServlet;
import model.BorrowStatusRequest;
import service.BorrowingService;
import util.AuthUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/admin/borrow/status")
public class AdminBorrowStatusController extends BaseServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!AuthUtil.isAdminCookie(req)) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            sendJsonResponse(resp, "Admin cookie not found");
            return;
        }

        // Lấy admin_id từ cookie
        String adminIdStr = AuthUtil.getCookieValue(req);
        int adminId;

        try {
            assert adminIdStr != null;
            adminId = Integer.parseInt(adminIdStr);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendJsonResponse(resp, "Invalid admin ID in cookie");
            return;
        }

        BorrowStatusRequest request = parseJsonRequest(req, BorrowStatusRequest.class);
        if (request.getBorrowId() <= 0 || request.getStatus() == null ||
                !request.getStatus().matches("borrowed|returned|overdue")) {
            resp.setStatus(400);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Valid borrowId and status (borrowed, returned, overdue) are required");
            sendJsonResponse(resp, error);
            return;
        }
        if ("returned".equals(request.getStatus()) &&
                (request.getReturnDate() == null || !request.getReturnDate().matches("\\d{4}-\\d{2}-\\d{2}"))) {
            resp.setStatus(400);
            Map<String, String> error = new HashMap<>();
            error.put("error", "returnDate (yyyy-MM-dd) is required for status 'returned'");
            sendJsonResponse(resp, error);
            return;
        }
        BorrowingService borrowingService = new BorrowingService();
        Map<String, String> response = borrowingService.updateBorrowingStatus(request);
        sendJsonResponse(resp, response);
    }
}
package controller.admin;

import controller.BaseServlet;
import model.BorrowingAddRequest;
import DAO.BorrowingDAO;
import util.AuthUtil;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/admin/borrowing/add")
public class AdBorrowAddController extends BaseServlet {
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

        BorrowingAddRequest request = parseJsonRequest(req, BorrowingAddRequest.class);
        if (request.getCustomerId() <= 0 ||
                request.getBorrowDate() == null || !request.getBorrowDate().matches("\\d{4}-\\d{2}-\\d{2}") ||
                request.getDueDate() == null || !request.getDueDate().matches("\\d{4}-\\d{2}-\\d{2}") ||
                request.getBorrowingFee() < 0 || request.getOverdueFee() < 0) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            Map<String, String> error = new HashMap<>();
            error.put("error", "return Valid customerId, borrowDate (yyyy-MM-dd), dueDate (yyyy-MM-dd), borrowingFee, and overdueFee are required");
            sendJsonResponse(resp, error);
            return;
        }

        BorrowingDAO borrowingDAO = new BorrowingDAO();
        Map<String, Object> response = borrowingDAO.addBorrowing(adminId, request);
        sendJsonResponse(resp, response);
    }
}
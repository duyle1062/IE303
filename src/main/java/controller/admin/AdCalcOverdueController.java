package controller.admin;

import controller.BaseServlet;
import DAO.BorrowingDAO;
import util.AuthUtil;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/admin/borrowing/calculateOverdueFee")
public class AdCalcOverdueController extends BaseServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!AuthUtil.isAdminCookie(req)) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Admin cookie not found");
            sendJsonResponse(resp, error);
            return;
        }

        // Get admin_id from cookie
        String adminIdStr = AuthUtil.getCookieValue(req);
        int adminId;
        try {
            assert adminIdStr != null;
            adminId = Integer.parseInt(adminIdStr);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid admin ID in cookie");
            sendJsonResponse(resp, error);
            return;
        }

        BorrowingDAO borrowingDAO = new BorrowingDAO();
        Map<String, Object> response = borrowingDAO.calculateOverdueFees(adminId);
        sendJsonResponse(resp, response);
    }
}
package controller.admin;

import controller.BaseServlet;
import model.BookAddRequest;
import DAO.BookDAO;
import util.AuthUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/admin/book/add")
public class AdBookAddController extends BaseServlet {
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

        BookAddRequest request = parseJsonRequest(req, BookAddRequest.class);
        if (request.getTitle() == null || request.getTitle().trim().isEmpty() ||
                request.getIsbn() == null || request.getIsbn().trim().isEmpty() ||
                request.getDescription() == null || request.getDescription().trim().isEmpty() ||
                request.getPublicationYear() <= 0 || request.getCopiesAvailable() < 0 ||
                request.getAuthorId() <= 0) {
            resp.setStatus(400);
            Map<String, String> error = new HashMap<>();
            error.put("error", "All book fields are required and must be valid");
            sendJsonResponse(resp, error);
            return;
        }
        BookDAO bookDAO = new BookDAO();
        Map<String, Object> response = bookDAO.addBook(request);
        sendJsonResponse(resp, response);
    }
}
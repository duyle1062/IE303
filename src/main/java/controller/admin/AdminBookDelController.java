package controller.admin;

import controller.BaseServlet;
import model.BookDeleteRequest;
import service.BookService;
import util.AuthUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/admin/book/delete")
public class AdminBookDelController extends BaseServlet {
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

        BookDeleteRequest request = parseJsonRequest(req, BookDeleteRequest.class);
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            resp.setStatus(400);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Title is required");
            sendJsonResponse(resp, error);
            return;
        }
        BookService bookService = new BookService();
        Map<String, Object> response = bookService.deleteBookByTitle(request.getTitle());
        sendJsonResponse(resp, response);
    }
}
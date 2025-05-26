package controller.customer;

import controller.BaseServlet;
import model.BookInfo;
import DAO.BookDAO;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/book/info")
public class BookInfoController extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String bookIdParam = req.getParameter("bookId");
        if (bookIdParam == null || bookIdParam.trim().isEmpty()) {
            resp.setStatus(400);
            sendJsonResponse(resp, "bookId parameter is required");
            return;
        }
        int bookId;
        try {
            bookId = Integer.parseInt(bookIdParam);
        } catch (NumberFormatException e) {
            resp.setStatus(400);
            sendJsonResponse(resp, "bookId must be a valid integer");
            return;
        }
        BookDAO bookDAO = new BookDAO();
        BookInfo bookInfo = bookDAO.getBookInfo(bookId);
        sendJsonResponse(resp, bookInfo == null ? "No result" : bookInfo);
    }
}
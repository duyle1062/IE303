package controller.customer;

import controller.BaseServlet;
import model.Book;
import service.BookService;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/book/search")
public class BookSearchController extends BaseServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String query = req.getParameter("q");
        if (query == null || query.trim().isEmpty()) {
            resp.setStatus(400);
            sendJsonResponse(resp, "Query parameter 'q' is required");
            return;
        }
        BookService bookService = new BookService();
        List<Book> books = bookService.searchBooks(query);
        sendJsonResponse(resp, books);
    }
}
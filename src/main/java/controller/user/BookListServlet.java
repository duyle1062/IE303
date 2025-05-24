package controller.user;

import controller.BaseServlet;
import model.Book;
import service.BookService;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/book")
public class BookListServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            BookService bookService = new BookService();
            List<Book> books = bookService.getAllBooks();
            sendJsonResponse(resp, books);
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendJsonResponse(resp, "Error retrieving books: " + e.getMessage());
        }
    }
}
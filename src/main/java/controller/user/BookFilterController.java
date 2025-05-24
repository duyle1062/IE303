package controller.user;

import controller.BaseServlet;
import model.FilterRequest;
import model.Book;
import service.BookService;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/book/filter")
public class BookFilterController extends BaseServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        FilterRequest filter = parseJsonRequest(req, FilterRequest.class);
        if (filter.getAuthorName().isEmpty() || filter.getGenreName().isEmpty()) {
            resp.setStatus(400);
            sendJsonResponse(resp, "authorName and genreName are required");
            return;
        }
        BookService bookService = new BookService();
        List<Book> books = bookService.filterBooks(filter.getAuthorName(), filter.getGenreName());
        sendJsonResponse(resp, books.isEmpty() ? "No result" : books);
    }
}
package service;

import dbConn.DBConnection;
import model.Book;
import model.BookInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookService {
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM BOOKS";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Book book = new Book();
                book.setBookId(rs.getInt("book_id"));
                book.setTitle(rs.getString("title"));
                book.setIsbn(rs.getString("isbn"));
                book.setDescription(rs.getString("description"));
                book.setPublicationYear(rs.getInt("publication_year"));
                book.setCopiesAvailable(rs.getInt("copies_available"));
                book.setAuthorId(rs.getInt("author_id"));
                books.add(book);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return books;
    }

    public List<Book> searchBooks(String query) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM BOOKS WHERE LOWER(title) LIKE ? OR LOWER(isbn) LIKE ? OR LOWER(description) LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchTerm = "%" + query.toLowerCase() + "%";
            stmt.setString(1, searchTerm);
            stmt.setString(2, searchTerm);
            stmt.setString(3, searchTerm);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Book book = new Book();
                    book.setBookId(rs.getInt("book_id"));
                    book.setTitle(rs.getString("title"));
                    book.setIsbn(rs.getString("isbn"));
                    book.setDescription(rs.getString("description"));
                    book.setPublicationYear(rs.getInt("publication_year"));
                    book.setCopiesAvailable(rs.getInt("copies_available"));
                    book.setAuthorId(rs.getInt("author_id"));
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return books;
    }

    public List<Book> filterBooks(String authorName, String genreName) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.* FROM Books b " +
                "JOIN Author a ON b.author_id = a.author_id " +
                "JOIN Book_Genre bg ON b.book_id = bg.book_id " +
                "JOIN Genre g ON bg.genre_id = g.genre_id " +
                "WHERE CONCAT(a.first_name, ' ', a.last_name) = ? AND g.genre_name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authorName);
            stmt.setString(2, genreName);
            System.out.println("Executing query with authorName: " + authorName + ", genreName: " + genreName);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Book book = new Book();
                    book.setBookId(rs.getInt("book_id"));
                    book.setTitle(rs.getString("title"));
                    book.setIsbn(rs.getString("isbn"));
                    book.setDescription(rs.getString("description"));
                    book.setPublicationYear(rs.getInt("publication_year"));
                    book.setCopiesAvailable(rs.getInt("copies_available"));
                    book.setAuthorId(rs.getInt("author_id"));
                    books.add(book);
                }
            }
            System.out.println("Found " + books.size() + " books");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return books;
    }

    public BookInfo getBookInfo(int bookId) {
        BookInfo bookInfo = null;
        String sql = "SELECT b.book_id, b.title, b.isbn, b.description, b.publication_year, " +
                "b.copies_available, CONCAT(a.first_name, ' ', a.last_name) AS author_name " +
                "FROM Books b JOIN Author a ON b.author_id = a.author_id WHERE b.book_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    bookInfo = new BookInfo();
                    bookInfo.setBookId(rs.getInt("book_id"));
                    bookInfo.setTitle(rs.getString("title"));
                    bookInfo.setIsbn(rs.getString("isbn"));
                    bookInfo.setDescription(rs.getString("description"));
                    bookInfo.setPublicationYear(rs.getInt("publication_year"));
                    bookInfo.setCopiesAvailable(rs.getInt("copies_available"));
                    bookInfo.setAuthorName(rs.getString("author_name"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (bookInfo != null) {
            List<String> genres = new ArrayList<>();
            String genreSql = "SELECT g.genre_name FROM Genre g " +
                    "JOIN Book_Genre bg ON g.genre_id = bg.genre_id WHERE bg.book_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(genreSql)) {
                stmt.setInt(1, bookId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        genres.add(rs.getString("genre_name"));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            bookInfo.setGenres(genres);
        }

        return bookInfo;
    }
}
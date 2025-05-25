package service;

import dbConn.DBConnection;
import model.Book;
import model.BookInfo;
import model.BookAddRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

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

    public Map<String, Object> addBook(BookAddRequest request) {
        Map<String, Object> response = new HashMap<>();

        // Validate author
        String authorSql = "SELECT author_id FROM Author WHERE author_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(authorSql)) {
            stmt.setInt(1, request.getAuthorId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    response.put("error", "Author not found");
                    return response;
                }
            }
        } catch (SQLException e) {
            response.put("error", "Database error");
            return response;
        }

        // Validate genres (if provided)
        List<Integer> genreIds = request.getGenreIds() != null ? request.getGenreIds() : new ArrayList<>();
        if (!genreIds.isEmpty()) {
            String genreSql = "SELECT genre_id FROM Genre WHERE genre_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(genreSql)) {
                for (Integer genreId : genreIds) {
                    stmt.setInt(1, genreId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (!rs.next()) {
                            response.put("error", "Genre ID " + genreId + " not found");
                            return response;
                        }
                    }
                }
            } catch (SQLException e) {
                response.put("error", "Database error");
                return response;
            }
        }

        // Insert book and genres
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Insert book
                String bookSql = "INSERT INTO Books (title, isbn, description, publication_year, copies_available, author_id) VALUES (?, ?, ?, ?, ?, ?)";
                int bookId;
                try (PreparedStatement stmt = conn.prepareStatement(bookSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, request.getTitle());
                    stmt.setString(2, request.getIsbn());
                    stmt.setString(3, request.getDescription());
                    stmt.setInt(4, request.getPublicationYear());
                    stmt.setInt(5, request.getCopiesAvailable());
                    stmt.setInt(6, request.getAuthorId());
                    stmt.executeUpdate();
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        rs.next();
                        bookId = rs.getInt(1);
                    }
                }

                // Insert genres
                if (!genreIds.isEmpty()) {
                    String bookGenreSql = "INSERT INTO Book_Genre (book_id, genre_id) VALUES (?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(bookGenreSql)) {
                        for (Integer genreId : genreIds) {
                            stmt.setInt(1, bookId);
                            stmt.setInt(2, genreId);
                            stmt.executeUpdate();
                        }
                    }
                }

                conn.commit();
                response.put("message", "Book added successfully");
                response.put("bookId", bookId);
            } catch (SQLException e) {
                conn.rollback();
                response.put("error", "Failed to add book");
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            response.put("error", "Database error");
        }

        return response;
    }

    public Map<String, Object> deleteBookByTitle(String title) {
        Map<String, Object> response = new HashMap<>();

        // Check if book exists
        String checkSql = "SELECT COUNT(*) AS count FROM Books WHERE title = ?";
        int bookCount;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(checkSql)) {
            stmt.setString(1, title);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                bookCount = rs.getInt("count");
            }
        } catch (SQLException e) {
            response.put("error", "Database error");
            return response;
        }

        if (bookCount == 0) {
            response.put("error", "No books found with title: " + title);
            return response;
        }

        // Delete book(s)
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String deleteSql = "DELETE FROM Books WHERE title = ?";
                try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
                    stmt.setString(1, title);
                    int deletedRows = stmt.executeUpdate();
                    conn.commit();
                    response.put("message", "Book(s) deleted successfully");
                    response.put("deletedCount", deletedRows);
                }
            } catch (SQLException e) {
                conn.rollback();
                response.put("error", "Failed to delete book(s)");
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            response.put("error", "Database error");
        }

        return response;
    }
}
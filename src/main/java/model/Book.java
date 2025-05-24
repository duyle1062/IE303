package model;

public class Book {
    private int book_id;
    private String title;
    private String isbn;
    private String description;
    private int publication_year;
    private int copies_available;
    private int author_id;

    // Getters, setters
    public int getBookId() { return book_id; }
    public void setBookId(int book_id) { this.book_id = book_id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getPublicationYear() { return publication_year; }
    public void setPublicationYear(int publication_year) { this.publication_year = publication_year; }
    public int getCopiesAvailable() { return copies_available; }
    public void setCopiesAvailable(int copies_available) { this.copies_available = copies_available; }
    public int getAuthorId() { return author_id; }
    public void setAuthorId(int author_id) { this.author_id = author_id; }
}
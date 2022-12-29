package exercise1;

import javafx.beans.property.SimpleStringProperty;

public class Book {
    private SimpleStringProperty bookId, bookTitle, bookCategory, bookAuthor, bookPubYear;

    public Book(String bookId, String bookTitle, String bookCategory, String bookAuthor, String bookPubYear){
        this.bookId = new SimpleStringProperty(bookId);
        this.bookTitle = new SimpleStringProperty(bookTitle);
        this.bookCategory = new SimpleStringProperty(bookCategory);
        this.bookAuthor = new SimpleStringProperty(bookAuthor);
        this.bookPubYear = new SimpleStringProperty(bookPubYear);
    }

    public String getBookId(){
        return bookId.get();
    }
    public String getBookTitle(){
        return bookTitle.get();
    }
    public String getBookCategory(){
        return bookCategory.get();
    }
    public String getBookAuthor(){
        return bookAuthor.get();
    }
    public String getBookPubYear(){
        return bookPubYear.get();
    }


}

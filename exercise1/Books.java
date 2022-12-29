package exercise1;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import javax.swing.table.DefaultTableModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class Books extends Application
{
    public Connection myConnection; //Variable to hold the connection to the database
    public ObservableList<Book> bookSearch = FXCollections.observableArrayList();
    public TableView<Book> booksTable = new TableView<Book>();

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage stage)
    {
        BorderPane pane = new BorderPane();

        //GridPane
        GridPane mainPane = new GridPane();
        mainPane.setHgap(10);
        mainPane.setVgap(10);
        GridPane bottomPane = new GridPane();
        bottomPane.setAlignment(Pos.CENTER);

        //Creating Text Fields, Labels, and Combo Boxes
        TextField txtBookTitle = new TextField();
        txtBookTitle.setPromptText("Book Title");
        TextField txtBookCategory = new TextField();
        txtBookCategory.setPromptText("Category");
        TextField txtBookYear = new TextField();
        txtBookYear.setPromptText("Year");
        TextField txtBookAuthor = new TextField();
        txtBookAuthor.setPromptText("Author");
        Label lblCategorySearch = new Label("Category:");
        TextField txtCategorySearch = new TextField();
        Label lblPubYearSearch = new Label("Publication Year: ");
        ComboBox cbxPubYearSearch = new ComboBox();
        cbxPubYearSearch.getItems().addAll("=", "<", ">");
        TextField txtPubYearSearch = new TextField();

        //Creating table
        TableColumn<Book, String> tbcBookId = new TableColumn<Book, String>("Book ID");
        tbcBookId.setMinWidth(1);
        tbcBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        TableColumn<Book, String> tbcBookTitle = new TableColumn<Book, String>("Title");
        tbcBookTitle.setMinWidth(200);
        tbcBookTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        TableColumn<Book, String> tbcBookCategory = new TableColumn<Book, String>("Category");
        tbcBookCategory.setMinWidth(145);
        tbcBookCategory.setCellValueFactory(new PropertyValueFactory<>("bookCategory"));
        TableColumn<Book, String> tbcBookYear = new TableColumn<Book, String>("Year");
        tbcBookYear.setMinWidth(4);
        tbcBookYear.setCellValueFactory(new PropertyValueFactory<>("bookPubYear"));
        TableColumn<Book, String> tbcBookAuthor = new TableColumn<Book, String>("Author");
        tbcBookAuthor.setMinWidth(200);
        tbcBookAuthor.setCellValueFactory(new PropertyValueFactory<>("bookAuthor"));
        booksTable.getColumns().addAll(tbcBookId, tbcBookTitle, tbcBookCategory, tbcBookYear, tbcBookAuthor);
        booksTable.setItems(bookSearch);

        //Creating buttons
        Button btnAddBook = new Button("Add Book");
        Button btnFilterTable = new Button("Filter the table");
        Button btnDeleteBook = new Button("Delete");

        //Buttons Actions
        btnAddBook.setOnAction(e -> {
            if(txtBookTitle.getText().equals("") ||
               txtBookCategory.getText().equals("") ||
               txtBookYear.getText().equals("") ||
               txtBookAuthor.getText().equals("")
            )
            {
                //Error
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setHeaderText(null);
                error.setContentText("You need to fill out all fields!");
                error.showAndWait();
            }
            else
            {
                addBook(txtBookTitle.getText(), txtBookCategory.getText(), txtBookYear.getText(), txtBookAuthor.getText());

                //Show confirmation
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("Book added!");
                alert.showAndWait();

                //Clear Fields
                clearFields(txtBookTitle, txtBookCategory, txtBookYear, txtBookAuthor);
            }
        });
        btnFilterTable.setOnAction(e -> {
            bookSearch.clear();

            if(txtCategorySearch.getText().equals("") && txtPubYearSearch.getText().equals("")){
                //Error
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setHeaderText(null);
                error.setContentText("You should provide at least one condition!");
                error.showAndWait();
            } else if (!txtPubYearSearch.getText().equals("") && cbxPubYearSearch.getSelectionModel().isEmpty()) {
                //Error
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setHeaderText(null);
                error.setContentText("You should select an operation for the publication year search!");
                error.showAndWait();
            }else{
                searchBooks(txtCategorySearch.getText(), txtPubYearSearch.getText(), (String) cbxPubYearSearch.getValue());
            }
        });
        btnDeleteBook.setOnAction(e -> {
            Book selectedBook = booksTable.getSelectionModel().getSelectedItem();

            deleteBook(selectedBook.getBookId());
            bookSearch.clear();
            searchBooks(txtCategorySearch.getText(), txtPubYearSearch.getText(), (String) cbxPubYearSearch.getValue());
        });

        //Adding controls to the grid pane
        mainPane.add(txtBookTitle,1,0);
        mainPane.add(txtBookCategory,2,0);
        mainPane.add(txtBookYear,3,0);
        mainPane.add(txtBookAuthor,4,0);
        mainPane.add(lblCategorySearch, 1, 10);
        mainPane.add(txtCategorySearch, 3, 10);
        mainPane.add(lblPubYearSearch, 1, 11);
        mainPane.add(cbxPubYearSearch, 2, 11);
        mainPane.add(txtPubYearSearch, 3, 11);
        mainPane.add(btnAddBook,5,0);
        mainPane.add(btnFilterTable, 4, 11);
        mainPane.add(booksTable, 1, 12, 5, 5);

        bottomPane.add(btnDeleteBook, 1, 1);

        //Setting grid pane in the position
        pane.setLeft(mainPane);
        pane.setBottom(bottomPane);

        //Create scene
        Scene scene = new Scene(pane, 720, 600);
        stage.setTitle("Books");

        //Place scene in the stage
        stage.setScene(scene);
        stage.show();
    }

    void clearFields
            (
                    TextField txtBookTitle,
                    TextField txtBookCategory,
                    TextField txtBookYear,
                    TextField txtBookAuthor
            )
    {
        txtBookTitle.setText("");
        txtBookCategory.setText("");
        txtBookYear.setText("");
        txtBookAuthor.setText("");
    }

    void addBook
            (
                String bookTitle,
                String bookCategory,
                String bookYear,
                String bookAuthor
            )
    {
        try{
            myConnection = dbConnect();

            PreparedStatement insertBookStmt = myConnection.prepareStatement("INSERT INTO book(title, author, category, p_year) VALUES(?,?,?,?)");
            insertBookStmt.setString(1, bookTitle);
            insertBookStmt.setString(2, bookAuthor);
            insertBookStmt.setString(3, bookCategory);
            insertBookStmt.setString(4, bookYear);
            insertBookStmt.executeUpdate();

            myConnection.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    void searchBooks(String bookCategory, String bookPubYear, String opPubYear){
        String sqlSearch = "SELECT bookID, title, category, p_year, author FROM book WHERE";
        String sqlWhere = "";

        if(!bookCategory.equals("")){
            sqlWhere += " category = '" + bookCategory + "'";
        }
        if(!bookPubYear.equals("")){
            if(!sqlWhere.equals("")){
                sqlWhere += "AND";
            }
            sqlWhere += " p_year " + opPubYear + "'" + bookPubYear + "'";
        }
        sqlSearch += sqlWhere;

        try{
            myConnection = dbConnect();
            PreparedStatement selectBooksStmt = myConnection.prepareStatement(sqlSearch);
            ResultSet selectBooksResSet = selectBooksStmt.executeQuery();

            while(selectBooksResSet.next()){
                bookSearch.add(new Book(
                        selectBooksResSet.getString(1),
                        selectBooksResSet.getString(2),
                        selectBooksResSet.getString(3),
                        selectBooksResSet.getString(5),
                        selectBooksResSet.getString(4)
                        ));
            }

            myConnection.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    void deleteBook(String bookId){
        try{
            myConnection = dbConnect();

            PreparedStatement deleteBookStmt = myConnection.prepareStatement("DELETE FROM book WHERE bookID = ?");
            deleteBookStmt.setString(1, bookId);
            deleteBookStmt.executeUpdate();

            myConnection.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    Connection dbConnect(){
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            myConnection = DriverManager.getConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("DB Connected!");
        return myConnection;
    }
}

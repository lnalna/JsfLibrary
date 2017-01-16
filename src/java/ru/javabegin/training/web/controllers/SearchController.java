package ru.javabegin.training.web.controllers;

import java.util.Map;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.faces.context.FacesContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import ru.javabegin.training.web.enums.SearchType;
import java.util.ArrayList;
import ru.javabegin.training.web.beans.Book;
import ru.javabegin.training.web.db.Database;
import java.util.logging.Level;
import java.util.logging.Logger;



@ManagedBean(eager = true)
@SessionScoped
public class SearchController implements Serializable{
    
    private int booksOnPage = 2;// книг на странице
    private int selectedGenreId;// выбранный жанр
    private char selectedLetter;// выбранная буква алфавита
    private long selectedPageNumber = 1;//выбранный номер страницы в постраничной навигации
    private long totalBooksCount;//общее количество книг    
    private SearchType searchType;
    private String searchString;
    private static Map<String, SearchType> searchList = new HashMap<String, SearchType>();//хранит все виды поисков (по автору, по названию)
    private ArrayList<Book> currentBookList;//текущий список книг для отображения
    private String currentSql;//последний выполненный sql без добавления limit
    
    
    public SearchController(){
        fillBooksAll();
        
        ResourceBundle bundle = ResourceBundle.getBundle("ru.javabegin.training.web.nls.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
        searchList.put(bundle.getString("author_name"), searchType.AUTHOR);
        searchList.put(bundle.getString("book_name"), searchType.TITLE);
    }
    
    private void fillBooksBySQL(String sql){
        
        Statement stmt = null;
        ResultSet rs = null;
        Connection conn = null;
        
        
        try {
            conn = Database.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            currentBookList = new ArrayList<Book>();
            
            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getLong("book.id"));
                book.setName(rs.getString("book.name"));
                book.setGenre(rs.getString("book.genre_id"));
                book.setIsbn(rs.getString("book.isbn"));
                book.setAuthor(rs.getString("author.fio"));
                book.setPageCount(rs.getInt("book.page_count"));
                book.setPublishDate(rs.getInt("book.publish_year"));
                book.setPublisher(rs.getString("publisher.name"));
                book.setDescription(rs.getString("book.description"));
                currentBookList.add(book);
                
            }
        } catch (SQLException ex) {
            Logger.getLogger(SearchController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (stmt != null){
                    stmt.close();
                }
                if (rs != null){
                    rs.close();
                }
                if (conn != null){
                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(SearchController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void fillBooksAll(){
        fillBooksBySQL("select * from library.book "
                    + "inner join library.author on "
                    + "library.book.author_id=library.author.id "
                    + "inner join library.publisher on "
                    + "library.book.publisher_id=library.publisher.id "
                    + "inner join library.genre on "
                    + "library.book.genre_id=library.genre.id");
    }
    
    public void fillBooksByGenre(){
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        Integer genreId = Integer.valueOf(params.get("genre_id"));
        
        fillBooksBySQL("select * from library.book "
                + "inner join library.author on "
                + "library.book.author_id=library.author.id "
                + "inner join library.publisher on "
                + "library.book.publisher_id=library.publisher.id "
                + "inner join library.genre on "
                + "library.book.genre_id=library.genre.id"
                + " where genre_id=" + genreId);
        
    }
    
    public void fillBooksBySearch(){
        
        if (searchString.trim().length() == 0){
            fillBooksAll();
            return;
        }
        
        StringBuilder sql = new StringBuilder("select * from library.book "
                + "inner join library.author on library.book.author_id=library.author.id "
                + "inner join library.genre on library.book.genre_id=library.genre.id "
                + "inner join library.publisher on library.book.publisher_id=library.publisher.id ");

        if (searchType == SearchType.AUTHOR) {
            sql.append("where lower(library.author.fio) like '%" + searchString.toLowerCase() + "%' order by library.book.name ");

        } else if (searchType == SearchType.TITLE) {
            sql.append("where lower(library.book.name) like '%" + searchString.toLowerCase() + "%' order by library.book.name ");
        }
        
        fillBooksBySQL(sql.toString());
    }
    
    public void fillBooksByLetter() {

        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String searchLetter = params.get("letter");

        fillBooksBySQL("select * from library.book "
                + "inner join library.author on library.book.author_id=library.author.id "
                + "inner join library.genre on library.book.genre_id=library.genre.id "
                + "inner join library.publisher on library.book.publisher_id=library.publisher.id "
                + " where lcase(left(library.book.name,1))='" + searchLetter + "' ");

    }
    
    public byte[] getImage(int id){
        
        Statement stmt = null;
        ResultSet rs = null;
        Connection conn = null;
        
        byte[] image = null;
        
                
        try {
            conn = Database.getConnection();
            stmt = conn.createStatement();
            
            rs = stmt.executeQuery("select image from library.book where id=" + id);
            while(rs.next()){
                image = rs.getBytes("image");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Book.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            
            try{
                if (stmt != null){
                    stmt.close();
                }
                if (rs != null){
                    rs.close();
                }
                if (conn != null){
                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Book.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return image;
    }
    
    public Character[] getRussianLetters() {
        Character[] letters = new Character[33];
        letters[0] = 'А';
        letters[1] = 'Б';
        letters[2] = 'В';
        letters[3] = 'Г';
        letters[4] = 'Д';
        letters[5] = 'Е';
        letters[6] = 'Ё';
        letters[7] = 'Ж';
        letters[8] = 'З';
        letters[9] = 'И';
        letters[10] = 'Й';
        letters[11] = 'К';
        letters[12] = 'Л';
        letters[13] = 'М';
        letters[14] = 'Н';
        letters[15] = 'О';
        letters[16] = 'П';
        letters[17] = 'Р';
        letters[18] = 'С';
        letters[19] = 'Т';
        letters[20] = 'У';
        letters[21] = 'Ф';
        letters[22] = 'Х';
        letters[23] = 'Ц';
        letters[24] = 'Ч';
        letters[25] = 'Ш';
        letters[26] = 'Щ';
        letters[27] = 'Ъ';
        letters[28] = 'Ы';
        letters[29] = 'Ь';
        letters[30] = 'Э';
        letters[31] = 'Ю';
        letters[32] = 'Я';

        return letters;
    }
    
    
    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }
    
    public SearchType getSearchType(){
        return searchType;
    }
    
    public void setSearchType(SearchType searchType) {
        this.searchType = searchType;
    }
    
    public Map<String, SearchType> getSearchList() {
        return searchList;
    }
    
    public ArrayList<Book> getCurrentBookList(){
        return currentBookList;
    }
}

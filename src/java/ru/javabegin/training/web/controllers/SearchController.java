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
    
    private SearchType searchType;
    private static Map<String, SearchType> searchList = new HashMap<String, SearchType>();//хранит все виды поисков (по автору, по названию)
    private ArrayList<Book> currentBookList;//текущий список книг для отображения
    
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
    
    public SearchType getSearchType(){
        return searchType;
    }
    
    public Map<String, SearchType> getSearchList() {
        return searchList;
    }
    
    public ArrayList<Book> getCurrentBookList(){
        return currentBookList;
    }
}

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
    
    public SearchType getSearchType(){
        return searchType;
    }
    
    public Map<String, SearchType> getSearchList() {
        return searchList;
    }
}

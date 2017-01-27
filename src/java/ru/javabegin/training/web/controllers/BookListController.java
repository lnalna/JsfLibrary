package ru.javabegin.training.web.controllers;

import java.util.Map;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
import javax.faces.event.ValueChangeEvent;



@ManagedBean(eager = true)
@SessionScoped
public class BookListController implements Serializable{
    
    private ArrayList<Book> currentBookList;//текущий список книг для отображения
    private ArrayList<Integer> pageNumbers = new ArrayList<Integer>();//количество страниц для постраничности
    
    
    //критерии поиска
    private char selectedLetter;//выбранная буква алфавита, по умолчанию не выбрана ни одна буква
    private SearchType selectedSearchType = SearchType.TITLE;//хранит выбранный тип поиска, по умолчанию - по названию
    private int selectedGenreId;//выбранный жанр
    private String currentSearchString;//хранит поисковую строку
    private String currentSqlNoLimit;//последний выполненный sql без добавления limit
    
    //для постраничности    
    private boolean pageSelected;//запрос со страницы requestFromPages
    private int booksCountOnPage = 2;//количество отображаемых книг на одной странице, нужно для подсчета количества страниц
    private long selectedPageNumber = 1;//выбранный номер страницы в постраничной навигации
    private long totalBooksCount;//общее количество книг всего, нужно для подсчета количества страниц
    
    //отображение режима редактирования
    private boolean editModeView;
    
    
    public BookListController(){
        fillBooksAll();       
    }
    
//<editor-fold defaultstate="collapsed" desc="нахождение книг по запросу fillBooksBySQL">
    private void fillBooksBySQL(String sql){
        
        StringBuilder sqlBuilder = new StringBuilder(sql);
        
        currentSqlNoLimit = sql;
        
        Statement stmt = null;
        ResultSet rs = null;
        Connection conn = null;
        
        
        try {
            conn = Database.getConnection();
            stmt = conn.createStatement();
            
            
            if (!pageSelected) {
                //запрос выполняется без limit для подсчета строк - количества книг
                rs = stmt.executeQuery(sqlBuilder.toString());
                rs.last();
                
                totalBooksCount = rs.getRow();
                
                fillPageNumbers(totalBooksCount, booksCountOnPage);
                
            }
            if (totalBooksCount > booksCountOnPage){
                sqlBuilder.append(" limit ").append(selectedPageNumber * booksCountOnPage - booksCountOnPage).append(",").append(booksCountOnPage);
            }
            
            //запрос выполняется с limit
            rs = stmt.executeQuery(sqlBuilder.toString());
            
            currentBookList = new ArrayList<Book>();
            
            System.out.println(sqlBuilder);
            
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
            Logger.getLogger(BookListController.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(BookListController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="подсчет количества страниц">
    public void changeBooksCountOnPage(ValueChangeEvent e){
        imitateLoading();
        cancelEditModeView();
        pageSelected = false;
        booksCountOnPage = Integer.parseInt(e.getNewValue().toString());
        selectedPageNumber = 1;
        fillBooksBySQL(currentSqlNoLimit);
    }
    
    private void fillPageNumbers(long totalBooksCount, int booksCountOnPage) {
        
        int pageCount = 0;
        
        if(totalBooksCount % booksCountOnPage != 0) {
            
            pageCount = totalBooksCount > 0 ? (int) ((totalBooksCount / booksCountOnPage) + 1) : 0;
        }
        else {
            pageCount = totalBooksCount > 0 ? (int) (totalBooksCount / booksCountOnPage)  : 0;
        }
        
        pageNumbers.clear();
        for (int i = 1; i <= pageCount; i++) {
            pageNumbers.add(i);
        }
        
    }
//</editor-fold>
        
    
//<editor-fold defaultstate="collapsed" desc="поиск всех книг fillBooksAll">
    private void fillBooksAll(){
        fillBooksBySQL("select * from library.book "
                + "inner join library.author on "
                + "library.book.author_id=library.author.id "
                + "inner join library.publisher on "
                + "library.book.publisher_id=library.publisher.id "
                + "inner join library.genre on "
                + "library.book.genre_id=library.genre.id");
    }
//</editor-fold>
    
    private void submitValues(Character selectedLetter, long selectedPageNumber, int selectedGenreId, boolean requestFromPager ) {
        this.selectedLetter = selectedLetter;
        this.selectedPageNumber = selectedPageNumber;
        this.selectedGenreId = selectedGenreId;
        this.pageSelected = requestFromPager;
    }
    
//<editor-fold defaultstate="collapsed" desc="поиск по жанру fillBooksByGenre">
    
    public void fillBooksByGenre(){
        
        imitateLoading();
        
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        
        submitValues(' ', 1, Integer.valueOf(params.get("genre_id")), false);
        
        fillBooksBySQL("select * from library.book "
                + "inner join library.author on "
                + "library.book.author_id=library.author.id "
                + "inner join library.publisher on "
                + "library.book.publisher_id=library.publisher.id "
                + "inner join library.genre on "
                + "library.book.genre_id=library.genre.id"
                + " where genre_id=" + selectedGenreId);
        
        
    }
//</editor-fold>
    
//<editor-fold defaultstate="collapsed" desc="поиск по названию или автору  fillBooksBySearch">
    public void fillBooksBySearch(){
        
        imitateLoading();
        
        submitValues(' ', 1, -1, false);
        
        if (currentSearchString.trim().length() == 0){
            fillBooksAll();
            return;
        }
        
        StringBuilder sql = new StringBuilder("select * from library.book "
                + "inner join library.author on library.book.author_id=library.author.id "
                + "inner join library.genre on library.book.genre_id=library.genre.id "
                + "inner join library.publisher on library.book.publisher_id=library.publisher.id ");
        
        if (selectedSearchType == SearchType.AUTHOR) {
            sql.append("where lower(library.author.fio) like '%" + currentSearchString.toLowerCase() + "%' order by library.book.name ");
            
        } else if (selectedSearchType == SearchType.TITLE) {
            sql.append("where lower(library.book.name) like '%" + currentSearchString.toLowerCase() + "%' order by library.book.name ");
        }
        
        fillBooksBySQL(sql.toString());
        
    }
//</editor-fold>
    
//<editor-fold defaultstate="collapsed" desc="поиск по букве fillBooksByLetter">
    public void fillBooksByLetter() {
        
        imitateLoading();
        
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        selectedLetter = params.get("letter").charAt(0);
        
        submitValues(selectedLetter, 1, -1, false);
        
        fillBooksBySQL("select * from library.book "
                + "inner join library.author on library.book.author_id=library.author.id "
                + "inner join library.genre on library.book.genre_id=library.genre.id "
                + "inner join library.publisher on library.book.publisher_id=library.publisher.id "
                + " where lcase(left(library.book.name,1))='" + selectedLetter + "' order by library.book.name");
        
        
    }
//</editor-fold>
    
    public void selectPage(){
        
        imitateLoading();
        
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        selectedPageNumber = Integer.valueOf(params.get("page_number"));
        pageSelected = true;
        fillBooksBySQL(currentSqlNoLimit);
    }
    
//<editor-fold defaultstate="collapsed" desc="получение контента из таблицы  library.book  метод getContent ">
    public byte[] getContent(int id) {
        Statement stmt = null;
        ResultSet rs = null;
        Connection conn = null;
        
        
        byte[] content = null;
        try {
            conn = Database.getConnection();
            stmt = conn.createStatement();
            
            rs = stmt.executeQuery("select content from library.book where id=" + id);
            while (rs.next()) {
                content = rs.getBytes("content");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Book.class
                    .getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Book.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return content;
        
    }
//</editor-fold>
    
//<editor-fold defaultstate="collapsed" desc="получение обложки из таблицы library.book  метод getImage">
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
//</editor-fold>
    
//<editor-fold defaultstate="collapsed" desc="update таблицы library.book  метод updateBooks">
    public String updateBooks(){
        
        imitateLoading();
        
        PreparedStatement prepStmt = null;
        ResultSet rs = null;
        Connection conn = null;
        
        try{
            conn = Database.getConnection();
            prepStmt = conn.prepareStatement("update library.book set name=?, isbn=?, page_count=?, publish_year=?, description=? where id=?");
            
            for(Book book : currentBookList){
                
                if (!book.isEdit()) continue;
                
                prepStmt.setString(1, book.getName());
                prepStmt.setString(2, book.getIsbn());
                //         prepStmt.setString(3, book.getAuthor());
                prepStmt.setInt(3, book.getPageCount());
                prepStmt.setInt(4, book.getPublishDate());
                prepStmt.setString(5, book.getDescription());
                //prepStmt.setString(6, book.getPublisher());
                prepStmt.setLong(6, book.getId());
                prepStmt.addBatch();
            }
            
            prepStmt.executeBatch();
            
            
        } catch(SQLException ex){
            Logger.getLogger(BookListController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try{
                if (prepStmt != null){
                    prepStmt.close();
                }
                if (rs != null){
                    rs.close();
                }
                if (conn != null){
                    conn.close();
                }
            } catch (SQLException ex){
                Logger.getLogger(BookListController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        cancelEditModeView();
        
        return "books";
    }
//</editor-fold>
    
      
    public boolean isEditModeView(){
        return editModeView;
    }
    
    public void showEditModeView(){
        editModeView = true;
    }
    
    public void cancelEditModeView(){
        editModeView = false;
        for(Book book : currentBookList){
            book.setEdit(false);
        }
        
    }
    
//<editor-fold defaultstate="collapsed" desc="получение всего русского алфавита getRussianLetters">
    public Character[] getRussianLetters() {
        Character[] letters = new Character[]{'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я',};
        return letters;
    }
    
//</editor-fold>
    
//<editor-fold defaultstate="collapsed" desc="геттеры и сеттеры">
    public String getSearchString() {
        return currentSearchString;
    }
    
    public void setSearchString(String searchString) {
        this.currentSearchString = searchString;
    }
    
    public SearchType getSearchType(){
        return selectedSearchType;
    }
    
    public void setSearchType(SearchType searchType) {
        this.selectedSearchType = searchType;
    }
    
    
    public ArrayList<Book> getCurrentBookList(){
        return currentBookList;
    }
    
    public ArrayList<Integer> getPageNumbers(){
        return pageNumbers;
    }
    
    public void setPageNumbers(ArrayList<Integer> pageNumbers){
        this.pageNumbers = pageNumbers;
    }
    
    public int getBooksCountOnPage() {
        return booksCountOnPage;
    }
    
    public void setBooksCountOnPage(int booksCountOnPage) {
        this.booksCountOnPage = booksCountOnPage;
    }
    
    public int getSelectedGenreId() {
        return selectedGenreId;
    }
    
    public void setSelectedGenreId(int selectedGenreId) {
        this.selectedGenreId = selectedGenreId;
    }
    
    public char getSelectedLetter() {
        return selectedLetter;
    }
    
    public void setSelectedLetter(char selectedLetter) {
        this.selectedLetter = selectedLetter;
    }
    
    public long getSelectedPageNumber() {
        return selectedPageNumber;
    }
    
    public void setSelectedPageNumber(long selectedPageNumber) {
        this.selectedPageNumber = selectedPageNumber;
    }
    
    public long getTotalBooksCount() {
        return totalBooksCount;
    }
    
    public void setTotalBooksCount(long totalBooksCount) {
        this.totalBooksCount = totalBooksCount;
    }
    
    public void searchStringChanged(ValueChangeEvent e){
        currentSearchString = e.getNewValue().toString();
    }
    
    public void searchTypeChanged(ValueChangeEvent e){
        selectedSearchType  = (SearchType) e.getNewValue();
    }
//</editor-fold>
    
//<editor-fold defaultstate="collapsed" desc="имитация загрузки из базы данных метод imitateLoading">
    private void imitateLoading() {
        try {
            Thread.sleep(1000);// имитация загрузки процесса
        } catch (InterruptedException ex) {
            Logger.getLogger(BookListController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//</editor-fold>
}

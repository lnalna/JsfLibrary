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
    
    
    
    
    
    
    
    
    
    
    
    
    public BookListController(){
        fillBooksAll();
        
        
    }
    
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
    
    public void booksCountOnPageChanged(ValueChangeEvent e){
        imitateLoading();
        cancelEdit();
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
        
    
    private void fillBooksAll(){
        fillBooksBySQL("select * from library.book "
                    + "inner join library.author on "
                    + "library.book.author_id=library.author.id "
                    + "inner join library.publisher on "
                    + "library.book.publisher_id=library.publisher.id "
                    + "inner join library.genre on "
                    + "library.book.genre_id=library.genre.id");
    }
    
    private void submitValues(Character selectedLetter, long selectedPageNumber, int selectedGenreId, boolean requestFromPager ) {
        this.selectedLetter = selectedLetter;
        this.selectedPageNumber = selectedPageNumber;
        this.selectedGenreId = selectedGenreId;
        this.pageSelected = requestFromPager;
    }
    
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
    
    public void selectPage(){
        
        imitateLoading();
        
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        selectedPageNumber = Integer.valueOf(params.get("page_number"));
        pageSelected = true;
        fillBooksBySQL(currentSqlNoLimit);
    }
    
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
        
        cancelEdit();
        
        return "books";
    }
    
    private boolean editMode;
    
    public boolean isEditMode(){
        return editMode;
    }
    
    public void showEdit(){
        editMode = true;
    }
    
    public void cancelEdit(){
        editMode = false;
        for(Book book : currentBookList){
            book.setEdit(false);
        }
        
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
    
    private void imitateLoading() {
        try {
            Thread.sleep(1000);// имитация загрузки процесса
        } catch (InterruptedException ex) {
            Logger.getLogger(BookListController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

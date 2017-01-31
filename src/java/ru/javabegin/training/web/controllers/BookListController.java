package ru.javabegin.training.web.controllers;

//import java.util.Map;
import java.io.Serializable;
import java.sql.Connection;
//import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.faces.context.FacesContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import ru.javabegin.training.web.enums.SearchType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ru.javabegin.training.web.entity.Book;
import ru.javabegin.training.web.db.Database;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.event.ValueChangeEvent;
import ru.javabegin.training.web.db.DataHelper;



@ManagedBean(eager = true)
@SessionScoped
public class BookListController implements Serializable{
    
    private List<Book> currentBookList;//текущий список книг для отображения
    private Long selectedAuthorId;//текущий автор книги из списка при редактировании книги
    private ArrayList<Integer> pageNumbers = new ArrayList<Integer>();//количество страниц для постраничности
    
    
    //критерии поиска
    private char selectedLetter;//выбранная буква алфавита, по умолчанию не выбрана ни одна буква
    private SearchType selectedSearchType = SearchType.TITLE;//хранит выбранный тип поиска, по умолчанию - по названию
    private long selectedGenreId;//выбранный жанр
    private String currentSearchString;//хранит поисковую строку
    //private String currentSqlNoLimit;//последний выполненный sql без добавления limit
    
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
    
    private void submitValues(Character selectedLetter, long selectedPageNumber, long selectedGenreId, boolean requestFromPager ) {
        this.selectedLetter = selectedLetter;
        this.selectedPageNumber = selectedPageNumber;
        this.selectedGenreId = selectedGenreId;
        this.pageSelected = requestFromPager;
    }
    
    //<editor-fold defaultstate="collapsed" desc="поиск всех книг fillBooksAll">
    private void fillBooksAll(){
        currentBookList = DataHelper.getInstance().getAllBooks();
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="поиск по жанру fillBooksByGenre">
    
    public String fillBooksByGenre(){
        
        //imitateLoading();
        
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        
        selectedGenreId = Long.valueOf(params.get("genre_id"));
        
        submitValues(' ', 1, selectedGenreId, false);
        currentBookList = DataHelper.getInstance().getBooksByGenre(selectedGenreId);
        
        return "books";
    }
//</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="поиск по букве fillBooksByLetter">
    public String fillBooksByLetter() {
        
        //imitateLoading();
        
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        selectedLetter = params.get("letter").charAt(0);
        
        submitValues(selectedLetter, 1, -1, false);
        
        currentBookList = DataHelper.getInstance().getBooksByLetter(selectedLetter);
        
        return "books";        
    }
//</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="поиск по названию или автору  fillBooksBySearch">
    public String fillBooksBySearch(){
        
      //  imitateLoading();
        
        submitValues(' ', 1, -1, false);
        
        if (currentSearchString.trim().length() == 0){
            fillBooksAll();
            return "books";
        }
        
        
        if (selectedSearchType == SearchType.AUTHOR) {
            currentBookList = DataHelper.getInstance().getBooksByAuthor(currentSearchString);            
        } else if (selectedSearchType == SearchType.TITLE) {
            currentBookList = DataHelper.getInstance().getBooksByName(currentSearchString);
        }
        
        return "books";
        
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="update таблицы library.book  метод updateBooks">
    public void updateBooks(){
               
        cancelEditModeView();
        
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
    
    
    
    public void searchStringChanged(ValueChangeEvent e){
        currentSearchString = e.getNewValue().toString();
    }
    
    public void searchTypeChanged(ValueChangeEvent e){
        selectedSearchType  = (SearchType) e.getNewValue();
    }
    

    
    //<editor-fold defaultstate="collapsed" desc="подсчет количества страниц">
    public void changeBooksCountOnPage(ValueChangeEvent e){
  //      imitateLoading();
  //      cancelEditModeView();
  //      pageSelected = false;
  //      booksCountOnPage = Integer.parseInt(e.getNewValue().toString());
  //      selectedPageNumber = 1;
  //      fillBooksBySQL(currentSqlNoLimit);
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
        
    

    
    
    

    

    

    
    public void selectPage(){
        
 //       imitateLoading();
        
   //     Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
   //     selectedPageNumber = Integer.valueOf(params.get("page_number"));
   //     pageSelected = true;
   //     fillBooksBySQL(currentSqlNoLimit);
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
    
    
    public List<Book> getCurrentBookList(){
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
    
    public long getSelectedGenreId() {
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
    
    public int getBooksOnPage() {
        return booksCountOnPage;
    }

    public void setBooksOnPage(int booksOnPage) {
        this.booksCountOnPage = booksOnPage;
    }
    
    
    public Long getSelectedAuthorId() {
        return selectedAuthorId;
    }

    public void setSelectedAuthorId(Long selectedAuthorId) {
        this.selectedAuthorId = selectedAuthorId;
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

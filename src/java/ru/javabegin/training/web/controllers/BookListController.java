package ru.javabegin.training.web.controllers;

import java.util.Map;
import java.io.Serializable;
import javax.faces.context.FacesContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import ru.javabegin.training.web.enums.SearchType;
import java.util.Map;
import java.util.ResourceBundle;
import ru.javabegin.training.web.entity.Book;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.event.ValueChangeEvent;
import org.primefaces.component.datagrid.DataGrid;
import ru.javabegin.training.web.db.DataHelper;
import ru.javabegin.training.web.beans.Pager;
import org.primefaces.model.LazyDataModel;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import ru.javabegin.training.web.models.BookListDataModel;




@ManagedBean(eager = true)
@SessionScoped
public class BookListController implements Serializable{
    
    private DataGrid dataTable;
    private Book selectedBook;
    private DataHelper dataHelper =  DataHelper.getInstance();
    private LazyDataModel<Book> bookListModel;
    private Long selectedAuthorId;//текущий автор книги из списка при редактировании книги   
    //критерии поиска
    private char selectedLetter;//выбранная буква алфавита, по умолчанию не выбрана ни одна буква
    private SearchType selectedSearchType = SearchType.TITLE;//хранит выбранный тип поиска, по умолчанию - по названию
    private long selectedGenreId;//выбранный жанр
    private String currentSearchString;//хранит поисковую строку
    private Pager pager =  Pager.getInstance();   
       
    //отображение режима редактирования
    private boolean editModeView;
    
    //номер строки (номер книги в списке книг)
    private transient int row = -1;
    
  
    
    
    public BookListController(){
        bookListModel = new BookListDataModel();       
    }
    
    private void submitValues(Character selectedLetter,  long selectedGenreId) {
        this.selectedLetter = selectedLetter;        
        this.selectedGenreId = selectedGenreId;   
        dataTable.setFirst(0);
    }
    
    //<editor-fold defaultstate="collapsed" desc="поиск всех книг fillBooksAll">
    public void fillBooksAll(){
       
       dataHelper.getAllBooks();
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="поиск по жанру fillBooksByGenre">
    
    public void fillBooksByGenre(){
        
        imitateLoading();
    //    cancelEditModeView();
    //    row = -1;
                
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        
        selectedGenreId = Long.valueOf(params.get("genre_id"));
        
        submitValues(' ',  selectedGenreId);
        dataHelper.getBooksByGenre(selectedGenreId);       
       
    }
//</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="поиск по букве fillBooksByLetter">
    public void fillBooksByLetter() {
        
        imitateLoading();
  //      cancelEditModeView();
  //      row = -1;
                
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        selectedLetter = params.get("letter").charAt(0);
        
        submitValues(selectedLetter, -1);
        
        dataHelper.getBooksByLetter(selectedLetter);
                
    }
//</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="поиск по названию или автору  fillBooksBySearch">
    public void fillBooksBySearch(){
       
        imitateLoading();
  //      cancelEditModeView();
  //      row = -1;
             
        submitValues(' ', -1);
        
        if (currentSearchString.trim().length() == 0){
            fillBooksAll();
            
        }
        
        
        if (selectedSearchType == SearchType.AUTHOR) {
            dataHelper.getBooksByAuthor(currentSearchString);            
        } else if (selectedSearchType == SearchType.TITLE) {
            dataHelper.getBooksByName(currentSearchString);
        }
        
            
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="update таблицы library.book  метод updateBooks">
    public void updateBooks(){
         
        dataHelper.updateBook(selectedBook);
        
        cancelEditModeView();
        
        dataHelper.populateList();
        
   //     RequestContext.getCurrentInstance().execute("dlgEditBook.hide()");

        ResourceBundle bundle = ResourceBundle.getBundle("ru.javabegin.training.web.nls.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(bundle.getString("updated")));

        dataTable.setFirst(calcSelectedPage());
    //fillBooksBySearch();
        
    }
    
    public void deleteBook() {
        dataHelper.deleteBook(selectedBook);
        
        cancelEditModeView();
        
        dataHelper.populateList();

//        RequestContext.getCurrentInstance().execute("dlgDeleteBook.hide()");
        ResourceBundle bundle = ResourceBundle.getBundle("ru.javabegin.training.web.nls.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(bundle.getString("deleted")));

        dataTable.setFirst(calcSelectedPage());
    //    fillBooksBySearch();

    }
//</editor-fold>
    
    public boolean isEditModeView(){
        return editModeView;
    }
    
    public void showEditModeView(){
        row=-1;
        editModeView = true;
    }
    
    public void cancelEditModeView(){
        editModeView = false;
    //    RequestContext.getCurrentInstance().execute("dlgEditBook.hide()");

        
    }
    
    public void switchEditMode() {
        editModeView = true;
//        RequestContext.getCurrentInstance().execute("dlgEditBook.show()");

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
      
    private int calcSelectedPage() {
        int page = dataTable.getPage();// текущий номер страницы (индексация с нуля)
        
        int leftBound = pager.getTo()*(page-1);
        int rightBound = pager.getTo()*page;
        
        boolean goPrevPage = pager.getTotalBooksCount()>leftBound & pager.getTotalBooksCount() <= rightBound;
                
                
        if (goPrevPage)        
        {
            page--;
        }       
        
        return (page>0)?page*pager.getTo():0;
    }
    
//</editor-fold>
       
    
public int getRow(){
    row +=1;
    return row;
}
   
    
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
    
    
    public Pager getPager(){
        return pager;
    }
    
    public LazyDataModel<Book> getBookListModel(){
       return bookListModel;
    }
    
    public void setSelectedBook(Book selectedBook) {
        this.selectedBook = selectedBook;
    }

    public Book getSelectedBook() {
        return selectedBook;
    }

    public DataGrid getDataGrid() {
        return dataTable;
    }

    public void setDataGrid(DataGrid dataTable) {
        this.dataTable = dataTable;
    }
}

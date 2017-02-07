package ru.javabegin.training.web.controllers;

//import java.util.Map;
import java.io.Serializable;
import javax.faces.context.FacesContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import ru.javabegin.training.web.enums.SearchType;
import java.util.Map;
import ru.javabegin.training.web.entity.Book;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.event.ValueChangeEvent;
import ru.javabegin.training.web.db.DataHelper;
import ru.javabegin.training.web.beans.Pager;




@ManagedBean(eager = true)
@SessionScoped
public class BookListController implements Serializable{
        
    private Long selectedAuthorId;//текущий автор книги из списка при редактировании книги   
    //критерии поиска
    private char selectedLetter;//выбранная буква алфавита, по умолчанию не выбрана ни одна буква
    private SearchType selectedSearchType = SearchType.TITLE;//хранит выбранный тип поиска, по умолчанию - по названию
    private long selectedGenreId;//выбранный жанр
    private String currentSearchString;//хранит поисковую строку
    private Pager<Book> pager = new Pager<Book>();   
       
    //отображение режима редактирования
    private boolean editModeView;
    
    //номер строки (номер книги в списке книг)
  //  private transient int row = -1;
    
  
    
    
    public BookListController(){
        fillBooksAll();       
    }
    
    private void submitValues(Character selectedLetter, int selectedPageNumber, long selectedGenreId) {
        this.selectedLetter = selectedLetter;
        pager.setSelectedPageNumber(selectedPageNumber);
        this.selectedGenreId = selectedGenreId;        
    }
    
    //<editor-fold defaultstate="collapsed" desc="поиск всех книг fillBooksAll">
    private void fillBooksAll(){
        DataHelper.getInstance().getAllBooks(pager);
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="поиск по жанру fillBooksByGenre">
    
    public String fillBooksByGenre(){
        
    //    row = -1;
                
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        
        selectedGenreId = Long.valueOf(params.get("genre_id"));
        
        submitValues(' ', 1, selectedGenreId);
        DataHelper.getInstance().getBooksByGenre(selectedGenreId, pager);
        
        return "books";
    }
//</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="поиск по букве fillBooksByLetter">
    public String fillBooksByLetter() {
        
      //  row = -1;
                
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        selectedLetter = params.get("letter").charAt(0);
        
        submitValues(selectedLetter, 1, -1);
        
        DataHelper.getInstance().getBooksByLetter(selectedLetter, pager);
        
        return "books";        
    }
//</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="поиск по названию или автору  fillBooksBySearch">
    public String fillBooksBySearch(){
        
      //  row = -1;
             
        submitValues(' ', 1, -1);
        
        if (currentSearchString.trim().length() == 0){
            fillBooksAll();
            return "books";
        }
        
        
        if (selectedSearchType == SearchType.AUTHOR) {
            DataHelper.getInstance().getBooksByAuthor(currentSearchString, pager);            
        } else if (selectedSearchType == SearchType.TITLE) {
            DataHelper.getInstance().getBooksByName(currentSearchString, pager);
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
     //   row=-1;
        editModeView = true;
    }
    
    public void cancelEditModeView(){
        editModeView = false;
        for(Book book : pager.getList()){
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
    //    row = -1;
        cancelEditModeView();
        pager.setBooksCountOnPage(Integer.parseInt(e.getNewValue().toString()));
        pager.setSelectedPageNumber(1);
        DataHelper.getInstance().runCurrentCriteria();
    }
    
    
//</editor-fold>
        public void selectPage() {
    //    row = -1;
        cancelEditModeView();
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        pager.setSelectedPageNumber(Integer.valueOf(params.get("page_number")));
        DataHelper.getInstance().runCurrentCriteria();
    }
/*    
public int getRow(){
    row +=1;
    return row;
}*/
   
    
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
    
    public Pager getPager(){
        return pager;
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

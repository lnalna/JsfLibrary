package ru.javabegin.training.web.beans;


import java.util.List;
import ru.javabegin.training.web.entity.ext.BookExt;



public class Pager {
    
    private static Pager pager;
    
    private int rowIndex;
    
    private Pager(){
        
    }
    
    public static Pager getInstance(){
        if (pager == null){
            pager = new Pager();
        }
        
        return pager;
    }
    
    private long totalBooksCount;
    private BookExt selectedBook;
    private List<BookExt> list;
    private int from;
    private int to;
    
    
    public int getFrom(){
        return from;
    }
    public void setFrom(int from){
        this.from = from;
    }
    
    public int getTo(){
        return to;
    }
    
    public void setTo(int to){
        this.to = to;
    }
    
    public List<BookExt> getList(){
        return list;
    }
    
    public void setList(List<BookExt> list){
        rowIndex = -1;
        this.list = list;
        
    }
    
    public void setTotalBooksCount(long totalBooksCount){
        this.totalBooksCount = totalBooksCount;
    }
    
    public long getTotalBooksCount(){
        return totalBooksCount;
    }
    
    public BookExt getSelectedBook(){
        return selectedBook;
    }
    
    public void setSelectedBook(BookExt selectedBook){
        this.selectedBook = selectedBook;
    }
    
    
    public int getRowIndex(){
        rowIndex+=1;
        return rowIndex;
    }
    
    
    public void setRowIndex(int rowIndex){
        this.rowIndex = rowIndex;
    }
    
    
}

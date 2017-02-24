package ru.javabegin.training.web.beans;


import java.util.List;
import javax.faces.bean.SessionScoped;
import ru.javabegin.training.web.entity.ext.BookExt;


@SessionScoped
public class Pager {
    
    private static Pager pager;
    
   
    
    public Pager(){
        
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
    
    
}

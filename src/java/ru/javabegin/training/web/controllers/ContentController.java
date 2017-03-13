package ru.javabegin.training.web.controllers;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import org.primefaces.event.FileUploadEvent;


@ManagedBean
@SessionScoped
public class ContentController  implements Serializable{
    
        
    @ManagedProperty(value = "#{bookListController}")
    private BookListController bookListController;
    
    public BookListController getBooksListController(){
        return bookListController;
    }
    
    public void setBookListController(BookListController bookListController){
        this.bookListController = bookListController;
    }
    
    public void handleFileUpload(FileUploadEvent event){
        bookListController.getSelectedBook().setUploadedContent(event.getFile().getContents());
    }
    
    
    public boolean isShowContent(){
    //   if (bookListController.getSelectedBook().getUploadedContent() != null) {
            return true;
      //  }

        //return false;
    }
    
    public byte[] getUploadedContent(){
        return bookListController.getSelectedBook().getUploadedContent();
    }
}

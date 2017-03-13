package ru.javabegin.training.web.controllers;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import org.primefaces.event.FileUploadEvent;


@ManagedBean
@SessionScoped
public class ContentController  implements Serializable{
    
    
    private byte[] uploadedContent;
    private boolean showContent = false;
        
    @ManagedProperty(value = "#{bookListController}")
    private BookListController bookListController;
    
    public BookListController getBooksListController(){
        return bookListController;
    }
    
    public void setBookListController(BookListController bookListController){
        this.bookListController = bookListController;
    }
    
    public void handleFileUpload(FileUploadEvent event){
        uploadedContent = event.getFile().getContents();
        bookListController.getSelectedBook().setUploadedContent(uploadedContent);
    }
    
    
    public boolean isShowContent(){
     if (bookListController.getSelectedBook().getUploadedContent() != null) {
            showContent = true;
        }
     else showContent = false;

        return showContent;
    }
    
    public byte[] getUploadedContent(){
        return uploadedContent;
    }
}

package ru.javabegin.training.web.controllers;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import org.primefaces.event.FileUploadEvent;


@ManagedBean
@SessionScoped
public class ContentController  implements Serializable{
    
    private byte[] uploadedContent;
    private boolean showContent;
    
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
        if (uploadedContent!=null && uploadedContent.length>0){
            showContent = true;
            
            bookListController.getSelectedBook().setContent(uploadedContent);
            bookListController.getSelectedBook().setContentEdited(true);
        }        
        
      //  clear();
    }
    
    
    public void clear() {
        uploadedContent = null;
        showContent = false;
    }
    
    public boolean isShowContent(){
        return showContent;
    }
    
    public byte[] getUploadedContent(){
        return uploadedContent;
    }
    
}

package ru.javabegin.training.web.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;


@ManagedBean
@SessionScoped
public class ImageController implements Serializable{
    
    private final int IMAGE_MAX_SIZE = 204800;
    private byte[] uploadedImage;
    
    @ManagedProperty(value = "#{bookListController}")
    private BookListController bookListController;
    
    public BookListController getBookListController(){
        return bookListController;
    }
    
    public void setBookListController(BookListController bookListController){
        this.bookListController = bookListController;
    }
    
    public StreamedContent getDefaultImage(){
        return getStreamedContent(bookListController.getSelectedBook().getImage());
    }
    
    public StreamedContent getUploadedImage(){
        return getStreamedContent(bookListController.getSelectedBook().getUploadedImage());
    }
    
    public void handleFileUpload(FileUploadEvent event){
         uploadedImage = event.getFile().getContents();
         bookListController.getSelectedBook().setUploadedImage(uploadedImage);
    }
    
    private DefaultStreamedContent getStreamedContent(byte[] image){
        
        if (image == null){
            return null;
        }
        
        InputStream inputStream = null;
        
        try {
            inputStream = new ByteArrayInputStream(image);
            return new DefaultStreamedContent(inputStream, "image/png");
        } finally {
            try {
                inputStream.close();
            } catch (IOException ex){
                Logger.getLogger(ImageController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    
    public int getImageMaxSize(){
        return IMAGE_MAX_SIZE;
    }    
 
    public byte[] getUploadedImageBytes(){
        return uploadedImage;
    }
}

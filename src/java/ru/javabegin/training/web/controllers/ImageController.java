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
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;


@ManagedBean
@SessionScoped
public class ImageController implements Serializable{
    
    private final int IMAGE_MAX_SIZE = 204800;
    private byte[] uploadedImage;
    private boolean showImage;
    
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
        return getStreamedContent(uploadedImage);
    }
    
    public void handleFileUpload(FileUploadEvent event){
        uploadedImage = event.getFile().getContents();
        
        if (uploadedImage != null){
            showImage = true;
            
            bookListController.getSelectedBook().setImage(uploadedImage);
            bookListController.getSelectedBook().setImageEdited(showImage);
        }
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
    
    public void clearImage(){
        uploadedImage = null;
        RequestContext.getCurrentInstance().execute("dlgEditBook.hide()");
    }
    
    public void updateImage(){
        if (uploadedImage != null){
            bookListController.getSelectedBook().setImage(uploadedImage);
            bookListController.getSelectedBook().setImageEdited(true);
        }
        clearImage();
    }
    
    public int getImageMaxSize(){
        return IMAGE_MAX_SIZE;
    }
    
    public byte[] getUploadedImageBytes(){
        return uploadedImage;
    }
    public boolean isShowImage(){
        return showImage;
    }
    
    public void clear(){
        uploadedImage = null;
        showImage = false;
    }
}

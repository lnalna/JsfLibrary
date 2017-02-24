/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.javabegin.training.web.entity.ext;

import ru.javabegin.training.web.entity.Book;
import java.io.Serializable;

/**
 *
 * @author nik
 */
public class BookExt extends Book implements Serializable{
    
    private boolean imageEdited;
    private boolean contentEdited;

    public void setImageEdited(boolean imageEdited) {
        this.imageEdited = imageEdited;
    }

    public boolean isImageEdited() {
        return imageEdited;
    }

    public void setContentEdited(boolean contentEdited) {
        this.contentEdited = contentEdited;
    }

    public boolean isContentEdited() {
        return contentEdited;
    }
    
}

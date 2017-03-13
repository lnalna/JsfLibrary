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

    public BookExt(){
        setVoteCount(0L);
        setRating(0);
    }
    
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
    
    public Book getBook(){
        Book book = new Book();
        book.setAuthor(getAuthor());
        book.setContent(getContent());// записываем загруженный контент
        //book.setDescr(getDescr());
        book.setGenre(getGenre());
        book.setImage(getImage());// записываем загруженное изображение
        book.setIsbn(getIsbn());
        book.setName(getName());
        book.setPageCount(getPageCount());
        book.setPublishYear(getPublishYear());
        book.setPublisher(getPublisher());
        book.setRating(getRating());
        book.setVoteCount(getVoteCount());
        book.setVotes(getVotes());
        return book;
    }
}

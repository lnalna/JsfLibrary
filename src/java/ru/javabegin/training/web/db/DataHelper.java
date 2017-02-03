/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.javabegin.training.web.db;


import org.hibernate.SessionFactory;
import ru.javabegin.training.web.entity.HibernateUtil;
import org.hibernate.Session;
import java.util.List;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import ru.javabegin.training.web.entity.Author;
import ru.javabegin.training.web.entity.Book;
import ru.javabegin.training.web.entity.Genre;
import ru.javabegin.training.web.entity.Publisher;

/**
 *
 * @author nik
 */
public class DataHelper {
    
    private SessionFactory sessionFactory = null;
    private static DataHelper dataHelper;
    
    
    private DataHelper(){
        sessionFactory = HibernateUtil.getSessionFactory();
    }
    
    public static DataHelper getInstance(){
        return dataHelper == null ? new DataHelper() : dataHelper;
    }
    
    private Session getSession(){
        return sessionFactory.getCurrentSession();
    }
    
    public List<Book> getAllBooks(){
        return getSession().createCriteria(Book.class).list();
    }
    
    public List<Genre> getAllGenres(){
        return getSession().createCriteria(Genre.class).list();
    }
    
    public List<Author> getAllAuthors(){
        return getSession().createCriteria(Author.class).list();
    }
    
    public List<Publisher> getAllPublishers(){
        return getSession().createCriteria(Publisher.class).list();
    }
    
    public List<Book> getBooksByGenre(Long genreId){
        return getSession().createCriteria(Book.class).add(Restrictions.eq("genre.id", genreId)).list();
    }
    
    public List<Book> getBooksByLetter(Character letter){
        return getBookList("name", letter.toString(), MatchMode.START);
    }
    
    public List<Book> getBooksByAuthor(String authorName){
        return getBookList("author", authorName, MatchMode.ANYWHERE);
    }
    
    public List<Book> getBooksByName(String bookName) {
        return getBookList("name", bookName, MatchMode.ANYWHERE);
    }
    
    public byte[] getContent(Long id){
        return (byte[]) getFieldValue("content", id);
    }
    
    public byte[] getImage(Long id){
        return(byte[]) getFieldValue("image",id);
    }
    
    public Author getAuthor(long id){
        return (Author) getSession().get(Author.class, id);
    }
    
    private List<Book> getBookList(String field, String value, MatchMode matchMode){
        return getSession().createCriteria(Book.class).add(Restrictions.ilike(field, value, matchMode)).list();
    }
    
    private Object getFieldValue(String field, long id){
        return getSession().createCriteria(Book.class).setProjection(Projections.property(field)).add(Restrictions.eq("id", id)).uniqueResult();
    }
}

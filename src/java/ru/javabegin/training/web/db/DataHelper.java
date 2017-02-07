/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.javabegin.training.web.db;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import ru.javabegin.training.web.beans.Pager;
import ru.javabegin.training.web.entity.Author;
import ru.javabegin.training.web.entity.Book;
import ru.javabegin.training.web.entity.Genre;
import ru.javabegin.training.web.entity.Publisher;
import ru.javabegin.training.web.entity.HibernateUtil;



public class DataHelper {
    
    private SessionFactory sessionFactory = null;
    private static DataHelper dataHelper;
    private DetachedCriteria currentCriteria;
    private Pager currentPager;
    
    
    private DataHelper(){
        sessionFactory = HibernateUtil.getSessionFactory();
    }
    
    public static DataHelper getInstance(){
        if (dataHelper == null){
            dataHelper = new DataHelper();
        }
        return dataHelper;
    }
    
    private Session getSession(){
        return sessionFactory.getCurrentSession();
    }
    
     public List<Genre> getAllGenres(){
        return getSession().createCriteria(Genre.class).list();
    }
    
    public List<Author> getAllAuthors(){
        return getSession().createCriteria(Author.class).list();
    }
    
     public Author getAuthor(long id){
        return (Author) getSession().get(Author.class, id);
    }
    
    public List<Publisher> getAllPublishers(){
        return getSession().createCriteria(Publisher.class).list();
    }
    
    public void getAllBooks(Pager pager){
        currentPager = pager;
        
        Criteria criteria = getSession().createCriteria(Book.class);
        Long total =  (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
        currentPager.setTotalBooksCount(total);
        
        currentCriteria = DetachedCriteria.forClass(Book.class);
        runCurrentCriteria();
    }
    
   
    
    public void getBooksByGenre(Long genreId, Pager pager) {
        currentPager = pager;

        Criterion criterion = Restrictions.eq("genre.id", genreId);

        Criteria criteria = getSession().createCriteria(Book.class);
        Long total = (Long) criteria.add(criterion).setProjection(Projections.rowCount()).uniqueResult();
        currentPager.setTotalBooksCount(total);

        currentCriteria = DetachedCriteria.forClass(Book.class);
        currentCriteria.add(criterion);

        runCurrentCriteria();
    }
    
    public void getBooksByLetter(Character letter, Pager pager) {
        currentPager = pager;

        Criterion criterion = Restrictions.ilike("name", letter.toString(), MatchMode.START);

        Criteria criteria = getSession().createCriteria(Book.class);
        Long total = (Long) criteria.add(criterion).setProjection(Projections.rowCount()).uniqueResult();
        currentPager.setTotalBooksCount(total);
        
        currentCriteria = DetachedCriteria.forClass(Book.class);
        currentCriteria.add(criterion);

        runCurrentCriteria();
    }
    
    public void getBooksByAuthor(String authorName, Pager pager) {
        currentPager = pager;
        
        Criterion criterion = Restrictions.ilike("author.fio", authorName, MatchMode.ANYWHERE);

        Criteria criteria = getSession().createCriteria(Book.class, "book").createAlias("book.author", "author");
        Long total = (Long) criteria.add(criterion).setProjection(Projections.rowCount()).uniqueResult();
        currentPager.setTotalBooksCount(total);
        
        currentCriteria = DetachedCriteria.forClass(Book.class, "book").createAlias("book.author", "author");;
        currentCriteria.add(criterion);

        runCurrentCriteria();
    }

    
    public void getBooksByName(String bookName, Pager pager) {
        currentPager = pager;
        
        Criterion criterion = Restrictions.ilike("name", bookName, MatchMode.ANYWHERE);
        Criteria criteria = getSession().createCriteria(Book.class);
        Long total = (Long) criteria.add(criterion).setProjection(Projections.rowCount()).uniqueResult();
        currentPager.setTotalBooksCount(total);

        
        currentCriteria = DetachedCriteria.forClass(Book.class);
        currentCriteria.add(criterion);

        runCurrentCriteria();
    }
    
   public byte[] getContent(long id) {
        Criteria criteria = getSession().createCriteria(Book.class);
        criteria.setProjection(Property.forName("content"));
        criteria.add(Restrictions.eq("id", id));
        return (byte[]) criteria.uniqueResult();
    }
   
   public byte[] getImage(long id) {
        Criteria criteria = getSession().createCriteria(Book.class);
        criteria.setProjection(Property.forName("image"));
        criteria.add(Restrictions.eq("id", id));
        return (byte[]) criteria.uniqueResult();
    }
    
   public void runCurrentCriteria() {
        Criteria criteria = currentCriteria.addOrder(Order.asc("name")).getExecutableCriteria(getSession());
        List<Book> list = criteria.setFirstResult(currentPager.getFrom()).setMaxResults(currentPager.getTo()).list();
        currentPager.setList(list);
    }   
}

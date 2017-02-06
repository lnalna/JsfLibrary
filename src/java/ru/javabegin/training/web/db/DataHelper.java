/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.javabegin.training.web.db;


import org.hibernate.SessionFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
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
import ru.javabegin.training.web.beans.Pager;

/**
 *
 * @author nik
 */
public class DataHelper {
    
    private SessionFactory sessionFactory = null;
    private static DataHelper dataHelper;
    private DetachedCriteria currentCriteria;
    private Pager currentPager;
    
    
    private DataHelper(){
        sessionFactory = HibernateUtil.getSessionFactory();
    }
    
    public static DataHelper getInstance(){
        return dataHelper == null ? new DataHelper() : dataHelper;
    }
    
    private Session getSession(){
        return sessionFactory.getCurrentSession();
    }
    
    public void getAllBooks(Pager pager){
        currentPager = pager;
        
        Criteria criteria = getSession().createCriteria(Book.class);
        Integer total = (Integer) criteria.setProjection(Projections.rowCount()).uniqueResult();
        currentPager.setTotalBooksCount(total);
        
        currentCriteria = DetachedCriteria.forClass(Book.class);
        runCurrentCriteria();
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
    
    public void getBooksByGenre(Long genreId, Pager pager) {
        currentPager = pager;

        Criterion criterion = Restrictions.eq("genre.id", genreId);

        Criteria criteria = getSession().createCriteria(Book.class);
        Integer total = (Integer) criteria.add(criterion).setProjection(Projections.rowCount()).uniqueResult();
        currentPager.setTotalBooksCount(total);

        currentCriteria = DetachedCriteria.forClass(Book.class);
        currentCriteria.add(criterion);

        runCurrentCriteria();
    }
    
    public void getBooksByLetter(Character letter, Pager pager) {
        currentPager = pager;

        Criterion criterion = Restrictions.ilike("name", letter.toString(), MatchMode.START);

        Criteria criteria = getSession().createCriteria(Book.class);
        Integer total = (Integer) criteria.add(criterion).setProjection(Projections.rowCount()).uniqueResult();
        currentPager.setTotalBooksCount(total);
        
        currentCriteria = DetachedCriteria.forClass(Book.class);
        currentCriteria.add(criterion);

        runCurrentCriteria();
    }
    
    public void getBooksByAuthor(String authorName, Pager pager) {
        currentPager = pager;
        
        Criterion criterion = Restrictions.ilike("author.fio", authorName, MatchMode.ANYWHERE);

        Criteria criteria = getSession().createCriteria(Book.class, "book").createAlias("book.author", "author");
        Integer total = (Integer) criteria.add(criterion).setProjection(Projections.rowCount()).uniqueResult();
        currentPager.setTotalBooksCount(total);
        
        currentCriteria = DetachedCriteria.forClass(Book.class, "book").createAlias("book.author", "author");;
        currentCriteria.add(criterion);

        runCurrentCriteria();
    }

    
    public void getBooksByName(String bookName, Pager pager) {
        currentPager = pager;
        
        Criterion criterion = Restrictions.ilike("name", bookName, MatchMode.ANYWHERE);
        Criteria criteria = getSession().createCriteria(Book.class);
        Integer total = (Integer) criteria.add(criterion).setProjection(Projections.rowCount()).uniqueResult();
        currentPager.setTotalBooksCount(total);

        
        currentCriteria = DetachedCriteria.forClass(Book.class);
        currentCriteria.add(criterion);

        runCurrentCriteria();
    }
    
   public byte[] getContent(Long id) {
        Criteria criteria = getSession().createCriteria(Book.class);
        criteria.setProjection(Property.forName("content"));
        criteria.add(Restrictions.eq("id", id));
        return (byte[]) criteria.uniqueResult();
    }
    
   public void runCurrentCriteria() {
        Criteria criteria = currentCriteria.addOrder(Order.asc("name")).getExecutableCriteria(getSession());
        List<Book> list = criteria.setFirstResult(currentPager.getFrom()).setMaxResults(currentPager.getTo()).list();
        currentPager.setList(list);
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

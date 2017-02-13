package ru.javabegin.training.web.db;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import ru.javabegin.training.web.beans.Pager;
import ru.javabegin.training.web.entity.Author;
import ru.javabegin.training.web.entity.Book;
import ru.javabegin.training.web.entity.Genre;
import ru.javabegin.training.web.entity.Publisher;
import ru.javabegin.training.web.entity.HibernateUtil;



public class DataHelper {
    
    private Pager pager = Pager.getInstance();
    private SessionFactory sessionFactory = null;
    private static DataHelper dataHelper;
    private DetachedCriteria bookListCriteria;
    private DetachedCriteria booksCountCriteria;
    private DetachedCriteria currentCriteria;
    private Pager currentPager;
    private ProjectionList bookProjection;
    
    
    private DataHelper(){
        
        prepareCriterias();
        
        sessionFactory = HibernateUtil.getSessionFactory();
        
        bookProjection = Projections.projectionList();
        bookProjection.add(Projections.property("id"), "id");
        bookProjection.add(Projections.property("name"), "name");
        bookProjection.add(Projections.property("image"), "image");
        bookProjection.add(Projections.property("genre"), "genre");
        bookProjection.add(Projections.property("pageCount"), "pageCount");
        bookProjection.add(Projections.property("isbn"), "isbn");
        bookProjection.add(Projections.property("publisher"), "publisher");
        bookProjection.add(Projections.property("author"), "author");
        bookProjection.add(Projections.property("publishYear"), "publishYear");
        bookProjection.add(Projections.property("description"), "description");
                
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
    
    public void getAllBooks(){
        prepareCriterias();
        populateList();
    }
    
   
    
    public void getBooksByGenre(Long genreId) {
      
        Criterion criterion = Restrictions.eq("genre.id", genreId);

        prepareCriterias(criterion);
        populateList();
    }
    
    public void getBooksByLetter(Character letter) {
        
        Criterion criterion = Restrictions.ilike("b.name", letter.toString(), MatchMode.START);

         prepareCriterias(criterion);
         populateList();
    }
    
    public void getBooksByAuthor(String authorName) {
      /*  currentPager = pager;            
        
        Criterion criterion = Restrictions.ilike("author.fio", authorName, MatchMode.ANYWHERE);

        Criteria criteria = getSession().createCriteria(Book.class, "book").createAlias("book.author", "author");
        Long total = (Long) criteria.add(criterion).setProjection(Projections.rowCount()).uniqueResult();
        currentPager.setTotalBooksCount(total);
        
        currentCriteria = DetachedCriteria.forClass(Book.class, "book").createAlias("book.author", "author");;
        currentCriteria.add(criterion);

        runCurrentCriteria();
              */
        Criterion criterion = Restrictions.ilike("author.fio", authorName, MatchMode.ANYWHERE);

        prepareCriterias(criterion);
        populateList();
        
    }

    
    public void getBooksByName(String bookName) {
                
        Criterion criterion = Restrictions.ilike("b.name", bookName, MatchMode.ANYWHERE);
        
        prepareCriterias(criterion);
        populateList();
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
    
   
   private void runBookListCriteria() {
        Criteria criteria = bookListCriteria.getExecutableCriteria(getSession());
        criteria.addOrder(Order.asc("b.name")).setProjection(bookProjection).setResultTransformer(Transformers.aliasToBean(Book.class));

        criteria.setFirstResult(pager.getFrom()).setMaxResults(pager.getTo());

        List<Book> list = criteria.list();
        pager.setList(list);
    }
   
    private void runCountCriteria(){
        Criteria criteria = booksCountCriteria.getExecutableCriteria(getSession());
        Long total = (Long)criteria.setProjection(Projections.rowCount()).uniqueResult();
        pager.setTotalBooksCount(total);
    }
    
    public void updateBook(Book book){
        Query query = getSession().createQuery("update Book set name = :name, "
                + " pageCount = :pageCount, "
                + " isbn = :isbn, "
                + " genre = :genre, "
                + " author = :author, "
                + " publishYear = :publishYear, "
                + " publisher = :publisher, "
                + " description = :description "
                + " where id = :id");
        
        query.setParameter("name", book.getName());
        query.setParameter("pageCount", book.getPageCount());
        query.setParameter("isbn", book.getIsbn());
        query.setParameter("genre", book.getGenre());
        query.setParameter("author", book.getAuthor());
        query.setParameter("publishYear", book.getPublishYear());
        query.setParameter("publisher", book.getPublisher());
        query.setParameter("description", book.getDescription());
        query.setParameter("id", book.getId());    
        
        int result = query.executeUpdate();
    }
    
    public void deleteBook(Book book) {
        Query query = getSession().createQuery("delete from Book where id = :id");
        query.setParameter("id", book.getId());
        int result = query.executeUpdate();
    }
    
    private void prepareCriterias(Criterion criterion) {
        bookListCriteria = DetachedCriteria.forClass(Book.class, "b");
        createAliases(bookListCriteria);
        bookListCriteria.add(criterion);

        booksCountCriteria = DetachedCriteria.forClass(Book.class, "b");
        createAliases(booksCountCriteria);
        booksCountCriteria.add(criterion);
    }

    private void prepareCriterias() {
        bookListCriteria = DetachedCriteria.forClass(Book.class, "b");
        createAliases(bookListCriteria);

        booksCountCriteria = DetachedCriteria.forClass(Book.class, "b");
        createAliases(booksCountCriteria);
    }
    
    public void runCurrentCriteria() {
        Criteria criteria = currentCriteria.addOrder(Order.asc("name")).getExecutableCriteria(getSession());
        List<Book> list = criteria.setFirstResult(currentPager.getFrom()).setMaxResults(currentPager.getTo()).list();
        currentPager.setList(list);
    } 
    
   
   
    
    private void createAliases(DetachedCriteria criteria){
        criteria.createAlias("b.author", "author");
        criteria.createAlias("b.genre", "genre");
        criteria.createAlias("b.publisher", "publisher");
    }
    
    public void refreshList(){
        runCountCriteria();
        runBookListCriteria();
    }
    
    public void populateList() {
        runCountCriteria();
        runBookListCriteria();
    }
}

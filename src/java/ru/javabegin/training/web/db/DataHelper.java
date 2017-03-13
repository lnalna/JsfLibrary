package ru.javabegin.training.web.db;

import java.util.HashMap;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
import ru.javabegin.training.web.entity.Book;
import ru.javabegin.training.web.entity.ext.AuthorExt;
import ru.javabegin.training.web.entity.ext.BookExt;
import ru.javabegin.training.web.entity.ext.GenreExt;
import ru.javabegin.training.web.entity.ext.PublisherExt;
import ru.javabegin.training.web.entity.HibernateUtil;
import ru.javabegin.training.web.entity.Vote;



public class DataHelper {
    
    private SessionFactory sessionFactory = null;
        
    private DetachedCriteria bookListCriteria;
    private DetachedCriteria booksCountCriteria;
    private DetachedCriteria currentCriteria;  
    private ProjectionList bookProjection;
    
    private Pager pager;
    
    public DataHelper(Pager pager){
        
        this.pager = pager;
        
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
        bookProjection.add(Projections.property("rating"), "rating");
        bookProjection.add(Projections.property("voteCount"), "voteCount");
        
        getAllBooks();
                
    }
    
    private Session getSession(){
        return sessionFactory.getCurrentSession();
    }
    
     public List<GenreExt> getAllGenres(){
        return getSession().createCriteria(GenreExt.class).list();
    }
    
    public List<AuthorExt> getAllAuthors(){
        return getSession().createCriteria(AuthorExt.class).list();
    }
    
     public AuthorExt getAuthor(long id){
        return (AuthorExt) getSession().get(AuthorExt.class, id);
    }
    
    public List<PublisherExt> getAllPublishers(){
        return getSession().createCriteria(PublisherExt.class).list();
    }
    
    public void getBooksByRate() {
        prepareOrderedCriterias("rating");
        populateList();
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
    
    public BookExt getBookById(int bookId){
        getAllBooks();
        
        List<BookExt> list = pager.getList();
        
        BookExt temp = null;
        
        for(BookExt bookExt :list)
            if(bookExt.getId()== bookId){
                temp = bookExt;
                break;
            }
            
        return temp;
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
        criteria.addOrder(Order.asc("b.name")).setProjection(bookProjection).setResultTransformer(Transformers.aliasToBean(BookExt.class));

        criteria.setFirstResult(pager.getFrom()).setMaxResults(pager.getTo());

        List<BookExt> list = criteria.list();
        pager.setList(list);
    }
  
   
    private void runCountCriteria(){
        Criteria criteria = booksCountCriteria.getExecutableCriteria(getSession());
        Long total = (Long)criteria.setProjection(Projections.rowCount()).uniqueResult();       
        pager.setTotalBooksCount(total);
    }
    
    public void updateBook(BookExt book){
        
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("update Book ");
        queryBuilder.append("set name = :name, ");
        
        if (book.isContentEdited())
            queryBuilder.append("content = :content, ");
        
        queryBuilder.append("pageCount = :pageCount, ");
        queryBuilder.append("isbn = :isbn, ");
        queryBuilder.append("genre = :genre, ");
        queryBuilder.append("author = :author, ");
        queryBuilder.append("publishYear = :publishYear, ");
        queryBuilder.append("publisher = :publisher, ");
        
        if(book.isImageEdited())
            queryBuilder.append("image = :image, ");
        
        queryBuilder.append("description = :description ");
        queryBuilder.append(" where id = :id");
        
       /* Query query = getSession().createQuery("update Book set name = :name, "
                + " pageCount = :pageCount, "
                + " isbn = :isbn, "
                + " genre = :genre, "
                + " author = :author, "
                + " publishYear = :publishYear, "
                + " publisher = :publisher, "
                + " description = :description "
                + " where id = :id");
*/
       Query query = getSession().createQuery(queryBuilder.toString());
        
        query.setParameter("name", book.getName());
        
        if (book.isContentEdited()){
            query.setParameter("content", book.getContent());
        }
        query.setParameter("pageCount", book.getPageCount());
        query.setParameter("isbn", book.getIsbn());
        query.setParameter("genre", book.getGenre());
        query.setParameter("author", book.getAuthor());
        query.setParameter("publishYear", book.getPublishYear());
        query.setParameter("publisher", book.getPublisher());
        query.setParameter("description", book.getDescription());
        query.setParameter("id", book.getId());    
        
        if (book.isImageEdited()){
            query.setParameter("image", book.getImage());
        }
        int result = query.executeUpdate();
    }
    
    public void addBook(Book book) {
        getSession().save(book);
    }
    
     public void rateBook(Book book, String username, int currentRatingVoice) {
        Vote vote = new Vote();
        vote.setBook(book);
        vote.setUsername(username);
        
        //сейчас здесь правилно - вставляется рейтинг текущий номер звездочик
        vote.setValue(currentRatingVoice);
        //vote.setValue(4);
        getSession().save(vote);

        updateBookRate(book);


    }
     
     private void updateBookRate(Book book) {


        Query query = getSession().createQuery("select new map(round(avg(value)) as rating, count(value) as voteCount)  from Vote v where v.book.id=:id");
        long bookId = book.getId();
        query.setParameter("id", bookId);

        List list = query.list();

        HashMap<String, Object> map = (HashMap<String, Object>) list.get(0);

        long voteCount = Long.valueOf(map.get("voteCount").toString());
     //   int rating =5;//(int) Long.valueOf(map.get("rating").toString()).intValue();
        int rating = Double.valueOf(map.get("rating").toString()).intValue();

        query = getSession().createQuery("update Book set rating = :rating, "
                + " voteCount = :voteCount"
                + " where id = :id");

        query.setParameter("rating", rating);
        query.setParameter("voteCount", voteCount);
        long temp = book.getId();
        query.setParameter("id", temp);

        int result = query.executeUpdate();
        
        
        //Если коммит будет срабатывать здесь, то каждый голос будет учитываться за один
        //а не за два
        sessionFactory.getCurrentSession().getTransaction().commit();
        

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
    
    private void prepareOrderedCriterias(String field) {
        bookListCriteria = DetachedCriteria.forClass(Book.class, "b");
        bookListCriteria.addOrder(Order.desc("b."+field));
        createAliases(bookListCriteria);

        booksCountCriteria = DetachedCriteria.forClass(Book.class, "b");
        createAliases(booksCountCriteria);
    }
    
      
   
   
    
    private void createAliases(DetachedCriteria criteria){
        criteria.createAlias("b.author", "author");
        criteria.createAlias("b.genre", "genre");
        criteria.createAlias("b.publisher", "publisher");
     //   criteria.createAlias("b.id", "id");
    }
    
       
    public void populateList() {
        runCountCriteria();
        runBookListCriteria();
    }
}

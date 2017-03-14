package ru.javabegin.training.web.controllers;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;
import java.io.Serializable;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import ru.javabegin.training.web.beans.Pager;
import ru.javabegin.training.web.entity.ext.AuthorExt;
import ru.javabegin.training.web.db.DataHelper;
import ru.javabegin.training.web.comparators.ListComparator;



@ManagedBean
@SessionScoped
public class AuthorController implements Serializable, Converter{
    
    private List<SelectItem> selectItems = new ArrayList<SelectItem>();
    private Map<Long, AuthorExt> authorMap;
    private List<AuthorExt> authorList;
    
    private Pager pager;
    private DataHelper dataHelper;
    
    @ManagedProperty(value = "#{bookListController}")
    private BookListController bookListController;
    
    
    @PostConstruct
    public void init(){
        pager = bookListController.getPager();
        dataHelper = bookListController.getDataHelper();
        
        authorMap = new HashMap<Long, AuthorExt>();
        authorList = dataHelper.getAllAuthors();
        Collections.sort(authorList, ListComparator.getInstance());
        
        authorList.add(0, createEmptyAuthor());
        
        for(AuthorExt authorExt : authorList){
            authorMap.put(authorExt.getId(), authorExt);
            selectItems.add(new SelectItem(authorExt, authorExt.getFio()));
        }
    }
    
   
    
    public List<AuthorExt> getAuthorList(){
        return authorList;
    }
    
    public List<SelectItem> getSelectItems(){
        return selectItems;
    }
    
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return authorMap.get(Long.valueOf(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return ((AuthorExt)value).getId().toString();
    }
    
    public BookListController getBookListController() {
        return bookListController;
    }

    public void setBookListController(BookListController bookListController) {
        this.bookListController = bookListController;
    }
    
    private AuthorExt createEmptyAuthor(){
        
        AuthorExt author = new AuthorExt();
        author.setId(-1L);
        author.setFio("");
        
        return author;
    }
    
}

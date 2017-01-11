package ru.javabegin.training.web.controllers;

import java.util.Map;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.io.Serializable;
import javax.faces.context.FacesContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import ru.javabegin.training.web.enums.SearchType;


@ManagedBean
@SessionScoped
public class SearchController implements Serializable{
    
    private SearchType searchType;
    private static Map<String, SearchType> searchList = new HashMap<String, SearchType>();
    
    public SearchController(){
        ResourceBundle bundle = ResourceBundle.getBundle("ru.javabegin.training.web.nls.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
        searchList.put(bundle.getString("author_name"), searchType.AUTHOR);
        searchList.put(bundle.getString("book_name"), searchType.TITLE);
    }
    
    public SearchType getSearchType(){
        return searchType;
    }
    
    public Map<String, SearchType> getSearchList() {
        return searchList;
    }
}

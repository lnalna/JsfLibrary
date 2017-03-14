package ru.javabegin.training.web.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import ru.javabegin.training.web.beans.Pager;
import ru.javabegin.training.web.db.DataHelper;
import ru.javabegin.training.web.entity.ext.GenreExt;
import ru.javabegin.training.web.comparators.ListComparator;


@ManagedBean
@SessionScoped
public class GenreController implements Serializable, Converter {

    private List<SelectItem> selectItems = new ArrayList<SelectItem>();
    private Map<Long, GenreExt> genreMap;
    private List<GenreExt> genreList;
    private Pager pager;
    private DataHelper dataHelper;
    @ManagedProperty(value = "#{bookListController}")
    private BookListController bookListController;

    

    @PostConstruct
    public void init(){
        pager = bookListController.getPager();
        dataHelper = bookListController.getDataHelper();
        
        genreMap = new HashMap<Long, GenreExt>();
        genreList = dataHelper.getAllGenres();
        Collections.sort(genreList, ListComparator.getInstance());
        
        genreList.add(0, createEmptyGenre());

        for (GenreExt genre : genreList) {
            genreMap.put(genre.getId(), genre);
            selectItems.add(new SelectItem(genre, genre.getName()));
        }

    }

    public List<SelectItem> getSelectItems() {
        return selectItems;
    }

    // 
    public List<GenreExt> getGenreList() {
        return genreList;
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return genreMap.get(Long.valueOf(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return ((GenreExt) value).getId().toString();
    }
    
    public BookListController getBookListController() {
        return bookListController;
    }

    public void setBookListController(BookListController bookListController) {
        this.bookListController = bookListController;
    }
    
    private GenreExt createEmptyGenre(){
        
        GenreExt genre = new GenreExt();
        genre.setId(-1L);
        genre.setName("");
        
        return genre;
    }
}
package ru.javabegin.training.web.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import ru.javabegin.training.web.db.DataHelper;
import ru.javabegin.training.web.entity.ext.GenreExt;
import ru.javabegin.training.web.comparators.ListComparator;


@ManagedBean(eager = false)
@ApplicationScoped
public class GenreController implements Serializable, Converter {

    private List<SelectItem> selectItems = new ArrayList<SelectItem>();
    private Map<Long, GenreExt> genreMap;
    private List<GenreExt> genreList;

    public GenreController() {

        genreMap = new HashMap<Long, GenreExt>();
        genreList = DataHelper.getInstance().getAllGenres();
        Collections.sort(genreList, ListComparator.getInstance());

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
}
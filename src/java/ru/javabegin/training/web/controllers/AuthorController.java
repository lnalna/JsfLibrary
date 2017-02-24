/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import ru.javabegin.training.web.entity.ext.AuthorExt;
import ru.javabegin.training.web.db.DataHelper;
import ru.javabegin.training.web.comparators.ListComparator;



@ManagedBean(eager = false)
@ApplicationScoped
public class AuthorController implements Serializable, Converter{
    
    private List<SelectItem> selectItems = new ArrayList<SelectItem>();
    private Map<Long, AuthorExt> authorMap;
    private List<AuthorExt> authorList;
    
    
    public AuthorController(){
        authorMap = new HashMap<Long, AuthorExt>();
        authorList = DataHelper.getInstance().getAllAuthors();
        Collections.sort(authorList, ListComparator.getInstance());
        
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
    
}

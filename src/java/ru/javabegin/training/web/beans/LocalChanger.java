package ru.javabegin.training.web.beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.Locale;
import java.io.Serializable;
//import javax.faces.context.FacesContext;


@ManagedBean(eager=true)
@SessionScoped
public class LocalChanger implements Serializable{
    
    private Locale currentLocale = new Locale("ru");//FacesContext.getCurrentInstance().getViewRoot().getLocale();
    
    public LocalChanger(){
        
    }
    
    public void changeLocale(String localeCode){
        currentLocale = new Locale(localeCode);
    }
    
    public Locale getCurrentLocale(){
        return currentLocale;
    }
    
}

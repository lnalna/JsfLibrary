package ru.javabegin.training.web.controllers;

import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;


public class SessionUtils {
    
    public static SessionUtils sessionObjects;
    
    public static SessionUtils getInstance(){
        
        if (sessionObjects == null){
            sessionObjects = new SessionUtils();
        }
        
        return sessionObjects;
    }
    
    private SessionUtils(){
        
    }
    
    private ResourceBundle bundle = ResourceBundle.getBundle("ru.javabegin.training.web.nls.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
    
    
    public boolean isNullOrEmpty(Object obj){
        if (obj == null || obj.toString().equals("")){
            return true;
        }
        
        return false;
    }
    
    public void failValidation(String message_key) {
        FacesContext.getCurrentInstance().validationFailed();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString(message_key), bundle.getString("error")));
    }

    public String getBundleMessage(String message_key) {
        return bundle.getString(message_key);
    }

    public void showMessage(String message_key) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(bundle.getString(message_key)));

    }

    public String getUserName() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        return facesContext.getExternalContext().getUserPrincipal().getName();
    }
    
    
}

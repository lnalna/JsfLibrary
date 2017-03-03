package ru.javabegin.training.web.beans;

import java.io.Serializable;
import static java.lang.Thread.sleep;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;



@ManagedBean
@SessionScoped
public class User implements Serializable {
    
    private String username;
    private String password;
    
    public User(){
        
    }
    
    public String getUsername(){
        return username;
    }
    
    public void setUsername(String username){
        this.username = username;
    }
    
    public String getPassword(){
        return password;
    }
    
    public void setPassword(String password){
        this.password = password;
    }
    
    public String login(){
        
        try{
             HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
            }

            

            if (request.getUserPrincipal()==null || (request.getUserPrincipal()!=null && !request.getUserPrincipal().getName().equals(username))) {
                request.logout();
                request.login(username, password);
            }
            
            return "books";
            
        } catch (ServletException ex){
            ResourceBundle bundle = ResourceBundle.getBundle("ru.javabegin.training.web.nls.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage message = new FacesMessage(bundle.getString("login_error"));
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            context.addMessage("login_form", message);  
        }
        return "index";
    }
    
    public String logout() {
        String result = "/index.xhtml?faces-redirect=true";

        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

        try {
            request.logout();
        } catch (ServletException e) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, e);
        }

        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

        return result;
    }
    
    public String goHome(){
        try {
            sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "/index.xhtml?faces-redirect=true";
    }
    
    public String goBooks(){
        return "/pages/books.xhtml?faces-redirect=true";
    }
    
}

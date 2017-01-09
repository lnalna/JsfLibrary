package ru.javabegin.training.web.beans;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author nik
 */

@ManagedBean
@SessionScoped

public class User implements Serializable {
    
    private String username;
    
    public User(){
        
    }
    
    public String getUsername(){
        return username;
    }
    
    public void setUsername(String username){
        this.username = username;
    }
}

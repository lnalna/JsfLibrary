/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.javabegin.training.web.converters;

/**
 *
 * @author nik
 */
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import ru.javabegin.training.web.controllers.AuthorController;
import ru.javabegin.training.web.db.DataHelper;

public class AuthorConverter implements Converter {

    private static AuthorController authorController;

    public AuthorConverter() {
        authorController = (AuthorController) FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get("authorController");
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return DataHelper.getInstance().getAuthor(Long.valueOf(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        System.out.println(value);
        return "";//((Author)value).getFio();
    }
}

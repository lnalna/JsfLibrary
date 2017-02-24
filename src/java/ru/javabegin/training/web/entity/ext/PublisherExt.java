/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.javabegin.training.web.entity.ext;

import ru.javabegin.training.web.entity.Publisher;
import java.io.Serializable;

/**
 *
 * @author nik
 */
public class PublisherExt extends Publisher implements Serializable{
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + (super.getId() != null ? super.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PublisherExt other = (PublisherExt) obj;
        if (super.getId() != other.getId() && (super.getId() == null || !super.getId().equals(other.getId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return super.getName();
    }
}

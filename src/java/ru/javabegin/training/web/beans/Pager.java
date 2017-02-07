/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.javabegin.training.web.beans;


import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author nik
 * @param <T>
 */
public class Pager<T> {
    
    private int selectedPageNumber = 1;
    private int booksCountOnPage = 5;
    private Long totalBooksCount;
    
    private List<T> list;
    
    public int getFrom(){
        return selectedPageNumber * booksCountOnPage - booksCountOnPage;
    }
    
    public int getTo(){
        return booksCountOnPage;
    }
    
    public List<T> getList(){
        return list;
    }
    
    public void setList(List<T> list){
        this.list = list;
    }
    
    public void setTotalBooksCount(Long totalBooksCount){
        this.totalBooksCount = totalBooksCount;
    }
    
    public Long getTotalBooksCount(){
        return totalBooksCount;
    }
    
    public void setSelectedPageNumber(int selectedPageNumber){
        this.selectedPageNumber = selectedPageNumber;
    }
    
    public int getSelectedPageNumber(){
        return selectedPageNumber;
    }
    
    private List<Integer> pageNumbers = new ArrayList<Integer>();
    
    public List<Integer> getPageNumbers() {// кол-во страниц для постраничности

        int pageCount = 0;

        if (totalBooksCount % booksCountOnPage == 0) {
            pageCount = totalBooksCount > 0 ? (int) (totalBooksCount / booksCountOnPage) : 0;
        } else {
            pageCount = totalBooksCount > 0 ? (int) (totalBooksCount / booksCountOnPage) + 1 : 0;
        }

        pageNumbers.clear();

        for (int i = 1; i <= pageCount; i++) {
            pageNumbers.add(i);
        }

        return pageNumbers;
    }

    public int getBooksCountOnPage() {
        return booksCountOnPage;
    }

    public void setBooksCountOnPage(int booksCountOnPage) {
        this.booksCountOnPage = booksCountOnPage;
    }
    
}

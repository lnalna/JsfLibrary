package ru.javabegin.training.web.servlets;

import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ru.javabegin.training.web.controllers.BookListController;

@WebServlet(name = "SavePdf", urlPatterns = {"/SavePdf"})
public class SavePdf extends HttpServlet{
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        
        response.setContentType("application/pdf");
        
        OutputStream outputStream =  response.getOutputStream();
        try{
            int id = Integer.valueOf(request.getParameter("id"));
            BookListController bookListController = (BookListController) request.getSession(false).getAttribute("bookListController");
            byte[] content = bookListController.getContent(id);
            response.setHeader("Content-Disposition", "attachment;filename=file.pdf");
            outputStream.write(content);
            
        } catch(Exception ex){
            ex.printStackTrace();
        }finally{
            outputStream.close();
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "SavePdf";
    }
}

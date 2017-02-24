package ru.javabegin.training.web.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ru.javabegin.training.web.controllers.BookListController;
//import ru.javabegin.training.web.controllers.BookListController;
import ru.javabegin.training.web.db.DataHelper;

@WebServlet(name = "SavePdf", urlPatterns = {"/SavePdf"})
public class SavePdf extends HttpServlet{
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        
        response.setContentType("application/pdf; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        OutputStream outputStream =  response.getOutputStream();
        try{
            long id = Long.valueOf(request.getParameter("id"));
            //Boolean save = Boolean.valueOf(request.getParameter("save"));
            String filename = request.getParameter("filename");
            filename = URLEncoder.encode(filename, "UTF-8");
            BookListController bookListController = (BookListController) request.getSession(false).getAttribute("bookListController");
            byte[] content = bookListController.getDataHelper().getContent(id);
            response.setContentLength(content.length);
            response.setHeader("Content-Disposition","attachment; filename*=UTF-8''"+filename+".pdf");
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

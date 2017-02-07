package ru.javabegin.training.web.servlets;

import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ru.javabegin.training.web.controllers.BookListController;
import ru.javabegin.training.web.db.DataHelper;
import ru.javabegin.training.web.entity.Book;

@WebServlet(name = "ShowImage",
urlPatterns = {"/ShowImage"})
public class ShowImage extends HttpServlet{
    
     /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("image/jpeg");
        OutputStream out = response.getOutputStream();
        
        try{
            long id = Integer.valueOf(request.getParameter("id"));
            
            //BookListController bookListController = (BookListController)request.getSession(false).getAttribute("bookListController");
            
            byte[] image = DataHelper.getInstance().getImage(id);
            response.setContentLength(image.length);
            out.write(image);
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            out.close();
}
        
    }
    

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Show image servlet";
    }
    
}

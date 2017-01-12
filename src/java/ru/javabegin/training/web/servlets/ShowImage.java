package ru.javabegin.training.web.servlets;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletException;
import ru.javabegin.training.web.controllers.SearchController;

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
            int id = Integer.valueOf(request.getParameter("id"));
            
            SearchController searchController = (SearchController)request.getSession(false).getAttribute("searchController");
            
            //byte[] image = searchController.getImage(id);
            //response.setContentLength(image.length);
            //out.write(image);
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            out.close();
        }
        
    }
    

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getServletInfo() {
        return super.getServletInfo(); //To change body of generated methods, choose Tools | Templates.
    }
    
}

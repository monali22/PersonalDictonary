/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.UserInfoBeanLocal;
import model.WordDataBeanLocal;

/**
 *
 * @author monali
 */
@WebServlet(name = "SignUpServlet", urlPatterns = {"/SignUpServlet"})
public class SignUpServlet extends HttpServlet {
    
    /*
     * Creates a user in the JavaDB  
     * Once found signUp is done and the scoreboard is displayed.
       userInfoBean and wordDataBean are used to invoke the method in the 
       UserInfo Session and WordData Session Bean
       userInfoBean calls signUp method 
       wordDataBean calls getAll method
    */
    UserInfoBeanLocal userInfoBean = lookupUserInfoBeanLocal();
    WordDataBeanLocal wordDataBean = lookupWordDataBeanLocal();

    /**
     * Creates a user by saving the credentials in the Java DB 
     * On load of signUp the scoreboard is displayed.
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            String uname = request.getParameter("uname");
            String password = request.getParameter("password");
            request.getSession().setAttribute("uname", uname);
            userInfoBean.signUp(uname, password);
            List<String> scores = wordDataBean.getScores();
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet LoginServlet</title>");
            out.println("<link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css\" integrity=\"sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO\" crossorigin=\"anonymous\">");
            out.println("</head>");
            out.println("<body style=\"background: #dbd1c7;\">");
            out.println("<nav class=\"navbar navbar-expand-lg navbar-light bg-light\" style=\"background: linear-gradient(90deg, #29323c 0%, #3b4249 100%);\">");
            out.println("<a class=\"navbar-brand\" href=\"#\" style=\"color: rgba(255, 255, 255, 0.7)\">My Dictionary</a>");
            out.println("<button class=\"navbar-toggler\" type=\"button\" data-toggle=\"collapse\" data-target=\"#navbarSupportedContent\" aria-controls=\"navbarSupportedContent\" aria-expanded=\"false\" aria-label=\"Toggle navigation\">");
            out.println("<span class=\"navbar-toggler-icon\"></span>");
            out.println("</button>");
            out.println("<div class=\"collapse navbar-collapse\" id=\"navbarSupportedContent\">");
            out.println(" <ul class=\"navbar-nav mr-auto\">");
            out.println("<li class=\"nav-item active\">");
            out.println("<a class=\"nav-link\" href=\"searchword.html\" style=\"color: rgba(255, 255, 255, 0.7)\">Search Word <span class=\"sr-only\">(current)</span></a>");
            out.println("</li>");
            out.println("<li class=\"nav-item\">");
            out.println("<a class=\"nav-link\" href=\"ColllectedWords\" style=\"color: rgba(255, 255, 255, 0.7)\">See my collection</a>");
            out.println(" </li>");
            out.println("</ul><form class=\"form-inline my-2 my-lg-0\">");
            out.println("<a class=\"nav-link\" style=\"color: rgba(255, 255, 255, 0.7)\">"+uname.toUpperCase()+"</a>");
            out.println("<a class=\"nav-link\" href=\"index.html\">Logout</a>");
            out.println("</form></div></nav>");
            out.println("<h1>" + "Welcome to Your Dictionary" + "</h1>");           
            out.println("<table class=\"table table-striped table-dark\"><thead>");
            out.println(" <tr><th scope=\"col\">#</th>");
            out.println("<th scope=\"col\">Name</th>");
            out.println("<th scope=\"col\">Score</th>");
            out.println("</tr></thead> <tbody>");
            for (int i = 0; i < scores.size()-2; i++) {
                out.println("<tr><th scope=\"row\">" + (i + 1) + "</th>");
                out.println("<td>" + scores.get(i).split(":=")[0].replace(".json", "") + "</td>");
                out.println("<td>" + scores.get(i).split(":=")[1] + "</td>");
                out.println(" </tr>");
            }
            out.println("</tbody></table>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    /*
    Looks up for UserInfoBean using lookupmethod of Context
    */
    private UserInfoBeanLocal lookupUserInfoBeanLocal() {
        try {
            Context c = new InitialContext();
            return (UserInfoBeanLocal) c.lookup("java:comp/env/ejb/UserInfoBean");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
    /*
    Looks up for WordDataBean using lookupmethod of Context
    */
    private WordDataBeanLocal lookupWordDataBeanLocal() {
        try {
            Context c = new InitialContext();
            return (WordDataBeanLocal) c.lookup("java:comp/env/ejb/WordDataBean");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

}

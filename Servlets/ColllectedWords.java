/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.WordDataBeanLocal;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author monali
 */
public class ColllectedWords extends HttpServlet {

    /*
    wordDataBeanto invoke the session bean method
    to gell all the words learned by the user.
    List of JSONArray words contains all the words
    searched by the user.Json Simple is used to 
    parse and process the JSON returned by the API
     */
    WordDataBeanLocal wordDataBean = lookupWordDataBeanLocal();
    List<JSONArray> list = null;

    /**
     * Requests for the words learned by asking wordDataBean to read an send the
     * list of words.The words are read and displayed as word, type,definition,
     * synonyms and antonyms.CSS and Bootstrap are used to make the user
     * interface more appealing.
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ParseException {
        response.setContentType("text/html;charset=UTF-8");
        String userName = request.getSession().getAttribute("uname").toString();
        list = wordDataBean.getAll(userName);
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css\" integrity=\"sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO\" crossorigin=\"anonymous\">");
            out.println("<title>Servlet Colllected Words</title>");
            out.println("<script src=\"collectWords\"></script>");
            out.println("</head>");
            out.println("<body>");
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
            out.println("<a class=\"nav-link\" style=\"color: rgba(255, 255, 255, 0.7)\">" + userName.toUpperCase() + "</a>");
            out.println("<a class=\"nav-link\" href=\"index.html\">Logout</a>");
            out.println("</form></div></nav>");
            String synonyms = null;
            String antonyms = null;
            String json2 = null;
            out.println("<div id=\"accordion\">");
            for (int j = 0; j < list.size(); j++) {
                JSONArray josd = list.get(j);
                for (int i = 0; i < josd.size(); i++) {
                    JSONObject jo = (JSONObject) josd.get(i);
                    json2 = jo.get("meta").toString();
                    JSONParser parser = new JSONParser();
                    JSONObject jj = (JSONObject) parser.parse(json2);
                    String wrd = jj.get("id").toString();
                    synonyms = jj.get("syns").toString();
                    antonyms = jj.get("ants").toString();
                    out.println("<div class=\"card\">");
                    out.println("<div class=\"card-header\" id=\"heading" + j + "\">");
                    out.println("<h5 class=\"mb-0\">");
                    out.println("<button class=\"btn btn-link collapsed\" data-toggle=\"collapse\" data-target=\"#collapse" + j + "\" aria-expanded=\"false\" aria-controls=\"collapse" + j + "\">");
                    out.println("<h1 style=\"display: block;font-weight: 300;font-size: 2em; text-transform: capitalize;\">" + "Word : " + wrd + "</h1>");
                    out.println("</button></h5></div>");
                    out.println("<div id=\"collapse" + j + "\" class=\"collapse show\" aria-labelledby=\"heading" + j + "\" data-parent=\"#accordion\">");
                    out.println("<div class=\"card-body\">");
                    out.println("<h3 style = \"text-transform: capitalize\">" + "Type : " + jo.get("fl").toString().replaceAll("[-+.^:,]", "").replaceAll("\\[", "").replaceAll("\\]", "") + "</h3>");
                    JSONArray jsonArray = (JSONArray) jo.get("shortdef");
                    out.println("<h3>" + "Definition:" + "</h3>");
                    Iterator<String> iterator = jsonArray.iterator();
                    out.println("<ul>");
                    while (iterator.hasNext()) {
                        out.println("<li><h3>" + iterator.next() + "</h3></li>");
                    }
                    out.println("</ul>");
                    out.println("<h3>" + "Synonyms : " + synonyms.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\"", "") + "</h3>");
                    out.println("<h3>" + "Antonyms : " + antonyms.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\"", "") + "</h3>");
                    out.println("</div></div></div>");
                }
            }
            out.println("</div>");
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
        try {
            processRequest(request, response);
        } catch (ParseException ex) {
            Logger.getLogger(ColllectedWords.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        try {
            processRequest(request, response);
        } catch (ParseException ex) {
            Logger.getLogger(ColllectedWords.class.getName()).log(Level.SEVERE, null, ex);
        }
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

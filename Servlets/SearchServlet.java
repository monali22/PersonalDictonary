/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.UserInfoBeanLocal;
import model.WordDataBeanLocal;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author monali
 */
public class SearchServlet extends HttpServlet {
    
    /*
     * Call the Merriam Webster API to search the word
     * Once found the data about the word is displayed.
       userInfoBean and wordDataBean are used to invoke the method in the 
       UserInfo Session and WordData Session Bean
       HttpURLConnection is used to call the API.
       responseConetnt is used to read the JSON String
       JsonObject and parser to handle the json data
    */
   WordDataBeanLocal wordDataBean =  lookupWordDataBeanLocal();
   UserInfoBeanLocal userInfoBean = lookupUserInfoBeanLocal();
   private HttpURLConnection connection;
   URL url; 
   BufferedReader reader;
   String line;
   StringBuffer responseConetnt;
   JSONObject jo;
   JSONParser parser;

    /*
     Calls the Merriam Webster Intermediate Thesaurus API usinh HTTPURLConnection
     The data is received as JSON which is parsed using JSON parser
     The JSON array received in response is saved to the json file on the remote machine
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String sWord = request.getParameter("word");
        String urld = "https://dictionaryapi.com/api/v3/references/ithesaurus/json/"+sWord+"?key=bfbe0b8a-9beb-49f6-b67d-5c87e7d858b6";

        try (PrintWriter out = response.getWriter()) {
            url = new URL(urld);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(50000);
            connection.setReadTimeout(30000);
            int status = connection.getResponseCode();
            responseConetnt = new StringBuffer();
            parser = new org.json.simple.parser.JSONParser();
            String userName = request.getSession().getAttribute("uname").toString();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while((line = reader.readLine()) != null) {
                responseConetnt.append(line);
                
            }
            String jsonString =  responseConetnt.toString(); //assign your JSON String here
            Object obj = parser.parse(jsonString);
            JSONArray array = (JSONArray)obj;
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css\" integrity=\"sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO\" crossorigin=\"anonymous\">");
            out.println("<title>Servlet Colllected Words</title>");  
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
            out.println("<a class=\"nav-link\" style=\"color: rgba(255, 255, 255, 0.7)\">"+userName.toUpperCase()+"</a>");
            out.println("<a class=\"nav-link\" href=\"index.html\">Logout</a>");
            out.println("</form></div></nav>");
            String syno = null;
            String anto = null;
            String json2 = null;
            out.println("<div id=\"accordion\">");
            for(int i = 0; i < array.size(); i++) {
                jo = (JSONObject)array.get(i);
                json2 = jo.get("meta").toString();
                JSONParser parser = new JSONParser();
                JSONObject jj = (JSONObject) parser.parse(json2);
                String wrd = jj.get("id").toString();
                syno =  jj.get("syns").toString();
                anto =  jj.get("ants").toString();
                out.println("<div class=\"card\">");
                out.println("<div class=\"card-header\" id=\"heading"+i+"\">");
                out.println("<h5 class=\"mb-0\">");
                out.println("<button class=\"btn btn-link collapsed\" data-toggle=\"collapse\" data-target=\"#collapse"+i+"\" aria-expanded=\"false\" aria-controls=\"collapse"+i+"\">");
                out.println("<h1 style=\"display: block;font-weight: 300;font-size: 2em; text-transform: capitalize;\">"+ "Word : "+wrd+"</h1>");
                out.println("</button></h5></div>");
                out.println("<div id=\"collapse"+i+"\" class=\"collapse show\" aria-labelledby=\"heading"+i+"\" data-parent=\"#accordion\">");
                out.println("<div class=\"card-body\">");
                out.println("<h3 style = \"text-transform: capitalize\">" + "Type : " +jo.get("fl").toString().replaceAll("[-+.^:,]","").replaceAll("\\[", "").replaceAll("\\]","") + "</h3>");
                JSONArray jsonArray = (JSONArray) jo.get("shortdef");
                out.println("<h3>"+"Definition:"+"</h3>");
                Iterator<String> iterator = jsonArray.iterator();
                out.println("<ul>");
                while (iterator.hasNext()) {
                    out.println("<li><h3>"+ iterator.next()+"</h3></li>");
                }
                out.println("</ul>");
                out.println("<h3>" + "Synonyms : " +syno.replaceAll("\\[", "").replaceAll("\\]","").replaceAll("\"", "")+"</h3>");
                out.println("<h3>"+ "Antonyms : "+anto.replaceAll("\\[", "").replaceAll("\\]","").replaceAll("\"", "")+"</h3>");
                out.println("</div></div></div>");
            }
            wordDataBean.save(userName,sWord, array);
            out.println("</ol>");
            out.println("</body>");
            out.println("</html>");
            reader.close();
        }
        catch (Exception e) {    
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
}

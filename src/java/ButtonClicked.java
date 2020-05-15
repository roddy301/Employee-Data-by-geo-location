/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.util.JSON;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author rudra
 */
@WebServlet(urlPatterns = {"/ButtonClicked"})
public class ButtonClicked extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        /*try (PrintWriter out = response.getWriter()) {
           
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ButtonClicked</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ButtonClicked at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }*/
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
        //processRequest(request, response);
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
        //processRequest(request, response);
        String geo = request.getParameter("loc");
        String rad = request.getParameter("radV");
        //System.out.println(rad+"    "+geo);
        if(rad.equals("MySQL")){
            PrintWriter out;
            try{
                out = response.getWriter();
                Class.forName("com.mysql.cj.jdbc.Driver");  
                Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/geo_employment_data?autoReconnect=true&useSSL=false","root","rudra301");
            
                Statement stmt=con.createStatement();
                
                ResultSet rs = stmt.executeQuery("select * from datasets where geo='"+geo+"'");
                
                JSONObject obj = new JSONObject();
                JSONArray array = new JSONArray();
                while(rs.next()){
                    obj = new JSONObject();
                    obj.put("_id",rs.getString(1));
                    obj.put("REF_DATE",rs.getString(2));
                    obj.put("GEO",rs.getString(3));
                    obj.put("DGUID",rs.getString(4));
                    obj.put("Job vacancy statistics",rs.getString(5));
                    obj.put("North American Industry Classification System (NAICS)",rs.getString(6));
                    obj.put("UOM",rs.getString(7));
                    obj.put("UOM_ID",rs.getString(8));
                    obj.put("SCALAR_FACTOR",rs.getString(9));
                    obj.put("SCALAR_ID",rs.getString(10));
                    obj.put("VECTOR",rs.getString(11));
                    obj.put("COORDINATE",rs.getString(12));
                    obj.put("STATUS",rs.getString(13));
                    array.put(obj);
                }
                out.write(array.toString());
                //System.out.print(array);
                con.close();
                }catch (ClassNotFoundException | SQLException ex) {
                Logger.getLogger(NewServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else{
            try{
            //System.out.println("mongo");
            PrintWriter out;
            
            out=response.getWriter();
            
            MongoClient mongoClient = new MongoClient("localhost",27017);
            DBCollection collection=mongoClient.getDB("naics").getCollection("EmpData");
            BasicDBObject searchQuery = new BasicDBObject();
            searchQuery.put("GEO", geo);
           
            DBCursor cursor = collection.find(searchQuery);
            
            JSONArray array = new JSONArray();
            
            String serialize=JSON.serialize(cursor.next());
            
            while (cursor.hasNext()) {
                JSONObject obj = new JSONObject(serialize);
                array.put(obj);
                serialize = JSON.serialize(cursor.next());
            }
            
            out.write(array.toString());
            //DBCursor cursor = collection.find();
            //JSON json = new JSON();
            //System.out.println(cursor.toString());
            
            }catch(Exception e){
                e.printStackTrace();
            }
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

}

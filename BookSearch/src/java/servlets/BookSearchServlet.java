package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Matthew Ecker
 */
@WebServlet(name = "BookSearchServlet", urlPatterns = {"/BookSearchServlet"})
public class BookSearchServlet extends HttpServlet {

    private PreparedStatement pstmt1;
    private PreparedStatement pstmt2;
    
    public void init(String isbn) throws ServletException {
      initializeJdbc(isbn);
    }
    
    /* not needed
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet BookSearchServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet BookSearchServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }
    */

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {        
        PrintWriter out = resp.getWriter();
        out.println("<!DOCTYPE html><html><head><title>Search Page</title></head><body>");
        out.println("<form action=\"BookSearchServlet\" method=\"POST\">");
        out.println("<ul>");
        out.println("<li>ISBN: <input type=\"text\" name=\"isbn\"></li>");
        out.println("<li>Example: 0-13-403732-4</li>");
        out.println("</ul>");
        out.println("<input type=\"submit\" value=\"Search\" />");
        out.println("</form>");
        out.println("</body></html>");
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String isbn = req.getParameter("isbn");
        
        PrintWriter out = resp.getWriter();
        out.println("<!DOCTYPE html><html><head><title>Results</title></head><body>");
        out.println("<ul>");
        this.initializeJdbc(isbn);
        try {
            this.getBook(out);
        } catch (SQLException ex) {
            Logger.getLogger(BookSearchServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        out.println("</ul>");
        out.println("<a href=\"index.html\">Return to Homepage</a>");
        out.println("</body></html>");
    }

    @Override
    public String getServletInfo() {
        return "This servlet returns book info based on an ISBN.";
    }// </editor-fold>
    
    // initializes JDBC connection and creates prepared statements for db queries
    private void initializeJdbc(String isbn) {
        try {
            String connectionString = "jdbc:derby://localhost:1527/BookDB";
            String dbCommand1 = "select TITLE, PAGES from BOOK where ISBN='" + isbn + "'";
            String dbCommand2 = "select AUTHOR from BOOKAUTHOR where ISBN='" + isbn + "'";

            Connection conn = DriverManager.getConnection
              (connectionString, "root", "root");

            pstmt1 = conn.prepareStatement(dbCommand1);
            pstmt2 = conn.prepareStatement(dbCommand2);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    // retrieves book information from the db and prints to the page
    private void getBook(PrintWriter out) throws SQLException {
        ResultSet rs = pstmt1.executeQuery();
        while (rs.next()) {
            String title = rs.getString("TITLE");
            String pages = rs.getString("PAGES");

            out.println("<li>Title: " + title + "</li>");
            out.println("<li>Pages: " + pages + "</li>");
        }
        
        rs = pstmt2.executeQuery();
        while (rs.next()) {
            String author = rs.getString("AUTHOR");
            out.println("<li>Author: " + author + "</li>");
        }
    }
}

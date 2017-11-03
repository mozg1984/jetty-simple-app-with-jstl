package demo;
 
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AboutServlet extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	Boolean isAuth = Boolean.valueOf((String) request.getSession().getAttribute("isAuth"));

        if (!isAuth) {
        	PrintWriter out = response.getWriter();
            out.println("<font color=red>Error: you are not authorized</font>");

            RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
            dispatcher.include(request, response);

            return;
        }

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("<h1>Simple web app</h1>");
        response.getWriter().println("<p>This is a demo simple web application using embedded Jetty web server</p>");
    }
}
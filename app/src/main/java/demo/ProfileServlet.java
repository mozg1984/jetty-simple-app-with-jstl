package demo;
 
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProfileServlet extends HttpServlet 
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

        String user = (String) request.getSession().getAttribute("user");
        request.setAttribute("greeting", "Hello, " + user);
        request.getRequestDispatcher("/profile.jsp").forward(request, response);
    }
}
package demo;
 
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {
    private static String user = "admin";
    private static String password = "password";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.sendRedirect("/");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	String user = request.getParameter("user");
    	String pwd = request.getParameter("password");
    	HttpSession session = request.getSession();

    	if (LoginServlet.isAuth(user, pwd)) {
	    	session.setAttribute("isAuth", "true");
	    	session.setAttribute("user", user);
            response.sendRedirect("/");
    	} else {
    		session.invalidate();
            
            PrintWriter out = response.getWriter();
            out.println("<font color=red>Error: user name or password is wrong</font>");
            
            RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
            dispatcher.include(request, response);
    	}
    }

    protected static boolean isAuth(String user, String password) 
    {
    	return user != null && LoginServlet.user.equals(user) &&
    		password != null && LoginServlet.password.equals(password);
    }
}
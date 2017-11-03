package demo.launcher;

import demo.AboutServlet;
import demo.ProfileServlet;
import demo.LoginServlet;
import demo.LogoutServlet;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tomcat.util.scan.StandardJarScanner;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.jsp.JettyJspServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.webapp.Configuration;

/**
 * Starts jetty-server on the 8080 port
 */
public class Launcher 
{
    // Resource path pointing to where the WEBROOT is
    private static final String WEBROOT = "/classes/";

    /**
     * JspStarter for embedded ServletContextHandlers
     * 
     * This is added as a bean that is a jetty LifeCycle on the ServletContextHandler.
     * This bean's doStart method will be called as the ServletContextHandler starts,
     * and will call the ServletContainerInitializer for the jsp engine.
     *
     */
    public static class JspStarter extends AbstractLifeCycle implements ServletContextHandler.ServletContainerInitializerCaller
    {
        JettyJasperInitializer sci;
        ServletContextHandler context;
        
        public JspStarter (ServletContextHandler context)
        {
            this.sci = new JettyJasperInitializer();
            this.context = context;
            this.context.setAttribute("org.apache.tomcat.JarScanner", new StandardJarScanner());
        }

        @Override
        protected void doStart() throws Exception
        {
            ClassLoader old = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(context.getClassLoader());
            try
            {
                sci.onStartup(null, context.getServletContext());   
                super.doStart();
            }
            finally
            {
                Thread.currentThread().setContextClassLoader(old);
            }
        }
    }

    public static void main(String[] args) throws Exception 
    {
        Launcher launcher = new Launcher();
        launcher.start();    
    }

    public void start() throws Exception 
    {
        Server server = new Server();
        int port = 8080;

        // Define ServerConnector
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        server.addConnector(connector);

        // Add annotation scanning (for WebAppContexts)
        Configuration.ClassList classlist = Configuration.ClassList
                .setServerDefault(server);
        
        classlist.addBefore(
            "org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
            "org.eclipse.jetty.annotations.AnnotationConfiguration"
        );


        // Base URI path for servlet context
        String basePath = getWebRootResourcePath();

        // Create Servlet context
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContextHandler.setContextPath("/");
        // "file:/C:/java/embedded-jetty-jsp/target/classes/webroot/"
        servletContextHandler.setResourceBase(basePath);

        // Since this is a ServletContextHandler we must manually configure JSP support.
        enableEmbeddedJspSupport(servletContextHandler);

        // Add Application Servlets
        servletContextHandler.addServlet(AboutServlet.class, "/about");
        servletContextHandler.addServlet(ProfileServlet.class, "/profile");
        servletContextHandler.addServlet(LoginServlet.class, "/login");
        servletContextHandler.addServlet(LogoutServlet.class, "/logout");

        // Default Servlet (always last, always named "default")
        ServletHolder holderDefault = new ServletHolder("default", DefaultServlet.class);
        holderDefault.setInitParameter("resourceBase", basePath);
        holderDefault.setInitParameter("dirAllowed", "true");
        servletContextHandler.addServlet(holderDefault, "/");
        server.setHandler(servletContextHandler);

        server.start();
        server.join();
    }

    /**
     * Setup JSP Support for ServletContextHandlers.
     * NOTE: This is not required or appropriate if using a WebAppContext.
     *
     * @param servletContextHandler the ServletContextHandler to configure
     * @throws IOException if unable to configure
     */
    private void enableEmbeddedJspSupport(ServletContextHandler servletContextHandler) throws IOException
    {
        // Establish Scratch directory for the servlet context (used by JSP compilation)
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File scratchDir = new File(tempDir.toString(), "embedded-jetty-jsp");
    
        if (!scratchDir.exists())
        {
            if (!scratchDir.mkdirs())
            {
                throw new IOException("Unable to create scratch directory: " + scratchDir);
            }
        }
        servletContextHandler.setAttribute("javax.servlet.context.tempdir", scratchDir);
    
        // Set Classloader of Context to be sane (needed for JSTL)
        // JSP requires a non-System classloader, this simply wraps the
        // embedded System classloader in a way that makes it suitable
        // for JSP to use
        ClassLoader jspClassLoader = new URLClassLoader(new URL[0], this.getClass().getClassLoader());
        servletContextHandler.setClassLoader(jspClassLoader);
        
        // Manually call JettyJasperInitializer on context startup
        servletContextHandler.addBean(new JspStarter(servletContextHandler));

        // Create / Register JSP Servlet (must be named "jsp" per spec)
        ServletHolder holderJsp = new ServletHolder("jsp", JettyJspServlet.class);
        holderJsp.setInitOrder(0);
        holderJsp.setInitParameter("logVerbosityLevel", "DEBUG");
        holderJsp.setInitParameter("fork", "false");
        holderJsp.setInitParameter("xpoweredBy", "false");
        holderJsp.setInitParameter("compilerTargetVM", "1.8");
        holderJsp.setInitParameter("compilerSourceVM", "1.8");
        holderJsp.setInitParameter("keepgenerated", "true");
        servletContextHandler.addServlet(holderJsp, "*.jsp");
    }
    
    private String getWebRootResourcePath()
    {
        URL url;
        String path;
        Class currClass = this.getClass();

        try {
            url = currClass.getProtectionDomain().getCodeSource().getLocation();
        } catch (SecurityException ex) {
            url = currClass.getResource(currClass.getSimpleName() + ".class");
        }

        path = url.toExternalForm();

        if (path.endsWith(".jar")) { // from getCodeSource
            path = path.substring(0, path.lastIndexOf("/"));
        } else {  // from getResource
            String suffix = "/" + (currClass.getName()).replace(".", "/") + ".class";
            path = path.replace(suffix, "");
            if (path.startsWith("jar:") && path.endsWith(".jar!")) {
                path = path.substring(4, path.lastIndexOf("/"));
            }
        }

        return path + WEBROOT;

    }
}
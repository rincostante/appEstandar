/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.gob.ambiente.aplicaciones.appestandar.util;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ResourceBundle;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author rincostante
 */
public class LoginFilter implements Filter {
    
    private static final boolean debug = true;
    private static final String nameCookieUser = ResourceBundle.getBundle("/Bundle").getString("nameCookieUser");
    private static final String nameCookieUrl = ResourceBundle.getBundle("/Bundle").getString("nameCookieUrl");    

    // The filter configuration object we are associated with.  If
    // this value is null, this filter instance is not currently
    // configured. 
    private FilterConfig filterConfig = null;
    
    public LoginFilter() {
    }    
    
    private void doBeforeProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
        if (debug) {
            log("LoginFilter:DoBeforeProcessing");
        }
    }    
    
    private void doAfterProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
        if (debug) {
            log("LoginFilter:DoAfterProcessing");
        }  
    }

    /**
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        //Proceso la URL que está requiriendo el cliente
        String urlStr = req.getRequestURL().toString().toLowerCase();
        boolean noProteger = noProteger(urlStr);
        System.out.println(urlStr + " - desprotegido=[" + noProteger + "]");

        //Si no requiere protección continúo normalmente.
        if (noProteger(urlStr)) {
          chain.doFilter(request, response);
          return;
        }

        //Si no salió busco la cookie de usuario para saber si está autenticado,
        //y busco la cookie de la url, si existe la elimino
        String usAutenticado = "";
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(nameCookieUser)){
                     usAutenticado = cookie.getValue();
                }
                if(cookie.getName().equals(nameCookieUrl)){
                    cookie.setValue(null);
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    res.addCookie(cookie);
                }
            }
        }
        
        // si el usuario no está autenticado, procedo
        if(usAutenticado.equals("")){
            // guardo la url en la cookie correspondiente para volver en caso de autenticar el usuario
            Cookie cookieUrl = new Cookie(nameCookieUrl, req.getContextPath());
            cookieUrl.setMaxAge(60);
            cookieUrl.setPath("/");
            res.addCookie(cookieUrl);
            
            // redirecciona para la autenticación del usuario
            res.sendRedirect(ResourceBundle.getBundle("/Bundle").getString("RutaAutenticacion") + "/faces/login.xhtml");
            return; 
        }

        // El recurso requiere protección, pero el usuario ya está logueado.
        chain.doFilter(request, response);  

    }
    
    private boolean noProteger(String urlStr) {

    /*
     * Este es un buen lugar para colocar y programar todos los patrones que
     * creamos convenientes para determinar cuales de los recursos no
     * requieren protección. Sin duda que habría que crear un mecanizmo tal
     * que se obtengan de un archivo de configuración o algo que no requiera
     * compilación.
     */
      if (urlStr.endsWith("login.xhtml"))
        return true;
      if (urlStr.indexOf("/javax.faces.resource/") != -1)
        return true;
      if (urlStr.endsWith(".gif"))
        return true;    
      if (urlStr.endsWith(".png"))
        return true;    
      if (urlStr.endsWith(".ico"))
        return true;
      if (urlStr.endsWith(".css"))
        return true;       
      return false;
    }     

    /**
     * Return the filter configuration object for this filter.
     */
    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    /**
     * Set the filter configuration object for this filter.
     *
     * @param filterConfig The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    /**
     * Destroy method for this filter
     */
    public void destroy() {        
    }

    /**
     * Init method for this filter
     */
    public void init(FilterConfig filterConfig) {        
        this.filterConfig = filterConfig;
        if (filterConfig != null) {
            if (debug) {                
                log("LoginFilter:Initializing filter");
            }
        }
    }

    /**
     * Return a String representation of this object.
     */
    @Override
    public String toString() {
        if (filterConfig == null) {
            return ("LoginFilter()");
        }
        StringBuffer sb = new StringBuffer("LoginFilter(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());
    }
    
    private void sendProcessingError(Throwable t, ServletResponse response) {
        String stackTrace = getStackTrace(t);        
        
        if (stackTrace != null && !stackTrace.equals("")) {
            try {
                response.setContentType("text/html");
                PrintStream ps = new PrintStream(response.getOutputStream());
                PrintWriter pw = new PrintWriter(ps);                
                pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n"); //NOI18N

                // PENDING! Localize this for next official release
                pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n");                
                pw.print(stackTrace);                
                pw.print("</pre></body>\n</html>"); //NOI18N
                pw.close();
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        } else {
            try {
                PrintStream ps = new PrintStream(response.getOutputStream());
                t.printStackTrace(ps);
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        }
    }
    
    public static String getStackTrace(Throwable t) {
        String stackTrace = null;
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            sw.close();
            stackTrace = sw.getBuffer().toString();
        } catch (Exception ex) {
        }
        return stackTrace;
    }
    
    public void log(String msg) {
        filterConfig.getServletContext().log(msg);        
    }
    
}

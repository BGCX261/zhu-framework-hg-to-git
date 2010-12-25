package ua.org.zhu;

import java.io.IOException;
import java.sql.*;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


public class AuthFilter
	implements Filter
{
	private static final String ADMIN_ZONE = "/admin";
	private static final String LOGIN_URI = "/admin/login.jsp";
	private static final String ADMIN_URI = "/admin/index.jsp";
	private static final String SESSION_LOGIN = "login";
	
	private static final String ACTION_LOGOUT = "logout";
	
	private static final String FORM_LOGIN = "f_login";
	private static final String FORM_PASS = "f_password";
	
	private FilterConfig m_Config;
	private ServletRequest m_request;
	private ServletResponse m_response;
	
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		m_request = request;
		m_response = response;
		
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		System.out.println("Request: " + httpRequest.getRequestURI());
		HttpSession httpSession = httpRequest.getSession(true);
		if (httpSession.getAttribute(SESSION_LOGIN) != null)
		{
			System.out.println("User: " + httpSession.getAttribute(SESSION_LOGIN));
			if (httpRequest.getParameter(ACTION_LOGOUT) != null && httpRequest.getParameter(ACTION_LOGOUT).equals("true"))
			{
				httpSession.invalidate();
				redirectTo(LOGIN_URI);
			} else 
				chain.doFilter(request, response);
		} else {
			System.out.println("Anonymous user");
						
			if (httpRequest.getRequestURI().startsWith(ADMIN_ZONE)) {
				if (httpRequest.getParameter(FORM_LOGIN) != null && httpRequest.getParameter(FORM_PASS) != null && !httpRequest.getParameter(FORM_LOGIN).equals("") && !httpRequest.getParameter(FORM_PASS).equals("")) {
					if (userAuth(httpRequest.getParameter(FORM_LOGIN), httpRequest.getParameter(FORM_PASS))) {
						httpSession.setAttribute(SESSION_LOGIN, httpRequest.getParameter(FORM_LOGIN));
						System.out.println("User " + httpRequest.getParameter(FORM_LOGIN) + " logged in");
						redirectTo(ADMIN_URI);
					} else {
						redirectTo(LOGIN_URI+"?auth=false");
					}
				} else {
					System.out.println("Not logged in. Could not enter to admin page.");
					redirectTo(LOGIN_URI);
				}
			} else 
				chain.doFilter(request, response);
		}
	}
	
	private void redirectTo(String URI) throws IOException, ServletException {
		RequestDispatcher dispatcher = m_Config.getServletContext().getContext("/").getRequestDispatcher(URI);
		dispatcher.forward(m_request, m_response);
	}
	
	private boolean userAuth(String login, String password) {
		ResultSet rs = null;
		Statement st = null;
		Connection con = null;
		
		try {
			con = DriverManager.getConnection("jdbc:mysql:///zhu-framework", "zhu", "zhu");
			if (con.isClosed()) {
				System.out.println("Connection to MySQL server failed ");
				return false;
			}
			
			System.out.println("Successfully connected to MySQL server");
			st = con.createStatement();
			rs = st.executeQuery("SELECT * FROM user WHERE login = '" + login + "' AND password = '" + password + "'");
			
			if (rs.next())
				return true;
			else
				return false;
		} catch (Exception e) {
			System.err.println("Exception: " + e.getMessage());
			return false;
		} finally {
			try {
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
		        if(con != null)
		        	con.close();
			} catch (SQLException e) {
			}
		}
	}

    public AuthFilter() {
    }

	public void init(FilterConfig fConfig) throws ServletException {
		m_Config = fConfig;
	}

	public void destroy(){
	}

}
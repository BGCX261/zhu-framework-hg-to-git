package ua.org.zhu;

import java.io.IOException;
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
	private static final String LOGIN_URI = "/admin/login";
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
		System.out.println("new request: " + httpRequest.getRequestURI());
		if (httpRequest.getRequestURI().startsWith(ADMIN_ZONE))
		{
			HttpSession httpSession = httpRequest.getSession(true);
			if (httpSession.getAttribute(SESSION_LOGIN) != null)
			{
				if (httpRequest.getParameter(ACTION_LOGOUT) != null && httpRequest.getParameter(ACTION_LOGOUT).equals("true"))
				{
					httpSession.invalidate();
					redirectTo(LOGIN_URI);
				}
				else
					chain.doFilter(request, response);
			}
			else 
			{
				if (httpRequest.getParameter(FORM_LOGIN) != null && httpRequest.getParameter(FORM_PASS) != null && !httpRequest.getParameter(FORM_LOGIN).equals("") && !httpRequest.getParameter(FORM_PASS).equals(""))
				{
					// Тут должна быть авторизация
					httpSession.setAttribute(SESSION_LOGIN, httpRequest.getParameter(FORM_LOGIN));
				}
				else 
					redirectTo(LOGIN_URI);
			}
			
		}
		chain.doFilter(request, response);
	}
	
	private void redirectTo(String URI)
		throws IOException, ServletException
	{
		RequestDispatcher dispatcher = m_Config.getServletContext().getContext("/").getRequestDispatcher(LOGIN_URI);
		dispatcher.forward(m_request, m_response);
	}

    public AuthFilter()
    {
    }

	public void init(FilterConfig fConfig)
		throws ServletException
	{
		m_Config = fConfig;
	}

	public void destroy()
	{
	}

}
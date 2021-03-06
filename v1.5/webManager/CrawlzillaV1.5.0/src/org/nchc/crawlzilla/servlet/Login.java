package org.nchc.crawlzilla.servlet;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nchc.crawlzilla.bean.LoginBean;
import org.nchc.crawlzilla.bean.OperFileBean;

/**
 * Servlet implementation class Login
 */
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		String targetURL = "/systemMessage.jsp";
		LoginBean oper = new LoginBean();
		
		if( (request.getParameter("mobileMode") != null) && (request.getParameter("mobileMode").equals("true")) ){
			targetURL = "/mobile/systemMessage.jsp";
		}
		

		
		String operation = request.getParameter("operation");
		String tranUrl = "index.jsp";
		if(operation.equals("changePW")){
			String user = request.getParameter("userName");
			String passwd = request.getParameter("passwd");
			String newpasswd = request.getParameter("newpasswd");
			String confpasswd = request.getParameter("confpasswd");
			

			try {
					if((newpasswd.equals(confpasswd)) && oper.getConfirm(user, passwd)){
						oper.changePW(user, newpasswd);
						response.sendRedirect(tranUrl);
					} else {
						//String targetURL="/systemMessage.jsp";
						String message = "Information not correct！" + "<p> please return to previous page.</p>";
						request.setAttribute("message", message);
						RequestDispatcher rd;
						rd = getServletContext().getRequestDispatcher(targetURL);
						rd.forward(request, response);
				}
			} catch (NoSuchAlgorithmException e) {
				e.getMessage();
			} 
			
			System.out.println("Change Passwd");
		} else if(operation.equals("login")){
			String user = request.getParameter("user");
			String passwd = request.getParameter("passwd");
			// language : set en to file /home/crawler/crawlzilla/user/admin/meta/weblang
			String langPath = "/home/crawler/crawlzilla/user/"+user+"/meta/weblang";
			File langP = new File (langPath);
			OperFileBean langbean;
			if (langP.exists()){
				langbean = new OperFileBean(langPath);
			}else{
				langbean = new OperFileBean("en",langPath);
			}
			
			try {
				if (oper.getConfirm(user, passwd)){
					HttpSession session = request.getSession(true);
					session.setAttribute("userName", user);
					session.setAttribute("loginFlag", "true");
					session.setAttribute("portNO",oper.getPortNO());
					session.setAttribute("language",langbean.getFileStr());
					if(user.equals("admin") && oper.checkFristLogin()){
						tranUrl = "changePW.jsp";
					}
					response.sendRedirect(tranUrl);	
					System.out.print(session.getAttribute("userName").toString());				
	
					System.out.print("login...");				
				} else {
					//TODO implement loginerror.jsp
					//response.sendRedirect("loginerror.jsp");
					//String targetURL="/systemMessage.jsp";
					String message = "Can not Login ! <p> Please check your username and password！</p>";
					request.setAttribute("message", message);
					RequestDispatcher rd;
			        rd = getServletContext().getRequestDispatcher(targetURL);
			        rd.forward(request, response);
					System.out.println("can't login...");
				}
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		else if(operation.equals("editEmail")){
			String userName = request.getParameter("userName");
			String newEmail = request.getParameter("newEmail");
			oper.setEmail(userName, newEmail);
			//String targetURL="/systemMessage.jsp";
			String message = "Successfully modified！";
			request.setAttribute("message", message);
			RequestDispatcher rd;
			rd = getServletContext().getRequestDispatcher(targetURL);
			rd.forward(request, response);
		}else if(operation.equals("firstLogin")){
			String userName = request.getParameter("userName");
			String newpasswd = request.getParameter("newpasswd");
			String confpasswd = request.getParameter("confpasswd");
			String newEmail = request.getParameter("newEmail");
			String language = request.getParameter("language");
			//String targetURL="/systemMessage.jsp";
			String mes = "";
			// set email
			oper.setEmail(userName, newEmail);
			// set language
			oper.setLang(userName, language);
			
			// set password
			if (newpasswd.equals(confpasswd)) {
				oper.changePW(userName, newpasswd);
				mes = "Successfully modified " + 
				"\n email: " + newEmail +
				"\n language:" + language ;
				
			}else{
				mes = "Password and confident is not the same.";
			}			

			request.setAttribute("message", mes);
			RequestDispatcher rd;
			rd = getServletContext().getRequestDispatcher(targetURL);
			rd.forward(request, response);
		}
	}
}
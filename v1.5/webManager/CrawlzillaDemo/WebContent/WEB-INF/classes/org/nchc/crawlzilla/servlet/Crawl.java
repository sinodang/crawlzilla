/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @guide
 * Launch nutch to crawl web page
 * @web
 * http://code.google.com/p/crawlzilla 
 * @author Waue, Shunfa, Rock {waue, shunfa, rock}@nchc.org.tw
 */

package org.nchc.crawlzilla.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nchc.crawlzilla.bean.crawlBean;

/**
 * Servlet implementation class Crawl
 */
public class Crawl extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Crawl() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession(true);
		
		String userName = session.getAttribute("userName").toString();
		String targetURL="/systemMessage.jsp";
		String IDBName = request.getParameter("IDBName");
		String crawlUrls = request.getParameter("crawlUrls");
		String depth = request.getParameter("depth");
		String schDate =  request.getParameter("schDate");
		String schHour = request.getParameter("schHour");
		String schMin = request.getParameter("schMin");
		String freq = request.getParameter("freq");
		
		boolean currentDo = false;
		
		crawlBean crawlJob = new crawlBean();
		System.out.println("IDBName = " + IDBName);
		
		if (IDBName.equals("") ||
			crawlUrls.equals("")){
			String message = "索引庫名稱及爬取網址未輸入 <p> 請返回上一頁重新輸入！</p>";
			request.setAttribute("message", message);
			RequestDispatcher rd;
	        rd = getServletContext().getRequestDispatcher(targetURL);
	        rd.forward(request, response);
			System.out.println("non keyin IDBName, crawlURLS");
		} else 	if(crawlJob.checkEqual(userName, IDBName)){
			if (schDate.equals("") || 
					schHour.equals("") ||
					schMin.equals("") ||
					freq.equals("")){
				currentDo = true;
				System.out.println("No schedule");		
			} else {
				try {
					crawlJob.schedule(userName, IDBName, schDate, schHour, schMin, freq, depth);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
			
			crawlJob.writeUrls(userName, crawlUrls, IDBName);
			crawlJob.crawlJob(userName, IDBName, depth, currentDo);
			
			System.out.println("call Crawl Job!");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			getServletContext().getRequestDispatcher("/crawlstatus.jsp").forward(request,response);
			//response.sendRedirect("indexpool.jsp");
		} else {
			String message = IDBName + "已經存在 <p> 請輸入其他名稱！</p>";
			request.setAttribute("message", message);
			RequestDispatcher rd;
	        rd = getServletContext().getRequestDispatcher(targetURL);
	        rd.forward(request, response);
			System.out.println(IDBName + "exist!!");
		}		
	}	
}

package com.jb.couponsystem.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.jb.couponsystem.entry.CouponSystem;
import com.jb.couponsystem.enums.ClientType;
import com.jb.couponsystem.exceptions.UserNotFoundException;
import com.jb.couponsystem.exceptions.WrongPasswordException;
import com.jb.couponsystem.facades.*;


@Controller
@CrossOrigin("*")
public class LoginServlet {

	//Attributes	
	@Autowired
	ApplicationContext ctx;


	//Methods
	@CrossOrigin(origins ="*")
	@RequestMapping(value = "/loginservlet", method = RequestMethod.GET)
	public String doGet(
			HttpServletRequest request,
			HttpServletResponse response

			) throws IOException, ServletException
	{
		String name = request.getParameter("nametxt");
		String password = request.getParameter("passwordtxt");
		String  clientType = request.getParameter("clienttype");

		CouponSystem couponSystem = new CouponSystem(ctx);

		try {

			if (clientType.equals("admin")) {
				AdminFacade result = (AdminFacade) couponSystem.login(name, password, ClientType.ADMIN);
				request.getSession().setAttribute("af", result);
				
				//return "redirect:http://localhost:4200";
				return "redirect:admin/index.html";	
	 			
			}

			else if (clientType.equals("company")) {
				CompanyFacade result = (CompanyFacade) couponSystem.login(name, password, ClientType.COMPANY);
				request.getSession().setAttribute("cf", result);
				
				//return "redirect:http://localhost:4200";
				return "redirect:company/index.html";
			}
			
			else if (clientType.equals("customer")) {
				CustomerFacade result = (CustomerFacade) couponSystem.login(name, password, ClientType.CUSTOMER);
				request.getSession().setAttribute("csf", result);
				
				//return "redirect:http://localhost:4200";
				return "redirect:customer/index.html";
				
				
			}
		
		}

		catch (UserNotFoundException e) {
		} 

		catch (WrongPasswordException e) {
		}

		return "redirect:errorpage.html";	

	}


}

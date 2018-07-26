package com.jb.couponsystem.webservices;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jb.couponsystem.entities.*;
import com.jb.couponsystem.entry.CouponSystem;
import com.jb.couponsystem.enums.ClientType;
import com.jb.couponsystem.enums.CouponType;
import com.jb.couponsystem.exceptions.CouponAlreadyExistsException;
import com.jb.couponsystem.facades.CompanyFacade;

/**
 * This class operates as a web service. It contains methods that allow
 * the company logged in the Coupon System
 * to obtain data from and to make changes in the database. 
 * Its methods create the instance of CompanyFacade class for the logged in company 
 * and call it to execute its methods.
 *  
 * @author Alexander Zablotsky
 *
 */
@RestController
public class CompanyWebServices {

	@Autowired
	ApplicationContext ctx;

	/**
	 * This method creates the instance of companyFacade class for the logged in company according to the received request.
	 * If the company did not pass login procedure it returns null.
	 * 
	 * @param request
	 * @return instance of companyFacade class for the logged in company
	 */
	private CompanyFacade getFacade(HttpServletRequest request)
	{
				if (request.getSession().getAttribute("cf") == null)
				{
					System.err.println("Facade was null"); 
					return null;
				}
				
				else {
				CompanyFacade companyFacade = (CompanyFacade)request.getSession().getAttribute("cf");
				return companyFacade;
				}
 
		//"Fake login"
//		CouponSystem couponSystem = new CouponSystem(ctx);
//		CompanyFacade result = (CompanyFacade) couponSystem.login("teva", "123", ClientType.COMPANY);
//		return result;
	}
	
	/**
	 * This method executes the request of the logged in company to display its companyName.
	 * It creates the instance of companyFacade class for the logged in company using getFacade method of the present class.
	 * Then it calls getLoginCompanyName method of the created facade.
	 * 
	 * @param request of the logged in company
	 * @return name of the logged in company
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping( value = "/getlogincompany" ,
	method = RequestMethod.GET)
	public String doGetLoginCompanyName(HttpServletRequest request)
	{

		CompanyFacade companyFacade = this.getFacade(request);
		return companyFacade.getLoginCompanyName();
	}
	

	/**
	 * This method executes the request of the logged in company to display all the values of the enum CouponType.
	 * It creates the array of enum values and returns it.
	 *  
	 * @param request of the logged in company
	 * @return array of enum CouponType values
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping( value = "/getcoupontypes" ,
	method = RequestMethod.GET)
	public List<CouponType> doGetCouponTypes(HttpServletRequest request)
	{
		return Arrays.asList(CouponType.values());
	}



	/**
	 * This method executes the request of the logged in company to display all its coupons that exist in the database.
	 * It creates the instance of companyFacade class for the logged in company using getFacade method of the present class.
	 * Then it calls getAllCoupons method of the created facade.
	 * 
	 * @param request of the logged in company
	 * @return list of coupons of the company
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping( value = "/getcoupon" ,
	method = RequestMethod.GET)
	public Collection<Coupon> doGetAllCoupons(HttpServletRequest request)
	{

		CompanyFacade companyFacade = this.getFacade(request);
		return companyFacade.getAllCoupons();
	}


	/**
	 * This method executes the request of the logged in company to display its coupon with certain ID from the database.
	 * The ID is received as a variable with the request and is mapped in method's URL.
	 * The method creates the instance of companyFacade class for the logged in company using getFacade method of the present class.
	 * Then it calls getCoupon method of the created facade.
	 * 
	 * @param request of the logged in company
	 * @param id of the requested coupon 
	 * @return the requested coupon of the company
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping( value = "/getcoupon/{id}", 
	method = RequestMethod.GET)
	public Coupon doGetCoupon(HttpServletRequest request,
			@PathVariable("id") int id)
	{
		CompanyFacade companyFacade = this.getFacade(request);

		return companyFacade.getCoupon(id);
	}




	/**
	 * This method executes the request of the logged in company to display all its coupons of certain type that exist in the database.
	 * The type is received as a  string variable with the request and is mapped in method's URL.
	 * It creates the instance of companyFacade class for the logged in company using getFacade method of the present class.
	 * Then it calls getCouponsByPrice method of the created facade.
	 * 
	 * 
	 * @param request of the logged in company
	 * @param stringtype type of coupon as String object
	 * @return list of coupons of the company of the given type
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping( value = "/getcouponbytype/{stringtype}", 
	method = RequestMethod.GET)
	public Collection<Coupon> doGetCouponsByType(HttpServletRequest request,
			@PathVariable("stringtype") String stringtype)
	{
		CompanyFacade companyFacade = this.getFacade(request);
		
		CouponType type = CouponType.valueOf(stringtype.toUpperCase());
		
		return companyFacade.getCouponsByType(type);
	}
	
	
	/**
	 * This method executes the request of the logged in company to display all its coupons under certain price that exist in the database.
	 * The price is received as a variable with the request and is mapped in method's URL.
	 * It creates the instance of companyFacade class for the logged in company using getFacade method of the present class.
	 * Then it calls getCouponsByPrice method of the created facade.
	 * 
	 * 
	 * @param request of the logged in company
	 * @param price of coupon
	 * @return list of coupons of the company under the given price
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping( value = "/getcouponbyprice/{price}", 
	method = RequestMethod.GET)
	public Collection<Coupon> doGetCouponsByPrice(HttpServletRequest request,
			@PathVariable("price") double price)
	{
		CompanyFacade companyFacade = this.getFacade(request);

		return companyFacade.getCouponsByPrice(price);
	}

	
	/**
	 * This method executes the request of the logged in company to display all its coupons that expire before certain end date in the database.
	 * The end date is received as a variable with the request and is mapped in method's URL.
	 * It creates the instance of companyFacade class for the logged in company using getFacade method of the present class.
	 * Then it calls getCouponsByEndDate method of the created facade.
	 * 
	 * @param request of the logged in company
	 * @param end date of coupon
	 * @return list of coupons of the company under the given price
	 * @throws ParseException if the given end date is not a valid date
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping( value = "/getcouponbyenddate/{stringenddate}", 
	method = RequestMethod.GET)
	public Collection<Coupon> doGetCouponsByEndDate(HttpServletRequest request,
			@PathVariable("stringenddate") String stringenddate) throws ParseException
	{
		CompanyFacade companyFacade = this.getFacade(request);
	
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date endDate  = dateFormat.parse(stringenddate);
			return companyFacade.getCouponsByEndDate(endDate);
	}
	

	/**
	 * This method executes the request of the logged in company to create coupon object sent in the body of the request in the database.
	 * It creates the instance of companyFacade class for the logged in company using getFacade method of the present class.
	 * Then it calls createCoupon method of the created facade.
	 * 
	 * @param request of the logged in company
	 * @param c coupon object sent in the body of the request
	 * @throws CouponAlreadyExistsException thrown by the method of the facade
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping( value = "/getcoupon",
	method = RequestMethod.POST)
	public void doPostCoupon(HttpServletRequest request,
			@RequestBody Coupon c) 
					throws CouponAlreadyExistsException
	{
		CompanyFacade companyFacade = this.getFacade(request);

		companyFacade.createCoupon(c);	
	}

	
	/**
	 * This method executes the request of the logged in company to update its coupon with the certain ID in the database.
	 * The ID is received as a variable with the request and is mapped in method's URL.
	 * It creates the instance of companyFacade class for the logged in company using getFacade method of the present class.
	 * Then it creates the coupon instance using the received ID.
	 * Then it sets the coupon PRICE and END DATE attributes to be the same as of the coupon sent in the body of the request. 
	 * Finally it calls updateCoupon method of the created facade which receives the created coupon instance as its signature.
	 * 
	 * @param request of the logged in company
	 * @param sent updated coupon sent in the body of the request
	 * @param id of the requested coupon
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping( value = "/getcoupon/{id}",
	method = RequestMethod.PUT)
	public void doPutCoupon(HttpServletRequest request,
			@RequestBody Coupon sent, @PathVariable("id") int id)

	{
		CompanyFacade companyFacade = this.getFacade(request);

		Coupon c = companyFacade.getCoupon(id);

		c.setPrice(sent.getPrice());
		c.setEndDate(sent.getEndDate());

		companyFacade.updateCoupon(c);	
	}
	
	
	
	/**
	 * This method executes the request of the logged in company to remove its coupon with the certain ID from the database.
	 * The ID is received as a variable with the request and is mapped in method's URL. 
	 * The method creates instance of companyFacade class for the logged in company using getFacade method of the present class.
	 * Then it creates the coupon instance using the received ID.
	 * Finally it calls removeCoupon method of the created facade which receives the created coupon instance as its signature.
	 * 
	 * @param request of the logged in company
	 * @param id of the requested coupon
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping( value = "/getcoupon/{id}",
	method = RequestMethod.DELETE)
	public void doDeleteCoupon(HttpServletRequest request,
			@PathVariable("id") int id) 

	{
		CompanyFacade companyFacade = this.getFacade(request);

		Coupon c = companyFacade.getCoupon(id);

		companyFacade.removeCoupon(c);	
	}

}




package com.jb.couponsystem.webservices;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jb.couponsystem.entities.Coupon;
import com.jb.couponsystem.entry.CouponSystem;
import com.jb.couponsystem.enums.ClientType;
import com.jb.couponsystem.enums.CouponType;
import com.jb.couponsystem.exceptions.CouponAlreadyExistsException;
import com.jb.couponsystem.exceptions.CouponAlreadyPurchasedException;
import com.jb.couponsystem.exceptions.CouponExpiredException;
import com.jb.couponsystem.exceptions.CouponNotFoundException;
import com.jb.couponsystem.exceptions.CouponOutOfStockException;
import com.jb.couponsystem.facades.CompanyFacade;
import com.jb.couponsystem.facades.CustomerFacade;

/**
 * This class operates as a web service. It contains methods that allow
 * the customer logged in the Coupon System
 * to obtain data from and to make changes in the database. 
 * Its methods create the instance of CustomerFacade class for the logged in customer 
 * and call it to execute its methods.
 *  
 * @author Alexander Zablotsky
 *
 */
@RestController
public class CustomerWebServices {
	

	@Autowired
	ApplicationContext ctx;
	
	

	/**
	 * This method creates the instance of customerFacade class for the logged in customer according to the received request.
	 * If the customer did not pass login procedure it returns null.
	 * 
	 * @param request
	 * @return instance of customerFacade class for the logged in customer
	 */
	private CustomerFacade getFacade(HttpServletRequest request)
	{
				if (request.getSession().getAttribute("csf") == null)
				{
					System.err.println("Facade was null"); 
					return null;
				}
				
				else {
				CustomerFacade customerFacade = (CustomerFacade)request.getSession().getAttribute("csf");
				return customerFacade;
				}

		//"Fake login"
//		CouponSystem couponSystem = new CouponSystem(ctx);
//		CustomerFacade result = (CustomerFacade) couponSystem.login("avi", "111", ClientType.CUSTOMER);
//		return result;
	}
	
	/**
	 * This method executes the request of the logged in customer to display its companyName.
	 * It creates the instance of customerFacade class for the logged in customer using getFacade method of the present class.
	 * Then it calls getLoginCustomerName method of the created facade.
	 * 
	 * @param request of the logged in customer
	 * @return name of the logged in customer
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping( value = "/getlogincustomer" ,
	method = RequestMethod.GET)
	public String doGetLoginCustomerName(HttpServletRequest request)
	{

		CustomerFacade customerFacade = this.getFacade(request);
		return customerFacade.getLoginCustomerName();
	}
	
	
	/**
	 * This method executes the request of the logged in customer to display all coupons that exist in the database.
	 * It creates the instance of customerFacade class for the logged in customer using getFacade method of the present class.
	 * Then it calls getAllCoupons method of the created facade.
	 * 
	 * @param request of the logged in customer
	 * @return list of coupons in the database
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping( value = "/getallcoupons", 
	method = RequestMethod.GET)
	public Collection<Coupon> doGetAllCoupons(HttpServletRequest request)
	{

		CustomerFacade customerFacade = this.getFacade(request);
		return customerFacade.getAllCoupons();
	}
	
	
	/**
	 * This method executes the request of the logged in customer to display coupon with certain ID from the database.
	 * The ID is received as a variable with the request and is mapped in method's URL.
	 * The method creates the instance of customerFacade class for the logged in company using getFacade method of the present class.
	 * Then it calls getCoupon method of the created facade.
	 * 
	 * @param request of the logged in customer
	 * @param id of the requested coupon 
	 * @return the requested coupon
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping( value = "/getallcoupons/{id}", 
	method = RequestMethod.GET)
	public Coupon doGetCoupon(HttpServletRequest request,
			@PathVariable("id") int id)
	{
		CustomerFacade customerFacade = this.getFacade(request);

		return customerFacade.getCoupon(id);
	}
	
	
	/**
	 * This method executes the request of the logged in customer to purchase coupon
	 *  with certain ID that exists in the database.
	 * The ID is received as a variable with the request and is mapped in method's URL.
	 * It creates the instance of customerFacade class for the logged in customer using getFacade method of the present class.
	 * Then it creates the coupon instance by calling getCoupon method of the created facade using the received ID.
	 * Then it calls purchaseCoupon method of the created facade which receives the created coupon instance as its signature.
	 * 
	 * @param request of the logged in customer
	 * @param id of the requested coupon
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping( value = "/getallcoupons/{id}", 
	method = RequestMethod.PUT)
	public void doPutCoupon(HttpServletRequest request,
			@PathVariable("id") int id)

	{
		CustomerFacade customerFacade = this.getFacade(request);

		Coupon c = customerFacade.getCoupon(id);

		customerFacade.purchaseCoupon(c);	
	}

	
	/**
	 * This method executes the request of the logged in customer to display all coupons
	 * purchased by him that exist in the database.
	 * It creates the instance of customerFacade class for the logged in customer using getFacade method of the present class.
	 * Then it calls getAllPurchasedCoupons method of the created facade.
	 * 
	 * @param request of the logged in customer
	 * @return list of coupons of the customer
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping( value = "/getpurchasedcoupons", 
	method = RequestMethod.GET)
	public Collection<Coupon> doGetAllPurchasedCoupons(HttpServletRequest request)
	{

		CustomerFacade customerFacade = this.getFacade(request);
		return customerFacade.getAllPurchasedCoupons();
	}


	/**
	 * This method executes the request of the logged in customer to display all his coupons of certain type that exist in the database.
	 * The type is received as a  string variable with the request and is mapped in method's URL.
	 * It creates the instance of customerFacade class for the logged in customer using getFacade method of the present class.
	 * Then it calls getAllPurchasedCouponsByPrice method of the created facade.
	 * 
	 * 
	 * @param request of the logged in customer
	 * @param stringtype type of coupon as String object
	 * @return list of coupons of the customer of the given type
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping( value = "/getpurchasedcouponsbytype/{stringtype}", 
	method = RequestMethod.GET)
	public Collection<Coupon> doGetAllPurchasedCouponsByType(HttpServletRequest request,
			@PathVariable("stringtype") String stringtype)
	{
		CustomerFacade customerFacade = this.getFacade(request);
		
		CouponType type = CouponType.valueOf(stringtype.toUpperCase());
		
		return customerFacade.getAllPurchasedCouponsByType(type);
	}
	
	
	/**
	 * This method executes the request of the logged in customer to display all his coupons under certain price that exist in the database.
	 * The price is received as a variable with the request and is mapped in method's URL.
	 * It creates the instance of customerFacade class for the logged in customer using getFacade method of the present class.
	 * Then it calls getAllPurchasedCouponsByPrice method of the created facade.
	 * 
	 * 
	 * @param request of the logged in customer
	 * @param price of coupon
	 * @return list of coupons of the customer under the given price
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping( value = "/getpurchasedcouponsbyprice/{price}", 
	method = RequestMethod.GET)
	public Collection<Coupon> doGetAllPurchasedCouponsByPrice(HttpServletRequest request,
			@PathVariable("price") double price)
	{
		CustomerFacade customerFacade = this.getFacade(request);

		return customerFacade.getAllPurchasedCouponsByPrice(price);
	}
	
	/**
	 * This method executes the request of the logged in customer to display all coupons in the database
	 * that were not purchased by him.
	 * It creates the instance of customerFacade class for the logged in customer using getFacade method of the present class.
	 * Then it calls getAllNonPurchasedCoupons method of the created facade.
	 * 
	 * @param request of the logged in customer
	 * @return list of coupons of the customer
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping( value = "/getnonpurchasedcoupons", 
	method = RequestMethod.GET)
	public Collection<Coupon> doGetAllNonPurchasedCoupons(HttpServletRequest request)
	{
		CustomerFacade customerFacade = this.getFacade(request);
		return customerFacade.getAllNonPurchasedCoupons();
	}

	
	
}

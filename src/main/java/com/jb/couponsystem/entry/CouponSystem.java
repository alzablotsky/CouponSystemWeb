package com.jb.couponsystem.entry;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jb.couponsystem.connection.ConnectionPool;
import com.jb.couponsystem.enums.ClientType;
import com.jb.couponsystem.exceptions.UserNotFoundException;
import com.jb.couponsystem.exceptions.WrongPasswordException;
import com.jb.couponsystem.facades.AdminFacade;
import com.jb.couponsystem.facades.CompanyFacade;
import com.jb.couponsystem.facades.CouponClientFacade;
import com.jb.couponsystem.facades.CustomerFacade;
import com.jb.couponsystem.tasks.DailyExpirationTask;

/**
 * This class manages the Coupon System.
 * It allows the users of all types to log in the system,
 * performs daily task of cleaning the expired coupons 
 * (in its constructor), and executes the system shutdown.
 * This class can generate only one instance in order to provide for 
 * the exclusive management of the system (Singleton design pattern). 
 *  
 * @author Alexander Zablotsky
 *
 */
@Component
@Scope("singleton")
public class CouponSystem {

	//Attributes
	
	private AdminFacade adminFacade;

	private CompanyFacade companyFacade;
	
	private CustomerFacade customerFacade;
	
	private DailyExpirationTask dailyExpirationTask;
	
	//CTORS
	public CouponSystem() {
	
	}

	public CouponSystem(ApplicationContext ctx) {
		
		adminFacade = new AdminFacade(ctx);
		
		companyFacade = new CompanyFacade (ctx);
		
		customerFacade = new CustomerFacade (ctx);
				
		dailyExpirationTask = new DailyExpirationTask(ctx);
		
		//Note - the daily expiration task is not for the application tests.
		//Its thread should not run when the tests are running
		Thread t = new Thread(dailyExpirationTask);
		//t.start();
	}
	
	//Getters and setters
	/**
	 * @return the adminFacade
	 */
	public AdminFacade getAdminFacade() {
		return adminFacade;
	}

	/**
	 * @param adminFacade the adminFacade to set
	 */
	public void setAdminFacade(AdminFacade adminFacade) {
		this.adminFacade = adminFacade;
	}

	/**
	 * @return the companyFacade
	 */
	public CompanyFacade getCompanyFacade() {
		return companyFacade;
	}

	/**
	 * @param companyFacade the companyFacade to set
	 */
	public void setCompanyFacade(CompanyFacade companyFacade) {
		this.companyFacade = companyFacade;
	}

	/**
	 * @return the customerFacade
	 */
	public CustomerFacade getCustomerFacade() {
		return customerFacade;
	}

	/**
	 * @param customerFacade the customerFacade to set
	 */
	public void setCustomerFacade(CustomerFacade customerFacade) {
		this.customerFacade = customerFacade;
	}

	
	/**
	 /*  
	 * @return the dailyExpirationTask
	 */
	public DailyExpirationTask getDailyExpirationTask() {
		return dailyExpirationTask;
	}

	/**
	 * 
	 * @param dailyExpirationTask the dailyExpirationTask to set
	 */
	public void setDailyExpirationTask(DailyExpirationTask dailyExpirationTask) {
		this.dailyExpirationTask = dailyExpirationTask;
	}

	
	//Methods
	/**
	 * This method allows the users to log in the system.
	 * It executes the login method of the user's facade class
	 * according to the user's type (administrator, company or customer).
	 * If the method succeeds, the instance of the user's facade class
	 * is returned, otherwise it returns null.
	 * The method catches all exceptions of the login methods of the facades.
	 * UserNotFoundException and WrongPasswordException are re-thrown.
	 *  
	 * @param name username
	 * @param password user's password
	 * @param clientType user's client type
	 * @return the user facade class if the login succeeds, otherwise return null.
	 */
	public CouponClientFacade login(String name, String password, ClientType clientType) {
		try {	
			switch (clientType)
			{
			case ADMIN:
				AdminFacade myAdminFacade = (AdminFacade) adminFacade.login(name, password);
				return myAdminFacade;

			case COMPANY:
				CompanyFacade myCompanyFacade = (CompanyFacade) companyFacade.login(name, password);
				return myCompanyFacade;

			case CUSTOMER: 
				CustomerFacade myCustomerFacade= (CustomerFacade) customerFacade.login(name, password);
				return myCustomerFacade;

			}
		}
		catch (UserNotFoundException e) {
			System.err.println(e.getMessage());
			throw e;
		} 

		catch (WrongPasswordException e) {
			System.err.println(e.getMessage());
			throw e;
		}

		catch (InterruptedException e) {
			System.err.println(e.getMessage());
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		return null;			

	}
	
	
	
	/**
	 * This method executes shutdown of the coupon system.
	 * It closes all the connections in the connection pool
	 * and terminates running of the daily expiration task
	 * by setting the quit variable as true.	 
	 * @throws SQLException 
	 */
	public void shutdown() throws SQLException {
		
		ConnectionPool.getInstance().closeAllConnections();
		dailyExpirationTask.setQuit(true);
		
	}

}

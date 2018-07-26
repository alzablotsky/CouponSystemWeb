package com.jb.couponsystem.tasks;

import org.springframework.context.ApplicationContext;

import com.jb.couponsystem.dbdao.CouponDBDAO;
import com.jb.couponsystem.exceptions.CouponNotFoundException;

/**
 * This method runs the task of cleaning expired coupons once a day.
 * The run method calls the instance of couponDBDAO class to remove 
 * expired coupons from the database. The method runs in while loop
 * while the system did not quit.
 * 
 * @author Alexander Zablotsky
 *
 */
public class DailyExpirationTask implements Runnable {

	//Attributes
	private ApplicationContext ctx;
	
	private boolean quit = false;
	
	
//CTOR
	public DailyExpirationTask(ApplicationContext ctx) {
		this.ctx = ctx;
	}


	//Getters and setters

	/**
	 * 
	 * @return the quit
	 */
	public boolean isQuit() {
		return quit;
	}

	/**
	 * 
	 * @param quit the quit to set
	 */
	public void setQuit(boolean quit) {
		this.quit = quit;
	}

	//Methods
	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
		while (!quit) {
         try {
        	 
        CouponDBDAO couponDBDAO= ctx.getBean(CouponDBDAO.class);	 
        	 
        System.out.println("Thread id: "
				+ Thread.currentThread().getId() + " started."
				+ " Performing expiration task...");

		couponDBDAO.removeExpiredCoupons();
		
		System.out.println("Thread id: "
				+ Thread.currentThread().getId() +  " performed expiration task. Now it goes sleeping till the next day.");
		
		Thread.sleep(1000*60*60*24);
		
         }
         catch (InterruptedException e) {
        	   System.err.println(e.getMessage());
        	   e.printStackTrace();
	}
         catch (CouponNotFoundException e) {
      	   System.err.println(e.getMessage());
      	   //e.printStackTrace();
	}
	}
}
}

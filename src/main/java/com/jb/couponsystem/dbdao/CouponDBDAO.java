package com.jb.couponsystem.dbdao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.jb.couponsystem.connection.ConnectionPool;
import com.jb.couponsystem.connection.DbConnection;
import com.jb.couponsystem.dao.CouponDAO;
import com.jb.couponsystem.entities.Company;
import com.jb.couponsystem.entities.Coupon;
import com.jb.couponsystem.entities.Customer;
import com.jb.couponsystem.enums.CouponType;
import com.jb.couponsystem.exceptions.CouponAlreadyExistsException;
import com.jb.couponsystem.exceptions.CouponAlreadyPurchasedException;
import com.jb.couponsystem.exceptions.CouponExpiredException;
import com.jb.couponsystem.exceptions.CouponNotFoundException;
import com.jb.couponsystem.exceptions.CouponOutOfStockException;
import com.jb.couponsystem.exceptions.IllegalUpdateException;
import com.jb.couponsystem.exceptions.UserAlreadyExistsException;
import com.jb.couponsystem.exceptions.UserNotFoundException;
import com.jb.couponsystem.facades.CompanyFacade;
import com.jb.couponsystem.repo.CouponRepo;

/**
 * This class contains methods that call CouponRepo interface in order to 
 * perform operations on coupon objects in the database.
 * Since the number of connections to the system is limited, each method begins with sending  
 * a request for connection from the pool of connections.
 * Also, at the end of each method the connection is returned back to the connection pool.  
 *   
 * @author Alexander Zablotsky
 *
 */
@Component
public class CouponDBDAO implements CouponDAO {

	//Attributes
	@Autowired
	CouponRepo couponRepo;

	private Company loginCompany;

	private Customer loginCustomer;

	//Getters and setters for login company and login customer

	/**
	 * 
	 * @return the loginCompany
	 */
	public Company getLoginCompany() {
		return loginCompany;
	}

	/**
	 * 
	 * @param loginCompany the loginCompany to set
	 */
	public void setLoginCompany(Company loginCompany) {
		this.loginCompany = loginCompany;
	}


	/**
	 * 
	 * @return the loginCustomer
	 */
	public Customer getLoginCustomer() {
		return loginCustomer;
	}

	/**
	 *  
	 * @param loginCustomer the loginCustomer to set
	 */
	public void setLoginCustomer(Customer loginCustomer) {
		this.loginCustomer = loginCustomer;
	}

	//Methods

	/*
	 * 
	 * Create coupon
	 * 
	 * (non-Javadoc)
	 * @see com.jb.couponsystem.dao.CouponDAO#createCoupon(com.jb.couponsystem.entities.Coupon)
	 */
	@Override
	public void createCoupon(Coupon c) throws CouponAlreadyExistsException, InterruptedException, SQLException {
		
		//Ask for connection from the connection pool
		Connection dbConnection = ConnectionPool.getInstance().getConnection();

		//If a coupon with this ID already exists - return connection and throw exception
		if (couponRepo.existsById(c.getId())) {

			ConnectionPool.getInstance().returnConnection(dbConnection);

			throw new CouponAlreadyExistsException ("Cannot create new coupon. Coupon id=" + c.getId() + " already exists.");
		}

		//If a coupon with this name already exists - return connection and throw exception
		if (couponRepo.existsByTitle(c.getTitle())) {

			ConnectionPool.getInstance().returnConnection(dbConnection);

			throw new CouponAlreadyExistsException ("Cannot create new coupon. Coupon title " + c.getTitle() + " already exists.");
		}
		//Otherwise - create coupon and return connection	
		else {
			couponRepo.save(c);

			ConnectionPool.getInstance().returnConnection(dbConnection);

		}

	}


	/*
	 * 
	 * Remove coupon
	 * 
	 * (non-Javadoc)
	 * @see com.jb.couponsystem.dao.CouponDAO#removeCoupon(com.jb.couponsystem.entities.Coupon)
	 */
	@Override
	public void removeCoupon(Coupon c) throws CouponNotFoundException, InterruptedException, SQLException {

		//Ask for connection from the connection pool
		Connection dbConnection = ConnectionPool.getInstance().getConnection();

		//If a coupon with this ID of the company with this ID does not exist - return connection and throw exception
		if (!couponRepo.existsByIdAndCompanyId(c.getId(), this.loginCompany.getId())) {

			ConnectionPool.getInstance().returnConnection(dbConnection);

			throw new CouponNotFoundException ("Cannot remove coupon. "
					+ "Coupon " + c + " of company " + this.loginCompany.getCompanyName()
					+ " does not exist.");
		}

		//Otherwise - remove coupon	and return connection
		else {
			couponRepo.removeCouponByIdAndCompanyId(c.getId(), this.loginCompany.getId());

			ConnectionPool.getInstance().returnConnection(dbConnection);

		}

	}

	/*
	 * 
	 * Update coupon
	 * 
	 * (non-Javadoc)
	 * @see com.jb.couponsystem.dao.CouponDAO#updateCoupon(com.jb.couponsystem.entities.Coupon)
	 */
	@Override
	public void updateCoupon(Coupon c) throws CouponNotFoundException, IllegalUpdateException, InterruptedException, SQLException {

		//Ask for connection from the connection pool
		Connection dbConnection = ConnectionPool.getInstance().getConnection();
		
		
		//Compare to the coupon in DB
		Coupon couponInDb= couponRepo.findByTitleAndCompanyId(c.getTitle(),this.loginCompany.getId());

		//If a coupon with this title of this company does not exist in DB - throw exception
		if(couponInDb==null) {
			ConnectionPool.getInstance().returnConnection(dbConnection);

			throw new CouponNotFoundException ("Cannot update coupon. "
					+ "Coupon " + c.getTitle() + " of company " + this.loginCompany.getCompanyName()
					+ " does not exist.");
		}

		//If the coupon attributes other than END DATE and PRICE 
		//are changed - return connection and throw exception:
		
		
		
		else {
		//Set null string fields to blank strings in order to compare them by equals() method	
		Coupon c2= c;
		Coupon couponInDb2= couponInDb;
		
		if (c2.getMessage()==null) c2.setMessage("");
		if (couponInDb2.getMessage()==null) couponInDb2.setMessage("");
		
		if (c2.getImage()==null) c2.setImage(new byte[0]);
		if (couponInDb2.getImage()==null) couponInDb2.setImage(new byte[0]);
		
		//1.ID
		if (couponInDb.getId()!=(c.getId())) {
			ConnectionPool.getInstance().returnConnection(dbConnection);
			throw new IllegalUpdateException ("Cannot update coupon "+ c.getTitle() 
			+  ". Coupon id cannot be changed."); 
		}

		//2. Start date
		
		else if (couponInDb.getStartDate().compareTo(c.getStartDate()) != 0 ) {
			ConnectionPool.getInstance().returnConnection(dbConnection);
			throw new IllegalUpdateException ("Cannot update coupon "+ c.getTitle() 
			+  ". Start date cannot be changed."); 
		}

		//3. Amount
		else if (couponInDb.getAmount()!=(c.getAmount())) {
			ConnectionPool.getInstance().returnConnection(dbConnection);
			throw new IllegalUpdateException ("Cannot update coupon "+ c.getTitle() 
			+  ". Amount cannot be changed."); 
		}

		//4. Type
		
		else if (couponInDb.getType()!= c.getType()) {
			ConnectionPool.getInstance().returnConnection(dbConnection);
			throw new IllegalUpdateException ("Cannot update coupon "+ c.getTitle() 
			+  ". Coupon type cannot be changed.");			
		}
		
		//5. Message
		else if (!c2.getMessage().equals(couponInDb2.getMessage())) {
			ConnectionPool.getInstance().returnConnection(dbConnection);
			throw new IllegalUpdateException ("Cannot update coupon "+ c.getTitle() 
			+  ". Message cannot be changed."); 
		}
		

		//6. Image
		
		//else if (c.getImage().length() != couponInDb.getImage().length()) {
		
		else if (c2.getImage().length != couponInDb2.getImage().length) {
			ConnectionPool.getInstance().returnConnection(dbConnection);
			
			throw new IllegalUpdateException ("Cannot update coupon "+ c.getTitle() 
			+  ". Image cannot be changed.");
		}

		//Otherwise - update coupon and return connection
		else {
		couponInDb.setEndDate(c.getEndDate());	
		couponInDb.setPrice(c.getPrice());	
		couponRepo.save(couponInDb);
		ConnectionPool.getInstance().returnConnection(dbConnection);
		}
	}
}


	/*
	 *
	 * Get company's coupon by id 
	 * 
	 * (non-Javadoc)
	 * @see com.jb.couponsystem.dao.CouponDAO#getCompanyCoupon(long)
	 */
	@Cacheable(value = "coupon", key = "#id")
	@Override
	public Coupon getCompanyCoupon(long id) throws CouponNotFoundException, InterruptedException, SQLException {

		//Ask for connection from the connection pool
		Connection dbConnection = ConnectionPool.getInstance().getConnection();

		//If coupon with this ID of the company with this ID does not exist -  return connection and throw exception
		if (!couponRepo.existsByIdAndCompanyId(id, this.loginCompany.getId()))  {

			ConnectionPool.getInstance().returnConnection(dbConnection);	

			throw new CouponNotFoundException ("Coupon id=" + id + " of company " + this.loginCompany.getCompanyName()
			+ " was not found.");
		}
		//Otherwise -   return connection and return coupon
		else {
			Coupon c = couponRepo.findByIdAndCompanyId(id, this.loginCompany.getId());

			ConnectionPool.getInstance().returnConnection(dbConnection);

			return c;
		}

	}


	/*
	 * Get all company's coupons
	 * 
	 * (non-Javadoc)
	 * @see com.jb.couponsystem.dao.CouponDAO#getAllCompanyCoupons()
	 */
	@Override
	public Collection<Coupon> getAllCompanyCoupons() throws CouponNotFoundException, InterruptedException, SQLException {

		//Ask for connection from the connection pool
		Connection dbConnection = ConnectionPool.getInstance().getConnection();

		Collection<Coupon> coupons =
				(Collection<Coupon>) couponRepo.findCouponByCompanyId(this.loginCompany.getId());

		//If no coupons exist -  return connection and throw exception
		if (coupons.isEmpty())  {

			ConnectionPool.getInstance().returnConnection(dbConnection);

			throw new CouponNotFoundException ("No coupons of company "
					+ this.loginCompany.getCompanyName() + " were found.");
		}

		//Otherwise - return connection and return all the coupons 
		else {

			ConnectionPool.getInstance().returnConnection(dbConnection);

			return coupons;
		}
	}

	/*
	 * 
	 * Get company's coupons by type
	 * 
	 * (non-Javadoc)
	 * @see com.jb.couponsystem.dao.CouponDAO#getCompanyCouponByType(com.jb.couponsystem.entities.CouponType)
	 */
	@Override
	public Collection<Coupon> getCompanyCouponsByType(CouponType type) throws CouponNotFoundException, InterruptedException, SQLException {

		//Ask for connection from the connection pool
				Connection dbConnection = ConnectionPool.getInstance().getConnection();

		Collection<Coupon> coupons =
				(Collection<Coupon>) couponRepo.findCouponByTypeAndCompanyId(type, this.loginCompany.getId());	

		//If no coupons exist - return connection and throw exception
		if (coupons.isEmpty())  {

			ConnectionPool.getInstance().returnConnection(dbConnection);

			throw new CouponNotFoundException ("No coupons of type "+ type + " of company "
					+ this.loginCompany.getCompanyName() + " were found.");
		}
		//Otherwise - return connection and return the coupons 
		else {

			ConnectionPool.getInstance().returnConnection(dbConnection);

			return coupons;
		}
	}

	/*
	 * 
	 * Get company's  coupons under certain price
	 * 
	 * (non-Javadoc)
	 * @see com.jb.couponsystem.dao.CouponDAO#getCompanyCouponsUnderPrice(double)
	 */
	@Override
	public Collection<Coupon> getCompanyCouponsByPrice(double price) throws CouponNotFoundException, InterruptedException, SQLException {

		//Ask for connection from the connection pool
		Connection dbConnection = ConnectionPool.getInstance().getConnection();
		Collection<Coupon> coupons =
				(Collection<Coupon>) couponRepo.findByMaxPriceAndCompanyId(price, this.loginCompany.getId());

		//If no coupons exist - return connection and throw exception
		if (coupons.isEmpty())  {

			ConnectionPool.getInstance().returnConnection(dbConnection);

			throw new CouponNotFoundException ("No coupons under price "+ price + " of company "
					+ this.loginCompany.getCompanyName() + " were found.");
		}
		//Otherwise - return connection and return the coupons 
		else {

			ConnectionPool.getInstance().returnConnection(dbConnection);

			return coupons;
		}
	}


	/*
	 * 
	 * Get company's  coupons before certain date
	 * 
	 * (non-Javadoc)
	 * @see com.jb.couponsystem.dao.CouponDAO#getCompanyCouponsBeforeEndDate(java.util.Date)
	 */
	@Override
	public Collection<Coupon> getCompanyCouponsByEndDate(Date endDate) throws CouponNotFoundException, InterruptedException, SQLException {

		//Ask for connection from the connection pool
		Connection dbConnection = ConnectionPool.getInstance().getConnection();

		Collection<Coupon> coupons =
				(Collection<Coupon>) couponRepo.findByMaxEndDateAndCompanyId(endDate, this.loginCompany.getId());

		//If no coupons exist - return connection and throw exception
		if (coupons.isEmpty())  {

			ConnectionPool.getInstance().returnConnection(dbConnection);

			throw new CouponNotFoundException ("No coupons with end date before "+ endDate + " of company "
					+ this.loginCompany.getCompanyName() + " were found.");
		}
		//Otherwise - return connection and return the coupons 
		else {

			ConnectionPool.getInstance().returnConnection(dbConnection);

			return coupons;
		}
	}

	/*
	 * Get coupon by ID
	 * 
	 * (non-Javadoc)
	 * @see com.jb.couponsystem.dao.CouponDAO#getCoupon(long)
	 */
	@Cacheable(value = "coupon", key = "#id")
	@Override
	public Coupon getCoupon(long id) throws CouponNotFoundException, InterruptedException, SQLException {
		//Ask for connection from the connection pool
		Connection dbConnection = ConnectionPool.getInstance().getConnection();

		//If coupon with this ID does not exist -  return connection and throw exception
		if (!couponRepo.existsById(id))  {

			ConnectionPool.getInstance().returnConnection(dbConnection);	

			throw new CouponNotFoundException ("Coupon id=" + id + " was not found.");
		}
		//Otherwise -  return connection and return coupon
		else {
			Coupon c = couponRepo.findCouponById(id);

			ConnectionPool.getInstance().returnConnection(dbConnection);

			return c;
		}

	}

	/*
	 * Get all coupons
	 * 
	 * (non-Javadoc)
	 * @see com.jb.couponsystem.dao.CouponDAO#getAllCoupons()
	 */
	@Override
	public Collection<Coupon> getAllCoupons() throws CouponNotFoundException, InterruptedException, SQLException {
		
		//Ask for connection from the connection pool
		Connection dbConnection = ConnectionPool.getInstance().getConnection();

		Collection<Coupon> coupons =
				(Collection<Coupon>) couponRepo.findAll();


		//If no coupons exist -  return connection and throw exception
		if (coupons.isEmpty())  {

			ConnectionPool.getInstance().returnConnection(dbConnection);

			throw new CouponNotFoundException ("No coupons were found.");
		}

		//Otherwise - return connection and return all the coupons 
		else {

			ConnectionPool.getInstance().returnConnection(dbConnection);

			return coupons;
		}
	}


	/*
	 * Purchase coupon
	 * 
	 * (non-Javadoc)
	 * @see com.jb.couponsystem.dao.CouponDAO#purchaseCoupon(com.jb.couponsystem.entities.Coupon)
	 */
	@Override
	public void purchaseCoupon (Coupon c) 
			throws CouponNotFoundException, CouponAlreadyPurchasedException, CouponOutOfStockException, CouponExpiredException, InterruptedException, SQLException {

		//Ask for connection from the connection pool
		Connection dbConnection = ConnectionPool.getInstance().getConnection();

		//Collection <Customer> couponCustomers = c.getCustomers();	
		Date today= new Date();

		//If the coupon does not exist - return connection and throw exception
		if (!couponRepo.existsById(c.getId())) {
			ConnectionPool.getInstance().returnConnection(dbConnection);
			throw new CouponNotFoundException("Customer "+this.loginCustomer.getCustomerName()
			+" cannot purchase coupon. Coupon "+ c.getTitle()+ " does not exist.");	
		}

		//If the customer had already purchased this coupon - return connection and throw exception
		else if(couponRepo.findCustomerCoupon(this.loginCustomer.getId(), c.getId()) != null) {
			ConnectionPool.getInstance().returnConnection(dbConnection);
			throw new CouponAlreadyPurchasedException("Customer "+this.loginCustomer.getCustomerName()
			+" cannot purchase coupon. Coupon "+ c.getTitle()+ " has already been purchased by this customer.");		
		}

		//If the coupon is out of stock - return connection and throw exception
		else if (c.getAmount()==0) {
			ConnectionPool.getInstance().returnConnection(dbConnection);
			throw new CouponOutOfStockException("Customer "+this.loginCustomer.getCustomerName()
			+" cannot purchase coupon. Coupon "+ c.getTitle()+ " is out of stock.");	
		}	

		//If the coupon has expired - return connection and throw exception
		else if (c.getEndDate().before(today)) {
			ConnectionPool.getInstance().returnConnection(dbConnection);
			throw new CouponExpiredException("Customer "+this.loginCustomer.getCustomerName()
			+" cannot purchase coupon. Coupon "+ c.getTitle()+ " has expired.");		
		}

		//Otherwise - add the customer to coupon customers, update amount and return connection
		else {

			c.getCustomers().add(this.loginCustomer);
			c.setAmount(c.getAmount()-1);
			couponRepo.save(c);
			ConnectionPool.getInstance().returnConnection(dbConnection);
		}

	}
	
	/*
	 * Get all purchased coupons
	 * 
	 * (non-Javadoc)
	 * @see com.jb.couponsystem.dao.CouponDAO#getAllPurchasedCouponsByType(com.jb.couponsystem.enums.CouponType)
	 */
	@Override
	public Collection<Coupon> getAllPurchasedCoupons() throws CouponNotFoundException, InterruptedException, SQLException {

		//Ask for connection from the connection pool
		Connection dbConnection = ConnectionPool.getInstance().getConnection();

		Collection<Coupon> coupons =
				(Collection<Coupon>) couponRepo.findCustomerCoupons(this.loginCustomer.getId());	

		//If the customer does not have coupons of this type - return connection and throw exception
		if (coupons.isEmpty())  {
			ConnectionPool.getInstance().returnConnection(dbConnection);
			throw new CouponNotFoundException ("Customer " + this.loginCustomer.getCustomerName() +
					" has not purchased any coupons.");
		}
		//Otherwise - return connection and return the coupons 
		else {
			ConnectionPool.getInstance().returnConnection(dbConnection);
			return coupons;
		}
	}
	

	/*
	 * Get all purchased coupons by type
	 * 
	 * (non-Javadoc)
	 * @see com.jb.couponsystem.dao.CouponDAO#getAllPurchasedCouponsByType(com.jb.couponsystem.enums.CouponType)
	 */
	@Override
	public Collection<Coupon> getAllPurchasedCouponsByType(CouponType type) throws CouponNotFoundException, InterruptedException, SQLException {

		//Ask for connection from the connection pool
		Connection dbConnection = ConnectionPool.getInstance().getConnection();

		Collection<Coupon> coupons =
				(Collection<Coupon>) couponRepo.findCustomerCouponsByType(this.loginCustomer.getId(), type);	

		//If the customer does not have coupons of this type - return connection and throw exception
		if (coupons.isEmpty())  {
			ConnectionPool.getInstance().returnConnection(dbConnection);
			throw new CouponNotFoundException ("Customer " + this.loginCustomer.getCustomerName() +
					" has not purchased coupons of type "+ type + ".");
		}
		//Otherwise - return connection and return the coupons 
		else {
			ConnectionPool.getInstance().returnConnection(dbConnection);
			return coupons;
		}
	}

	/*
	 * Get all purchased coupons by price
	 * 
	 * (non-Javadoc)
	 * @see com.jb.couponsystem.dao.CouponDAO#getAllPurchasedCouponsByPrice(double)
	 */
	@Override
	public Collection<Coupon> getAllPurchasedCouponsByPrice(double price) throws CouponNotFoundException, InterruptedException, SQLException {

		//Ask for connection from the connection pool
		Connection dbConnection = ConnectionPool.getInstance().getConnection();

		Collection<Coupon> coupons =
				(Collection<Coupon>) couponRepo.findCustomerCouponsByMaxPrice(this.loginCustomer.getId(), price);

		//If the customer does not have coupons under this price - return connection and throw exception
		if (coupons.isEmpty())  {
			ConnectionPool.getInstance().returnConnection(dbConnection);
			throw new CouponNotFoundException ("Customer " + this.loginCustomer.getCustomerName() +
					" has not purchased coupons under price "+ price + ".");
		}

		//Otherwise - return connection and return the coupons 
		else {
			ConnectionPool.getInstance().returnConnection(dbConnection);
			return coupons;
		} 
	}
	
	
	/*
	 * Get all non-purchased coupons
	 * 
	 * (non-Javadoc)
	 * @see com.jb.couponsystem.dao.CouponDAO#getAllPurchasedCouponsByType(com.jb.couponsystem.enums.CouponType)
	 */
	@Override
	public Collection<Coupon> getAllNonPurchasedCoupons() throws CouponNotFoundException, InterruptedException, SQLException {
		
		//Ask for connection from the connection pool
		Connection dbConnection = ConnectionPool.getInstance().getConnection();
		
		Collection<Coupon> nonPurchased =(Collection<Coupon>) couponRepo.findNonCustomerCoupons(this.loginCustomer.getId());
				
		//If the customer does not have coupons of this type - return connection and throw exception
		if (nonPurchased.isEmpty())  {
			ConnectionPool.getInstance().returnConnection(dbConnection);
			throw new CouponNotFoundException ("Customer " + this.loginCustomer.getCustomerName() +
					" has purchased all the existing coupons.");
		}
		//Otherwise - return connection and return the coupons 
		else {
			ConnectionPool.getInstance().returnConnection(dbConnection);
			return nonPurchased;
		}
		
	}
	
	/*
	 * 
	 * Remove coupon
	 * 
	 * (non-Javadoc)
	 * @see com.jb.couponsystem.dao.CouponDAO#removeCoupon(com.jb.couponsystem.entities.Coupon)
	 */
	@Override
	public void removeCompanyCoupons(long id) throws InterruptedException, SQLException {

		//Ask for connection from the connection pool
		Connection dbConnection = ConnectionPool.getInstance().getConnection();

		//If a coupon with this ID of the company with this ID does not exist - return connection and print message
		if (!couponRepo.existsByCompanyId(id)) {
			ConnectionPool.getInstance().returnConnection(dbConnection);
			System.out.println("No coupons of company id " + id + " were found.");
		}

		//Otherwise - remove coupon	and return connection
		else {
			couponRepo.removeCouponByCompanyId(id);
			ConnectionPool.getInstance().returnConnection(dbConnection);

		}

	}
	

	/**
	 * This method removes all expired coupons from the database.
	 * It creates a list of coupons and a today Date object.
	 * Then in the for-each loop it calls the CouponRepo Interface
	 * to remove those coupons the end date of which is before today from the database.
	 * 
	 * @throws CouponNotFoundException if there is no coupons in the database
	 * 
	 */
	public void removeExpiredCoupons () throws CouponNotFoundException {

		System.out.println("Searching for expired coupons...");

		Date today= new Date();

		if (!couponRepo.anyCouponsExist()) {
			throw new CouponNotFoundException ("No coupons found.");
		}
		else {
			List<Coupon> coupons = (List<Coupon>) couponRepo.findAll();	
			for (Coupon c : coupons) {
				if (c.getEndDate().before(today)) {

					couponRepo.removeCouponById(c.getId());

					System.err.println("Coupon " + c.getTitle() + " has expired."
							+ " It was removed from the database.");
				}
			}
		}
	}

}

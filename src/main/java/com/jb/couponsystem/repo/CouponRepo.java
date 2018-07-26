package com.jb.couponsystem.repo;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.jb.couponsystem.entities.*;
import com.jb.couponsystem.enums.CouponType;

/**
 * This interface contains query methods that refer to the coupon objects in the database.
 *  
 * @author Alexander Zablotsky
 *
 */

public interface CouponRepo extends CrudRepository<Coupon, Long>{


	/**
	 * This method checks if a coupon with a given title exists in the database. 
	 * It returns true if it exists, false otherwise.
	 * 
	 * @param title coupon title
	 * @return true if a coupon with a given title exists, false otherwise 
	 */
	@Query("SELECT CASE WHEN COUNT(c) > 0 THEN 'true' ELSE 'false' END FROM COUPONS c WHERE UPPER(c.title) = UPPER(:title)") 
	boolean existsByTitle(@Param("title") String title);


	@Query("SELECT CASE WHEN COUNT(c) > 0 THEN 'true' ELSE 'false' END FROM COUPONS c") 
	boolean anyCouponsExist();
	
	/**
	 * This method checks if a coupon with a given  id and company id exists in the database. 
	 * It returns true if it exists, false otherwise.
	 * 
	 * 
	 * @param id coupon ID
	 * @param companyId company ID
	 * @return if a coupon with a given id and company id exists, false otherwise 
	 */
	@Query("SELECT CASE WHEN COUNT(c) > 0 THEN 'true' ELSE 'false' END FROM COUPONS c WHERE c.id = :id AND  c.company.id = :companyId") 
	boolean existsByIdAndCompanyId(@Param("id") long id, @Param("companyId") long companyId);

	/**
	 * This method checks if a coupon with a given  id and company id exists in the database. 
	 * It returns true if it exists, false otherwise.
	 * 
	 * 
	 * @param companyId company ID
	 * @return if any coupon with a given company id exists, false otherwise 
	 */
	@Query("SELECT CASE WHEN COUNT(c) > 0 THEN 'true' ELSE 'false' END FROM COUPONS c WHERE c.company.id = :companyId") 
	boolean existsByCompanyId(@Param("companyId") long companyId);

	/**
	 * This method deletes coupon from the database. The coupon is found by its ID received as a parameter from the outside.
	 * 
	 * @param id coupon ID
	 *
	 */
	@Transactional
	@Modifying
	@Query("DELETE FROM COUPONS c WHERE c.id = :id")
	void removeCouponById(@Param("id") long id);

	/**
	 * This method deletes company's coupon from the database. The coupon is found by its ID and company ID received as parameters from the outside.
	 * 
	 * @param id coupon ID
	 * @param companyId company ID
	 */
	@Transactional
	@Modifying
	@Query("DELETE FROM COUPONS c WHERE c.id = :id AND  c.company.id = :companyId")
	void removeCouponByIdAndCompanyId(@Param("id") long id, @Param("companyId") long companyId);


	/**
	 * This method deletes company's coupons from the database. 
	 * The coupons are found by its company ID received as parameters from the outside.
	 * 
	 * @param companyId company ID
	 */
	@Transactional
	@Modifying
	@Query("DELETE FROM COUPONS c WHERE c.company.id = :companyId")
	void removeCouponByCompanyId(@Param("companyId") long companyId);

	
	
	/**
	 * This method finds and returns company's coupon in the database by its title and company ID received as parameters from the outside. 
	 * 
	 * @param title coupon title
	 * @param companyId company ID
	 * @return coupon object
	 */
	@Query("SELECT c FROM COUPONS c WHERE UPPER(c.title) = UPPER(:title) AND c.company.id = :companyId") 
	Coupon findByTitleAndCompanyId(@Param("title") String title, @Param("companyId") long companyId);


	/**
	 * This method finds and returns company's coupon in the database by its ID and company ID received as parameters from the outside.
	 * 
	 * @param id coupon ID
	 * @param companyId company ID
	 * @return coupon object
	 */
	@Query("SELECT c FROM COUPONS c WHERE c.id = :id AND  c.company.id = :companyId") 
	Coupon findByIdAndCompanyId(@Param("id") long id, @Param("companyId") long companyId);


	/**
	 * This method finds and returns company's coupon in the database by its ID received as parameter from the outside.
	 * 
	 * @param id coupon ID
	 * @return coupon object
	 */
	@Query("SELECT c FROM COUPONS c WHERE c.id = :id") 
	Coupon findCouponById(@Param("id") long id);
	
	/**
	 * This method finds and returns company's coupons in the database by their company ID received as a parameter from the outside.
	 * 
	 * @param companyId company ID
	 * @return list of coupons
	 */
	List<Coupon> findCouponByCompanyId(long companyId);


	/**
	 * This method finds and returns company's coupons in the database by their type and company ID received as parameters from the outside.
	 * 
	 * @param type coupon type
	 * @param companyId company ID
	 * @return list of coupons
	 */
	List<Coupon> findCouponByTypeAndCompanyId(CouponType type, long companyId);


	/**
	 * 
	 * This method finds and returns company's coupons in the database the price of which is 
	 * lower than the given price. Price and company ID are received as parameters from the outside.
	 * 
	 * @param price maximal price of the coupon
	 * @param companyId company ID
	 * @return collection of coupons
	 */
	@Query("SELECT c FROM COUPONS c WHERE c.price <= :price AND  c.company.id = :companyId") 
	Collection<Coupon> findByMaxPriceAndCompanyId( @Param("price") double price,  @Param("companyId") long companyId);



	/**
	 * This method finds and returns company's coupons in the database the end date of which is 
	 * before the given end date. End date and company ID are received as parameters from the outside.
	 * 
	 * @param endDate maximal end date
	 * @param companyId company ID
	 * @return collection of coupons
	 */
	@Query("SELECT c FROM COUPONS c WHERE c.endDate <= :endDate AND  c.company.id = :companyId") 
	Collection<Coupon> findByMaxEndDateAndCompanyId( @Param("endDate") Date endDate,  @Param("companyId") long companyId);



	/**
	 * This method finds and returns customer's coupon in the database by coupon ID
	 * and customer ID received as parameters from the outside.
	 *  
	 * @param customerId customer ID
	 * @param couponId coupon ID
	 * @return coupon object
	 */
	@Query("SELECT coup FROM COUPONS coup WHERE coup.id = :couponId AND coup.id IN (SELECT coup.id FROM coup.customers c WHERE c.id = :customerId)") 
	Coupon findCustomerCoupon(@Param("customerId") long customerId, @Param("couponId") long couponId);


	
	/**
	 * 
	 * This method finds and returns all customer's coupons of the given type in the database.
	 * Coupon type and customer ID are received as parameters from the outside.
	 * 
	 * @param customerId customer ID
	 * @param type coupon type
	 * @return collection of coupons
	 */
	@Query("SELECT coup FROM COUPONS coup WHERE coup.id IN (SELECT coup.id FROM coup.customers c WHERE c.id = :customerId) AND coup.type = :type") 
	Collection <Coupon> findCustomerCouponsByType(@Param("customerId") long customerId, @Param("type") CouponType type);


	/**
	 * This method finds and returns all customer's coupons under the given price in the database.
	 * Coupon price and customer ID are received as parameters from the outside.
	 * 	
	 * @param customerId customer ID
	 * @param price coupon price
	 * @return collection of coupons
	 */
	@Query("SELECT coup FROM COUPONS coup WHERE coup.id IN (SELECT coup.id FROM coup.customers c WHERE c.id = :customerId) AND coup.price <= :price") 
	Collection <Coupon> findCustomerCouponsByMaxPrice(@Param("customerId") long customerId, @Param("price") double price);


	/**
	 * This method finds and returns coupons in the database by their title received as a parameter from the outside.
	 * 
	 * @param title coupon title
	 * @return list of coupons
	 */
	List<Coupon> findCouponByTitleIgnoreCase(String title);	


	/**
	 * 
	 * This method finds and returns coupon in the database by its title  received as a parameter from the outside.
	 * 
	 * @param title coupon title
	 * @return coupon object
	 */
	@Query(value = "SELECT * FROM COUPONS WHERE UPPER(TITLE) = UPPER(:title)", nativeQuery= true ) 
	Coupon findByTitle(@Param("title") String title);		


	/**
	 * 
	 * This method finds and returns all customer's coupons in the database by customer ID received as a parameter from the outside..
	 * 
	 * @param customerId customer ID
	 * @return collection of coupons
	 */
	@Query("SELECT coup FROM COUPONS coup WHERE coup.id IN (SELECT coup.id FROM coup.customers c WHERE c.id = :customerId)") 
	Collection <Coupon> findCustomerCoupons(@Param("customerId") long customerId);		

	/**
	 * 
	 * This method finds and returns all non-customer's coupons in the database by customer ID received as a parameter from the outside..
	 * 
	 * @param customerId customer ID
	 * @return collection of coupons
	 */
	@Query("SELECT coup FROM COUPONS coup WHERE coup.id NOT IN (SELECT coup.id FROM coup.customers c WHERE c.id = :customerId)") 
	Collection <Coupon> findNonCustomerCoupons(@Param("customerId") long customerId);		

	
}
package com.jb.couponsystem.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.jb.couponsystem.entities.*;

/**
 * This interface contains query methods that refer to the customer objects in the database.
 *  
 * @author Alexander Zablotsky
 *
 */
public interface CustomerRepo extends CrudRepository<Customer, Long> {

	/**
	 * This method finds customers from the database by customer name received as a parameter
	 * from the outside and returns them as list of objects.
	 * 
	 * @param customerName customer name
	 * @return list of customers
	 */
	List<Customer> findCustomerByCustomerNameIgnoreCase(String customerName);

	
	/**
	 * This method checks if a customer with a given name exists in the database. 
	 * It returns true if it exists, false otherwise.
	 * 
	 * @param customerName customer name
	 * @return true if a customer with a given name exists, false otherwise 
	 */
	@Query("SELECT CASE WHEN COUNT(c) > 0 THEN 'true' ELSE 'false' END FROM CUSTOMERS c WHERE UPPER(c.customerName) = UPPER(:customerName)") 
	public boolean existsByCustomerName(@Param("customerName") String customerName);
	
		
	/**
	 * This method finds and returns customer object in the database by his/her name received as a parameter from the outside.
	 * 
	 * @param customerName customer name
	 * @return customer object
	 */
	@Query("SELECT c FROM CUSTOMERS c WHERE UPPER(c.customerName) = UPPER(:customerName)") 
	Customer findByName(@Param("customerName") String customerName);

	/**
	 * This method finds and returns customer object in the database by his/her ID received as a parameter from the outside.
	 * 
	 * @param id customer ID
	 * @return customer object
	 */
	@Query("SELECT c FROM CUSTOMERS c WHERE c.id = :id") 
	Customer findCustomerById(@Param("id") long id);
	
	/**
	 * This method finds and returns customer object in the database by his/her name and password
	 * received as parameters from the outside.
	 * 
	 * @param customerName customer name
	 * @param password password
	 * @return customer object
	 */
	@Query(value = "SELECT * FROM CUSTOMERS WHERE UPPER(CUSTOMER_NAME) = UPPER(:customerName) AND PASSWORD = :password", nativeQuery = true) 
	Customer findByNameAndPwd(@Param("customerName") String customerName, @Param("password") String password);


	/**
	 * This method deletes customer from the database. The customer is found by its ID received as a parameter from the outside.
	 * 
	 * @param id customer ID
	 *
	 */
	@Transactional
	@Modifying
	@Query("DELETE FROM CUSTOMERS c WHERE c.id = :id")
	void removeCustomerById(@Param("id") long id);
	
}

package com.jb.couponsystem.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.jb.couponsystem.entities.*;

/**
 * This interface contains query methods that refer to the company objects in the database.
 *  
 * @author Alexander Zablotsky
 *
 */
public interface CompanyRepo extends CrudRepository<Company, Long> {
	
	
	/**
	 * This method finds companies from the database by company name received as a parameter
	 * from the outside and returns them as list of objects.
	 * 
	 * @param companyName company name
	 * @return list of companies
	 */
	List<Company> findCompanyByCompanyNameIgnoreCase(String companyName);
	
	
	/**
	 * This method checks if a company with a given name exists in the database. 
	 * It returns true if it exists, false otherwise.
	 * 
	 * @param companyName company name
	 * @return true if a company with a given name exists, false otherwise 
	 */
	@Query("SELECT CASE WHEN COUNT(c) > 0 THEN 'true' ELSE 'false' END FROM COMPANIES c WHERE UPPER(c.companyName) = UPPER(:companyName)") 
	public boolean existsByCompanyName(@Param("companyName") String companyName);

	
	/**
	 * This method finds and returns company object in the database by its name received as a parameter from the outside.
	 * 
	 * @param companyName company name
	 * @return company object
	 */
	@Query(value = "SELECT * FROM COMPANIES WHERE UPPER(COMPANY_NAME) = UPPER(:companyName)", nativeQuery = true) 
	Company findByName(@Param("companyName") String companyName);

	/**
	 * This method finds and returns company object in the database by its name received as a parameter from the outside.
	 * 
	 * @param companyName company name
	 * @return company object
	 */
	@Query(value = "SELECT * FROM COMPANIES WHERE ID = :id", nativeQuery = true) 
	Company findCompanyById(@Param("id") long id);
	
	/**
	 * This method finds and returns company object in the database by its name and password received as parameters from the outside.
	 *  
	 * @param companyName company name
	 * @param password company's password
	 * @return company object
	 */
	@Query("SELECT c FROM COMPANIES c WHERE UPPER(c.companyName) = UPPER(:companyName) AND c.password = :password") 
	Company findByNameAndPwd(@Param("companyName") String companyName, @Param ("password") String password);

	/**
	 * This method deletes company from the database. The company is found by its ID received as a parameter from the outside.
	 * 
	 * @param id company ID
	 *
	 */
	@Transactional
	@Modifying
	@Query("DELETE FROM COMPANIES c WHERE c.id = :id")
	void removeCompanyById(@Param("id") long id);
}

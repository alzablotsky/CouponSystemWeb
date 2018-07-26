package com.jb.couponsystem.dbdao;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.jb.couponsystem.connection.ConnectionPool;
import com.jb.couponsystem.connection.DbConnection;
import com.jb.couponsystem.dao.CompanyDAO;
import com.jb.couponsystem.entities.Company;
import com.jb.couponsystem.exceptions.IllegalUpdateException;
import com.jb.couponsystem.exceptions.UserAlreadyExistsException;
import com.jb.couponsystem.exceptions.UserNotFoundException;
import com.jb.couponsystem.exceptions.WrongPasswordException;
import com.jb.couponsystem.repo.CompanyRepo;

/**
 * This class contains methods that call the instance of CompanyRepo interface in order to 
 * perform operations on company objects in the database.
 * Since the number of connections to the system is limited, each method begins with sending  
 * a request for connection from the pool of connections.
 * Also, at the end of each method the connection is returned back to the connection pool.  
 *   
 * @author Alexander Zablotsky
 *
 */
@Component
public class CompanyDBDAO implements CompanyDAO{

	//Attributes
	@Autowired
	CompanyRepo companyRepo;

	private Company loginCompany;

//Getters and setters - for loginCompany 
	/** 
	 * 
	 * Getter for the logged in company attribute
	 * 
	 * @return the loginCompany
	 */
	public Company getLoginCompany() {
		return loginCompany;
	}


	/**
	 * 
	 * Setter for the logged in company attribute
	 * 
	 * @param loginCompany the loginCompany to set
	 */
	public void setLoginCompany(Company loginCompany) {
		this.loginCompany = loginCompany;
	}

	//Methods

	/*
	 * Create company
	 * 
	 * (non-Javadoc)
	 * @see com.jb.couponsystem.dao.CompanyDAO#createCompany(com.jb.couponsystem.entities.Company)
	 */

	@Override
	public void createCompany(Company c) throws UserAlreadyExistsException, InterruptedException, SQLException {
		
		//Ask for connection from the connection pool
		Connection dbConnection = ConnectionPool.getInstance().getConnection();
		
		//If a company with this ID already exists - return connection and throw exception
		if (( companyRepo).existsById(c.getId())) {
			
			ConnectionPool.getInstance().returnConnection(dbConnection);
			
			throw new UserAlreadyExistsException ("Cannot create new company. Company id=" + c.getId() + " already exists.");
			
		}

		//If a company with this name already exists - - return connection and throw exception
		if (companyRepo.existsByCompanyName(c.getCompanyName())) {
			
			ConnectionPool.getInstance().returnConnection(dbConnection);
			
			throw new UserAlreadyExistsException ("Cannot create new company. Company name " + c.getCompanyName() + " already exists.");
			
		}
		// Otherwise - create company and return connection	
		else {
			
			
			
			companyRepo.save(c);
			
			ConnectionPool.getInstance().returnConnection(dbConnection);
		}
		
	}
	
	
	/*
	 * Remove company
	 * 
	 * (non-Javadoc)
	 * @see com.jb.couponsystem.dao.CompanyDAO#removeCompany(com.jb.couponsystem.entities.Company)
	 */
	@Override
	public void removeCompany(Company c) throws UserNotFoundException, InterruptedException, SQLException {
		
		//Ask for connection from the connection pool
		Connection dbConnection = ConnectionPool.getInstance().getConnection();
		
		//If a company with this ID does not exist - return connection and throw exception
		if (!companyRepo.existsById(c.getId())) {
			
			ConnectionPool.getInstance().returnConnection(dbConnection);
			
			throw new UserNotFoundException ("Cannot remove company. Company "+ 
					c + " does not exist.");
		}

		//Otherwise - remove company and return connection	
		else {
			
			companyRepo.removeCompanyById(c.getId());
			ConnectionPool.getInstance().returnConnection(dbConnection);
		}

	}


	/*
	 * Update company
	 * 
	 * (non-Javadoc)
	 * @see com.jb.couponsystem.dao.CompanyDAO#updateCompany(com.jb.couponsystem.entities.Company)
	 */
	@Override
	public void updateCompany(Company c) throws UserNotFoundException, IllegalUpdateException, InterruptedException, SQLException {
	
		//Ask for connection from the connection pool
		Connection dbConnection = ConnectionPool.getInstance().getConnection();
		
		//Compare to the company in DB
		Company companyInDb = companyRepo.findCompanyById(c.getId());

		//If a company with this ID does not exist in the DB - return connection and throw exception
		if (companyInDb == null)	{
			
			ConnectionPool.getInstance().returnConnection(dbConnection);
			
			throw new UserNotFoundException ("Cannot update company. Company "+ 
					c + " does not exist.");
		}
		
		//If the company's name is changed - return connection and throw exception
		else if (!companyInDb.getCompanyName().equals(c.getCompanyName())) {
			
			    ConnectionPool.getInstance().returnConnection(dbConnection);
			    
				throw new IllegalUpdateException ("Cannot update company "
					+  companyInDb.getCompanyName() 
					+  ". Company name cannot be changed."); 
		}

		//Otherwise - save the updated company and return connection
		else {
			companyRepo.save(c);
			
			ConnectionPool.getInstance().returnConnection(dbConnection);
		}
		
	}
		

/*
 * Get company by id
 * 
 * (non-Javadoc)
 * @see com.jb.couponsystem.dao.CompanyDAO#getCompany(long)
 */
	
@Cacheable(value = "company", key = "#id")	
@Override
public Company getCompany(long id) throws UserNotFoundException, InterruptedException, SQLException {

	//Ask for connection from the connection pool
	Connection dbConnection = ConnectionPool.getInstance().getConnection();
	
	Company c = companyRepo.findCompanyById(id);
	
	//If a company with this ID does not exist - return connection and throw exception
	if (!companyRepo.existsById(id))  {
		
		 ConnectionPool.getInstance().returnConnection(dbConnection);
		
		throw new UserNotFoundException ("Cannot display company details. "
				+ "Company id=" + id + " does not exist.");
	}
	//Otherwise - return connection and return the company
	else {
	
		ConnectionPool.getInstance().returnConnection(dbConnection);
		
		return c;
	}

}

/*
 * Get all companies
 * 
 * (non-Javadoc)
 * @see com.jb.couponsystem.dao.CompanyDAO#getAllCompanies()
 */
@Override
public Collection<Company> getAllCompanies() throws UserNotFoundException, InterruptedException, SQLException {

	//Ask for connection from the connection pool
	Connection dbConnection = ConnectionPool.getInstance().getConnection();
	
	Collection<Company> comp = (Collection<Company>) companyRepo.findAll();

	//If no companies exist - return connection and throw exception
	if (comp.isEmpty())  {

		ConnectionPool.getInstance().returnConnection(dbConnection);	
		
		throw new UserNotFoundException ("No companies were found.");
	}

	//Otherwise - return connection and return all the companies 
	else {

		ConnectionPool.getInstance().returnConnection(dbConnection);
		
		return comp;
	}
}

/*
 * Login as a company
 * 
 * (non-Javadoc)
 * @see com.jb.couponsystem.dao.CompanyDAO#login(java.lang.String, java.lang.String)
 */
@Override
public boolean login (String companyName, String password) 
		throws UserNotFoundException, WrongPasswordException, InterruptedException, SQLException{
	
	//Ask for connection from the connection pool
		Connection dbConnection = ConnectionPool.getInstance().getConnection();
	
	//If a company with this name does not exist - return connection and throw exception
	if (!companyRepo.existsByCompanyName(companyName)) {

		ConnectionPool.getInstance().returnConnection(dbConnection);
		
		throw new UserNotFoundException ("Login failed. Company name " + companyName + " does not exist.");	
	}

	//If the password does not fit the name	- return connection and throw exception
	else if (companyRepo.findByNameAndPwd(companyName, password)== null) {

		ConnectionPool.getInstance().returnConnection(dbConnection);
				
			throw new WrongPasswordException("Login failed. Wrong name + password: "
					+ companyName + ", " + password);
		}
		//Otherwise -  set the logged in company, return connection and return true
		else  {
			this.setLoginCompany(companyRepo.findByNameAndPwd(companyName, password));
			
			ConnectionPool.getInstance().returnConnection(dbConnection);
			
			return true;
		}
	}
}	








package com.jb.couponsystem;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.jb.couponsystem.dbdao.CompanyDBDAO;
import com.jb.couponsystem.dbdao.CouponDBDAO;
import com.jb.couponsystem.entities.Company;
import com.jb.couponsystem.entities.Coupon;
import com.jb.couponsystem.entities.Customer;
import com.jb.couponsystem.entry.CouponSystem;
import com.jb.couponsystem.enums.ClientType;
import com.jb.couponsystem.enums.CouponType;
import com.jb.couponsystem.exceptions.CouponAlreadyExistsException;
import com.jb.couponsystem.exceptions.CouponAlreadyPurchasedException;
import com.jb.couponsystem.exceptions.CouponExpiredException;
import com.jb.couponsystem.exceptions.CouponNotFoundException;
import com.jb.couponsystem.exceptions.CouponOutOfStockException;
import com.jb.couponsystem.exceptions.IllegalUpdateException;
import com.jb.couponsystem.exceptions.UserAlreadyExistsException;
import com.jb.couponsystem.exceptions.UserNotFoundException;
import com.jb.couponsystem.exceptions.WrongPasswordException;
import com.jb.couponsystem.facades.AdminFacade;
import com.jb.couponsystem.facades.CompanyFacade;
import com.jb.couponsystem.facades.CustomerFacade;
import com.jb.couponsystem.repo.CompanyRepo;
import com.jb.couponsystem.repo.CouponRepo;
import com.jb.couponsystem.repo.CustomerRepo;



/**
 * This class contains Spring Boot tests for the Coupon System application.
 * FixMethodOrder: All the tests are running according to the ascending name order.
 * RunWith: tests are run by SpringRunner class of the Spring Framework.
 * 
 * @author Alexander Zablotsky
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringRunner.class)
@SpringBootTest
public class Webproject2ApplicationTests {

	//Attributes	

		@Autowired
		ApplicationContext ctx;

		@Autowired
		CouponRepo couponRepo;

		@Autowired
		CompanyRepo companyRepo;

		@Autowired
		CustomerRepo customerRepo;

		@Autowired
		CouponDBDAO couponDBDAO;
		
		@Autowired
		CompanyDBDAO companyDBDAO;

		private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		//Tests

		/**
		 * Test for daily thread. Creates new expired coupon in the database.
		 * Then creates an instance of the CouponSystem class which performs the daily task 
		 * of removing the expired coupon from the database.
		 * 
		 * Note: this test should not run when other tests are running.
		 * In order to enable this test to run, add t.start()
		 * command in the CTOR of the CouponSystem.
		 * 
		 * 
		 * @throws CouponAlreadyExistsException if the coupon exists in the database
		 * @throws InterruptedException if the thread was interrupted 
		 * @throws SQLException 
		 */
		@Ignore
		@Test
		public void test_1_001_dailyThread() throws CouponAlreadyExistsException, InterruptedException, SQLException {
			Company c= new Company();
			c.setCompanyName("Empty company");
			companyDBDAO.createCompany(c);
			
			Coupon coupon = new Coupon();
			coupon.setTitle("Empty coupon");
			coupon.setEndDate("2017-01-01");
			coupon.setCompany(c);

			couponDBDAO.createCoupon(coupon);
			Assert.assertTrue(couponRepo.existsByTitle("Empty coupon"));

			CouponSystem couponsystem = new CouponSystem(ctx);

			try {
				Thread.sleep(10000);
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}

			Assert.assertFalse(couponRepo.existsByTitle("Empty coupon"));


		}


		/**
		 * Test whether the Context loads.
		 */
		@Test
		public void test_1_002_contextLoads() {
		}
		
		
		//1. Test Admin Facade methods
		// login as admin
		/**
		 * Test for login method of AdminFacade.
		 * When the username and password are correct, the method returns the instance of AdminFacade.
		 */
		@Test
		public void test_2_001_adminLogin() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);

			Assert.assertNotNull(adminFacade);
		}

		/**
		 * Test for login method of AdminFacade.
		 * When the username is incorrect, the method throws 
		 * UserNotFoundException and returns null.
		 */
		@Test (expected = UserNotFoundException.class)
		public void test_2_002_adminLogin() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			AdminFacade adminFacade1 = (AdminFacade) couponsystem.login("admin1", "1234", ClientType.ADMIN);

			Assert.assertNull(adminFacade1);
		}

		//create company
		/**
		 * Test for createCompany method of AdminFacade.
		 * If the company name and ID were not previously used,
		 * the method creates the company in the database. 
		 */
		@Test
		public void test_2_003_adminCreateCompany() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
			Company comp= new Company("Teva", "123", "teva@gmail.com");
			adminFacade.createCompany(comp);

			Assert.assertTrue(companyRepo.existsByCompanyName("Teva"));
		}


		/**
		 * Test for createCompany method of AdminFacade.
		 * If the company name and ID were not previously used,
		 * the method creates the company in the database. 
		 */
		@Test
		public void test_2_004_adminCreateCompany() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
			Company comp= new Company("Google", "234", "google@gmail.com");
			adminFacade.createCompany(comp);

			Assert.assertTrue(companyRepo.existsByCompanyName("Google"));
		}


		/**
		 * Test for createCompany method of AdminFacade.
		 * If the company name and ID were not previously used,
		 * the method creates the company in the database. 
		 */
		@Test
		public void test_2_005_adminCreateCompany() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
			Company comp= new Company("Amdocs", "345", "amdocs@gmail.com");
			adminFacade.createCompany(comp);

			Assert.assertTrue(companyRepo.existsByCompanyName("Amdocs"));
		}

		/**
		 * Test for createCompany method of AdminFacade.
		 * If the company name already exists in the database,
		 * UserAlreadyExistsException is thrown and the company is not created. 
		 */

		@Test (expected = UserAlreadyExistsException.class)
		public void test_2_006_adminCreateCompany() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
			Company comp= new Company("Teva", "234", "teva@gmail.com");
			adminFacade.createCompany(comp);

			//Cannot create two companies with the same name
			Assert.assertFalse(companyRepo.findCompanyByCompanyNameIgnoreCase("TEVA").size()>1);
		}

		/**
		 * Test for createCompany method of AdminFacade.
		 * If the company ID already exists in the database,
		 * UserAlreadyExistsException is thrown and the company is not created. 
		 */
		@Test (expected = UserAlreadyExistsException.class)
		public void test_2_007_adminCreateCompany() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
			Company comp= new Company("Sonol", "444", "sonol@gmail.com");
			comp.setId(1);
			adminFacade.createCompany(comp);

			//Cannot create a company with id used by another company
			Assert.assertFalse(companyRepo.existsByCompanyName("Sonol"));

		}

		/**
		 * Test for createCompany method of AdminFacade.
		 * If the company name and ID were not previously used,
		 * the method creates the company in the database. 
		 */
		@Test
		public void test_2_008_adminCreateCompany() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
			Company comp= new Company("Sonol", "444", "sonol@gmail.com");
			adminFacade.createCompany(comp);

			Assert.assertTrue(companyRepo.existsByCompanyName("Sonol"));
		}
		
		
		/**
		 * Test for createCompany method of AdminFacade.
		 * If the company name and ID were not previously used,
		 * the method creates the company in the database. 
		 */
		@Test
		public void test_2_009_adminCreateCompany() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
			Company comp= new Company("Arkia", "888", "arkia@gmail.com");
			adminFacade.createCompany(comp);

			Assert.assertTrue(companyRepo.existsByCompanyName("Arkia"));
		}

		//remove company
		/**
		 * Test for removeCompany method of AdminFacade.
		 * If the company details are correct,
		 * the method removes the company from the database. 
		 */
		@Test
		public void test_2_010_adminRemoveCompany() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
			Company comp= companyRepo.findByName("Google");
			
			adminFacade.removeCompany(comp);

			Assert.assertFalse(companyRepo.existsByCompanyName("Google"));

		}

		/**
		 * Test for removeCompany method of AdminFacade.
		 * If the company does not exist in the database,
		 * the method throws UserNotFoundException and
		 * does not make changes in the database.  
		 */

		@Test (expected = UserNotFoundException.class)
		public void test_2_011_adminRemoveCompany() throws CouponNotFoundException, InterruptedException, SQLException {

			CouponSystem couponsystem = new CouponSystem(ctx);
			AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
			Company comp= new Company();
			comp.setCompanyName("Hashmal");

			//cannot remove company that does not exist in DB
			long countBefore = companyRepo.count();
			
			adminFacade.removeCompany(comp);

			long countAfter = companyRepo.count();
			Assert.assertEquals(countBefore, countAfter);

		}


		//update company
		/**
		 * Test for updateCompany method of AdminFacade.
		 * If the company details are correct,
		 * and no attempt of illegal update is made, 
		 * the method updates the company in the database. 
		 */
		@Test
		public void test_2_012_adminUpdateCompany() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
			Company comp= companyRepo.findByName("Amdocs"); 
			comp.setPassword("456");
			adminFacade.updateCompany(comp);

			String updated= companyRepo.findByName("Amdocs").getPassword();
			Assert.assertEquals("456", updated);

		}	

		/**
		 * Test for updateCompany method of AdminFacade.
		 * If the company details are correct,
		 * and no attempt of illegal update is made, 
		 * the method updates the company in the database. 
		 */
		@Test
		public void test_2_013_adminUpdateCompany() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
			Company comp= companyRepo.findByName("Amdocs"); 
			comp.setEmail("amdocs1@gmail.com");
			adminFacade.updateCompany(comp);

			String updated= companyRepo.findByName("Amdocs").getEmail();
			Assert.assertEquals("amdocs1@gmail.com", updated);
		}	

		/**
		 * Test for updateCompany method of AdminFacade.
		 * If there is an attempt to change the company name,
		 * the method throws IllegalUpdateException and does not update the company. 
		 */
		@Test (expected =  IllegalUpdateException.class)
		public void test_2_014_adminUpdateCompany() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
			Company comp= companyRepo.findByName("Amdocs"); 
			comp.setCompanyName("Amdocs1");
			adminFacade.updateCompany(comp);

			//Cannot update company name
			Assert.assertNull(companyRepo.findByName("Amdocs1"));
		}	

		/**
		 * Test for updateCompany method of AdminFacade.
		 * If the company does not exist in the database
		 * the method throws UserNotFoundException. 
		 */
		@Test  (expected = UserNotFoundException.class)
		public void test_2_015_adminUpdateCompany() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
			Company comp= new Company();
			comp.setCompanyName("Hashmal");
			comp.setPassword("123");
			adminFacade.updateCompany(comp);

			//Cannot update company that does not exist in DB
			Assert.assertNull(companyRepo.findByName("Hashmal"));
		}



		//get company by id

		/**
		 * Test for getCompany method of AdminFacade.
		 * If the company id exists in the database
		 * the method displays the company object 
		 * from the database with the same id. 
		 */
		@Test
		public void test_2_016_adminGetCompany() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
			Company comp= adminFacade.getCompany(1);
			System.out.println("Company id 1: "+ comp);

			Assert.assertNotNull(comp);
			Assert.assertEquals(1, comp.getId());
		}

		/**
		 * Test for getCompany method of AdminFacade.
		 * If the company id does not exist in the database
		 * the method throws UserNotFoundException and no company is displayed.
		 */
		@Test (expected = UserNotFoundException.class)
		public void test_2_017_adminGetCompany() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
			Company comp= adminFacade.getCompany(6);
			System.out.println("Company id 6: "+ comp);

			Assert.assertNull(comp);

		}

		//get all companies
		/**
		 * Test for getAllCompanies method of AdminFacade.
		 * If there are any companies in the database
		 * the method displays all these companies.
		 */
		@Test
		public void test_2_018_adminGetAllCompanies() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
			Collection <Company> comps= adminFacade.getAllCompanies();
			System.out.println("Companies list: \n"+ comps);

			Assert.assertNotNull(comps);
			Assert.assertEquals(companyRepo.count(), comps.size());

		}


		//create customer
		/**
		 * Test for createCustomer method of AdminFacade.
		 * If the customer name and ID were not previously used,
		 * the method creates the customer in the database. 
		 */
		@Test
		public void test_2_019_adminCreateCustomer() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
			Customer cust= new Customer("Avi", "111");
			adminFacade.createCustomer(cust);

			Assert.assertTrue(customerRepo.existsByCustomerName("Avi"));
		}

		/**
		 * Test for createCustomer method of AdminFacade.
		 * If the customer name and ID were not previously used,
		 * the method creates the customer in the database. 
		 */
		@Test
		public void test_2_020_adminCreateCustomer() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
			Customer cust= new Customer("Benny", "222");
			adminFacade.createCustomer(cust);

			Assert.assertTrue(customerRepo.existsByCustomerName("Benny"));
		}

		/**
		 * Test for createCustomer method of AdminFacade.
		 * If the customer name and ID were not previously used,
		 * the method creates the customer in the database. 
		 */
		@Test
		public void test_2_021_adminCreateCustomer() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
			Customer cust= new Customer("Gabi", "333");
			adminFacade.createCustomer(cust);

			Assert.assertTrue(customerRepo.existsByCustomerName("Gabi"));
		}

		/**
		 * Test for createCustomer method of AdminFacade.
		 * If the customer name exists in the database,
		 * the method throws UserAlreadyExistsException and the customer is not created. 
		 */
		@Test (expected = UserAlreadyExistsException.class)
		public void test_2_022_adminCreateCustomer() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
			Customer cust= new Customer("Gabi", "444");
			adminFacade.createCustomer(cust);

			//Cannot create two customers with the same name
			Assert.assertFalse(customerRepo.findCustomerByCustomerNameIgnoreCase("Gabi").size()>1);
		}

		/**
		 * Test for createCustomer method of AdminFacade.
		 * If the customer name and ID were not previously used,
		 * the method creates the customer in the database. 
		 */
		@Test
		public void test_2_023_adminCreateCustomer() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
			Customer cust= new Customer("Dudi", "555");
			adminFacade.createCustomer(cust);

			Assert.assertTrue(customerRepo.existsByCustomerName("Dudi"));
		}

		/**
		 * Test for createCustomer method of AdminFacade.
		 * If the customer name and ID were not previously used,
		 * the method creates the customer in the database. 
		 */
		@Test
		public void test_2_024_adminCreateCustomer() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
			Customer cust= new Customer("Harel", "666");
			adminFacade.createCustomer(cust);

			Assert.assertTrue(customerRepo.existsByCustomerName("Harel"));
		}



		//remove customer
		/**
		 * Test for removeCustomer method of AdminFacade.
		 * If the customer details are correct,
		 * the method removes the customer from the database. 
		 */
		@Test
		public void test_2_025_adminRemoveCustomer() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
			Customer cust= customerRepo.findByName("Benny"); 

			adminFacade.removeCustomer(cust);

			Assert.assertFalse(customerRepo.existsByCustomerName("Benny"));
		}			

		/**
		 * Test for removeCustomer method of AdminFacade.
		 * If the customer does not exist in the database,
		 * the method throws UserNotFoundException and 
		 * does not make changes in the database. 
		 */
		@Test (expected = UserNotFoundException.class)
		public void test_2_026_adminRemoveCustomer() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
			Customer cust=  new Customer ();
			cust.setCustomerName("Harel");

			//cannot remove customer that does not exist
			long countBefore = customerRepo.count();
			adminFacade.removeCustomer(cust);

			long countAfter = customerRepo.count();
			Assert.assertEquals(countBefore, countAfter);
		}

		//update customer
		/**
		 * Test for updateCustomer method of AdminFacade.
		 * If the customer details are correct,
		 * and no attempt of illegal update is made, 
		 * the method updates the customer in the database. 
		 */
		@Test
		public void test_2_027_adminUpdateCustomer() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
			Customer cust= customerRepo.findByName("Gabi");
			cust.setPassword("444");
			adminFacade.updateCustomer(cust);

			Assert.assertEquals("444", customerRepo.findByName("Gabi").getPassword());


		}	

		/**
		 * Test for updateCustomer method of AdminFacade.
		 * If there is an attempt to change customer name, 
		 * the method throws IllegalUpdateException,
		 * and the customer is not updated.
		 */
		@Test (expected = IllegalUpdateException.class)
		public void test_2_028_adminUpdateCustomer() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
			Customer cust= customerRepo.findByName("Gabi");
			cust.setCustomerName("Gabriel");
			adminFacade.updateCustomer(cust);

			//cannot update customer name
			Assert.assertNull(customerRepo.findByName("Gabriel"));
		}

		/**
		 * Test for updateCustomer method of AdminFacade.
		 * If the customer with the given name does not exist in the database, 
		 * the method throws UserNotFoundException,
		 * and the customer is not updated.
		 */
		@Test (expected = UserNotFoundException.class)
		public void test_2_029_adminUpdateCustomer() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
			Customer cust= new Customer();
			cust.setCustomerName("Daniel");
			adminFacade.updateCustomer(cust);

			//cannot update customer that does not exist in the database
			Assert.assertNull(customerRepo.findByName("Daniel"));
		}

		//get customer by id
		/**
		 * Test for getCustomer method of AdminFacade.
		 * If the customer id exists in the database,
		 * the method displays the customer object 
		 * from the database with the same id. 
		 */
		@Test
		public void test_2_030_adminGetCustomer() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
			Customer cust= adminFacade.getCustomer(1);
			System.out.println("Customer id 1: "+ cust);

			Assert.assertNotNull(cust);
			Assert.assertEquals(1, cust.getId());
		}

		/**
		 * Test for getCustomer method of AdminFacade.
		 * If the customer id does not exist in the database,
		 * the method throws UserNotFoundException and no customer is displayed.
		 */
		@Test (expected = UserNotFoundException.class)
		public void test_2_031_adminGetCustomer() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
			Customer cust= adminFacade.getCustomer(6);
			System.out.println("Customer id 6: "+ cust);

			Assert.assertNull(cust);
		}

		//get all customers
		/**
		 * Test for getAllCustomers method of AdminFacade.
		 * If there are any customers in the database
		 * the method displays all these customers.
		 */
		@Test
		public void test_2_032_adminGetAllCustomers() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
			Collection <Customer> custs= adminFacade.getAllCustomers();
			System.out.println("Customers list: \n"+ custs);

			Assert.assertNotNull(custs);
			Assert.assertEquals(customerRepo.count(), custs.size());

		}


		//2. Test Company Facade methods

		// login as a company
		/**
		 * Test for login method of CompanyFacade class.
		 * When the username and password are correct, the method returns
		 * the instance of CompanyFacade.
		 */
		@Test
		public void test_3_001_companyLogin() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			CompanyFacade teva = (CompanyFacade) couponsystem.login("Teva", "123", ClientType.COMPANY);

			Assert.assertNotNull(teva);
		}

		/**
		 * Test for login method of CompanyFacade class.
		 * When the username is incorrect, the method throws 
		 * UserNotFoundException and returns null.
		 */
		@Test 
		(expected= UserNotFoundException.class )
		public void test_3_002_companyLogin() { 

			CouponSystem couponsystem = new CouponSystem(ctx);
			CompanyFacade teva1 = (CompanyFacade) couponsystem.login("Teva1", "123", ClientType.COMPANY);
			Assert.assertNull(teva1);
		}

		/**
		 * Test for login method of CompanyFacade class.
		 * When the password is incorrect, the method throws 
		 * WrongPasswordException and returns null.
		 */
		@Test 
	    (expected= WrongPasswordException.class )
		public void test_3_003_companyLogin() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			CompanyFacade teva2 = (CompanyFacade) couponsystem.login("Teva", "1234", ClientType.COMPANY);
			Assert.assertNull(teva2);
		}

		/**
		 * Test for login method of CompanyFacade class.
		 * When the username and password are correct, the method returns
		 * the instance of CompanyFacade.
		 */
		@Test
		public void test_3_004_companyGetLoginCompanyName() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			CompanyFacade teva = (CompanyFacade) couponsystem.login("Teva", "123", ClientType.COMPANY);

			String name = teva.getLoginCompanyName();
			String nameInDb = companyDBDAO.getLoginCompany().getCompanyName();
			Assert.assertEquals(name, nameInDb );
		}

		// create coupon
		/**
		 * Test for createCoupon method of CompanyFacade.
		 * If the coupon name and ID were not previously used,
		 * the method creates the coupon in the database. 
		 *
		 * @throws CouponAlreadyExistsException if the coupon exists in the database
		 * @throws SQLException 
		 * @throws IOException 
		 */
		@Test
		public void test_3_005_companyCreateCoupon() throws CouponAlreadyExistsException, IOException, SQLException 	{

			CouponSystem couponsystem = new CouponSystem(ctx);
			CompanyFacade teva = (CompanyFacade) couponsystem.login("Teva", "123", ClientType.COMPANY);

			Coupon coupon = new Coupon();
			coupon.setTitle("Free camping");
			coupon.setMessage("You can camp for free...");
			coupon.setAmount(2);
			coupon.setType(CouponType.CAMPING);
			coupon.setPrice(200);
			coupon.setStartDate("2017-06-01");
			coupon.setEndDate("2019-06-01");
			
					
			//coupon.setImageFromFile("C:/temp/coupon1.jpg"); //use for image in PC C:/temp folder
			//coupon.setImageFromUrl("http://localhost:8080/images/coupon1.jpg"); //use for image folder when the application is up
			
			coupon.setImageFromFile("images/coupon1.jpg"); //local images folder
			
			teva.createCoupon(coupon);

			Assert.assertTrue(couponRepo.existsByTitle("Free camping"));
		}

		

		/**
		 * Test for createCoupon method of CompanyFacade.
		 * If the coupon name and ID were not previously used,
		 * the method creates the coupon in the database.
		 *
		 * @throws CouponAlreadyExistsException if the coupon exists in the database
		 * @throws IOException 
		 * @throws FileNotFoundException 
		 */
		@Test
		public void test_3_007_companyCreateCoupon() throws CouponAlreadyExistsException, IOException 	{

			CouponSystem couponsystem = new CouponSystem(ctx);
			CompanyFacade teva = (CompanyFacade) couponsystem.login("Teva", "123", ClientType.COMPANY);

			Coupon coupon = new Coupon();
			coupon.setTitle("Dinner for two");
			coupon.setMessage("You can have a dinner...");
			coupon.setAmount(20);
			coupon.setType(CouponType.RESTAURANTS);
			coupon.setPrice(100);
			coupon.setStartDate("aaa");
			coupon.setStartDate("2017-01-01");
			coupon.setEndDate("2018-02-01");
			
			//coupon.setImageFromFile("C:/temp/coupon2.jpg"); //use for image in PC C:/temp folder
			//coupon.setImageFromUrl("http://localhost:8080/images/coupon2.jpg");  //use for image folder when the application is up
			
			coupon.setImageFromFile("images/coupon2.jpg"); //local images folder
			
			teva.createCoupon(coupon);

			Assert.assertTrue(couponRepo.existsByTitle("Dinner for two"));
		}

		/**
		 * Test for createCoupon method of CompanyFacade.
		 * If the coupon name with the same name exists in the database,
		 * the method throws CouponAlreadyExistsException,
		 * and no coupon is created.
		 *
		 * @throws CouponAlreadyExistsException if the coupon exists in the database 
		 * @throws IOException 
		 */
		@Test (expected = CouponAlreadyExistsException.class)
		public void test_3_008_companyCreateCoupon() throws CouponAlreadyExistsException, IOException 	{

			CouponSystem couponsystem = new CouponSystem(ctx);
			CompanyFacade teva = (CompanyFacade) couponsystem.login("Teva", "123", ClientType.COMPANY);

			Coupon coupon = new Coupon();
			coupon.setTitle("Free camping");
			coupon.setMessage("You can camp for free...");
			coupon.setAmount(20);
			coupon.setType(CouponType.TRAVELLING);
			coupon.setPrice(200);
			coupon.setStartDate("2018-06-01");
			coupon.setEndDate("2019-06-01");
			
			//coupon.setImageFromFile("C:/temp/coupon1.jpg"); //use for image in PC C:/temp folder
			//coupon.setImageFromUrl("http://localhost:8080/images/coupon1.jpg"); //use for image folder when the application is up

			coupon.setImageFromFile("images/coupon1.jpg"); //local images folder
			
			teva.createCoupon(coupon);

			//Cannot create 2 coupons with the same name
			Assert.assertFalse(couponRepo.findCouponByTitleIgnoreCase("Free camping").size()>1);
		}

		/**
		 * Test for createCoupon method of CompanyFacade.
		 * If the coupon name and ID were not previously used,
		 * the method creates the coupon in the database.
		 *
		 * @throws CouponAlreadyExistsException if the coupon exists in the database 
		 * @throws IOException 
		 */
		@Test
		public void test_3_009_companyCreateCoupon() throws CouponAlreadyExistsException, IOException 	{

			CouponSystem couponsystem = new CouponSystem(ctx);
			CompanyFacade amdocs = (CompanyFacade) couponsystem.login("Amdocs", "456", ClientType.COMPANY);

			Coupon coupon = new Coupon();
			coupon.setTitle("Flight to Ibiza");
			coupon.setMessage("You can fly to Ibiza...");
			coupon.setAmount(30);
			coupon.setType(CouponType.TRAVELLING);
			coupon.setPrice(150);
			coupon.setStartDate("2017-10-01");
			coupon.setEndDate("2019-10-01");
			
			//coupon.setImageFromFile("C:/temp/coupon3.jpg"); // use for image in PC C:/temp folder
			//coupon.setImageFromUrl("http://localhost:8080/images/coupon3.jpg"); //use for image folder when the application is up
			
			coupon.setImageFromFile("images/coupon3.jpg"); //local images folder
			
			amdocs.createCoupon(coupon);

			Assert.assertTrue(couponRepo.existsByTitle("Flight to Ibiza"));

		}


		/**
		 * Test for createCoupon method of CompanyFacade.
		 * If the coupon name and ID were not previously used,
		 * the method creates the coupon in the database.
		 *
		 * @throws CouponAlreadyExistsException if the coupon exists in the database 
		 * @throws IOException 
		 */
		@Test
		public void test_3_010_companyCreateCoupon() throws CouponAlreadyExistsException, IOException 	{

			CouponSystem couponsystem = new CouponSystem(ctx);
			CompanyFacade amdocs = (CompanyFacade) couponsystem.login("Amdocs", "456", ClientType.COMPANY);

			Coupon coupon = new Coupon();
			coupon.setTitle("Gym membership");
			coupon.setMessage("You can go to a gym...");
			coupon.setAmount(30);
			coupon.setType(CouponType.SPORTS);
			coupon.setPrice(150);
			coupon.setStartDate("2017-05-01");
			coupon.setEndDate("2019-05-01");
			
			//coupon.setImageFromFile("C:/temp/coupon5.jpg"); // use for image in PC C:/temp folder
			//coupon.setImageFromUrl("http://localhost:8080/images/coupon5.jpg"); //use for image folder when the application is up

			coupon.setImageFromFile("images/coupon5.jpg"); //local images folder
			
			amdocs.createCoupon(coupon);

			Assert.assertTrue(couponRepo.existsByTitle("Gym membership"));

		}
		
		/**
		 * Test for createCoupon method of CompanyFacade.
		 * If the coupon name and ID were not previously used,
		 * the method creates the coupon in the database.
		 *
		 * @throws CouponAlreadyExistsException if the coupon exists in the database 
		 * @throws IOException 
		 */
		@Test
		public void test_3_011_companyCreateCoupon() throws CouponAlreadyExistsException, IOException 	{

			CouponSystem couponsystem = new CouponSystem(ctx);
			CompanyFacade amdocs = (CompanyFacade) couponsystem.login("Amdocs", "456", ClientType.COMPANY);

			Coupon coupon = new Coupon();
			coupon.setTitle("Downhill skiing");
			coupon.setMessage("You can do skiing...");
			coupon.setAmount(1);
			coupon.setType(CouponType.SPORTS);
			coupon.setPrice(550);
			coupon.setStartDate("2017-06-01");
			coupon.setEndDate("2019-06-01");
			
			//coupon.setImageFromFile("C:/temp/coupon5.jpg"); // use for image in PC C:/temp folder
			//coupon.setImageFromUrl("http://localhost:8080/images/coupon5.jpg"); //use for image folder when the application is up

			coupon.setImageFromFile("images/coupon5.jpg"); //local images folder

			amdocs.createCoupon(coupon);

			Assert.assertTrue(couponRepo.existsByTitle("Downhill skiing"));

		}

		/**
		 * Test for createCoupon method of CompanyFacade.
		 * If the coupon name and ID were not previously used,
		 * the method creates the coupon in the database.
		 *
		 * @throws CouponAlreadyExistsException if the coupon exists in the database 
		 * @throws IOException 
		 */
		@Test
		public void test_3_012_companyCreateCoupon() throws CouponAlreadyExistsException, IOException 	{

			CouponSystem couponsystem = new CouponSystem(ctx);
			CompanyFacade amdocs = (CompanyFacade) couponsystem.login("Amdocs", "456", ClientType.COMPANY);

			Coupon coupon = new Coupon();
			coupon.setTitle("Shopping in the mall");
			coupon.setMessage("You can go shopping...");
			coupon.setAmount(30);
			coupon.setType(CouponType.FOOD);
			coupon.setPrice(500);
			coupon.setStartDate("2017-09-01");
			coupon.setEndDate("2019-09-01");
			
			//coupon.setImageFromFile("C:/temp/coupon4.jpg"); // use for image in PC C:/temp folder
			//coupon.setImageFromUrl("http://localhost:8080/images/coupon4.jpg"); //use for image folder when the application is up

			coupon.setImageFromFile("images/coupon4.jpg"); //local images folder

			amdocs.createCoupon(coupon);

			Assert.assertTrue(couponRepo.existsByTitle("Shopping in the mall"));

		}
		
		
		/**
		 * Test for createCoupon method of CompanyFacade.
		 * If the coupon name and ID were not previously used,
		 * the method creates the coupon in the database.
		 *
		 * @throws CouponAlreadyExistsException if the coupon exists in the database 
		 * @throws IOException 
		 */
		@Test
		public void test_3_013_companyCreateCoupon() throws CouponAlreadyExistsException, IOException 	{

			CouponSystem couponsystem = new CouponSystem(ctx);
			CompanyFacade arkia = (CompanyFacade) couponsystem.login("Arkia", "888", ClientType.COMPANY);

			Coupon coupon = new Coupon();
			coupon.setTitle("Flight to Eilat");
			coupon.setMessage("You can fly to Eilat...");
			coupon.setAmount(30);
			coupon.setType(CouponType.TRAVELLING);
			coupon.setPrice(150);
			coupon.setStartDate("2017-11-01");
			coupon.setEndDate("2019-11-01");
			
			//coupon.setImageFromFile("C:/temp/coupon5.jpg"); // use for image in PC C:/temp folder
			//coupon.setImageFromUrl("http://localhost:8080/images/coupon5.jpg"); //use for image folder when the application is up

			coupon.setImageFromFile("images/coupon5.jpg"); //local images folder
			
			arkia.createCoupon(coupon);

			Assert.assertTrue(couponRepo.existsByTitle("Flight to Eilat"));

		}

		//Remove coupon
		/**
		 * Test for removeCoupon method of CompanyFacade.
		 * If the coupon details are correct,
		 * the method removes the company's coupon from the database. 
		 */
		@Test
		public void test_3_014_companyRemoveCoupon() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			Coupon coupon = couponRepo.findByTitle("Gym membership");
			CompanyFacade amdocs = (CompanyFacade) couponsystem.login("Amdocs", "456", ClientType.COMPANY);
			amdocs.removeCoupon(coupon);

			Assert.assertFalse(couponRepo.existsByTitle("Gym membership"));

		}

		/**
		 * Test for removeCoupon method of CompanyFacade.
		 * If the coupon belongs to another company,
		 * the method throws CouponNotFoundException,
		 * and the coupon is not removed from the database. 
		 */
		@Test (expected = CouponNotFoundException.class )
		public void test_3_015_companyRemoveCoupon() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			Coupon coupon = couponRepo.findByTitle("Free camping");
			CompanyFacade amdocs = (CompanyFacade) couponsystem.login("Amdocs", "456", ClientType.COMPANY);
			amdocs.removeCoupon(coupon);

			//Cannot remove coupon of another company
			Assert.assertTrue(couponRepo.existsByTitle("Free camping"));

		}

		//Update coupon

		//can update price and end date only

		/**
		 * Test for updateCoupon method of CompanyFacade.
		 * Only the price and the end date of the coupon can be updated.
		 * If the coupon details are correct,
		 * and the coupon price is updated,  
		 * the method updates the coupon in the database.
		 */

		@Test
		public void test_3_016_companyUpdateCoupon() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			Coupon coupon = couponRepo.findByTitle("Shopping in the mall");
			coupon.setPrice(550);
					
			CompanyFacade amdocs = (CompanyFacade) couponsystem.login("Amdocs", "456", ClientType.COMPANY);
			amdocs.updateCoupon(coupon);

			//can update price
			double updatePrice = couponRepo.findByTitle("Shopping in the mall").getPrice();
			Assert.assertEquals(coupon.getPrice(), updatePrice, 0.01);
		}

		/**
		 * Test for updateCoupon method of CompanyFacade.
		 * Only the price and the end date of the coupon can be updated.
		 * If the coupon details are correct,
		 * and the coupon price is updated,  
		 * the method updates the coupon in the database.
		 */
		@Test
		public void test_3_017_companyUpdateCoupon() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			Coupon coupon = couponRepo.findByTitle("Shopping in the mall");
			coupon.setEndDate("2019-10-01");
			CompanyFacade amdocs = (CompanyFacade) couponsystem.login("Amdocs", "456", ClientType.COMPANY);
			amdocs.updateCoupon(coupon);

			//can update end date
			Date updateEndDate = couponRepo.findByTitle("Shopping in the mall").getEndDate();
			Assert.assertEquals(coupon.getEndDate(), updateEndDate);
		}


		/**
		 * Test for updateCoupon method of CompanyFacade.
		 * If the attempt to update the coupon ID is made,
		 * the method throws IllegalUpdateException,
		 * and the coupon is not updated.
		 */
		@Test (expected = IllegalUpdateException.class)
		public void test_3_018_companyUpdateCoupon() {
			CouponSystem couponsystem = new CouponSystem(ctx);
			Coupon coupon = couponRepo.findByTitle("Shopping in the mall");
			coupon.setId(10);
			CompanyFacade amdocs = (CompanyFacade) couponsystem.login("Amdocs", "456", ClientType.COMPANY);
			amdocs.updateCoupon(coupon);

			//Cannot update id
			long updateId = couponRepo.findByTitle("Shopping in the mall").getId();
			Assert.assertNotSame(coupon.getId(), updateId);
		}

		/**
		 * Test for updateCoupon method of CompanyFacade.
		 * If the attempt to update the coupon start date is made,
		 * the method throws IllegalUpdateException,
		 * and the coupon is not updated.
		 */
		@Test (expected = IllegalUpdateException.class)
		public void test_3_019_companyUpdateCoupon() {
			CouponSystem couponsystem = new CouponSystem(ctx);
			Coupon coupon = couponRepo.findByTitle("Shopping in the mall");
			coupon.setStartDate("2019-01-01");
			CompanyFacade amdocs = (CompanyFacade) couponsystem.login("Amdocs", "456", ClientType.COMPANY);
			amdocs.updateCoupon(coupon);

			//Cannot update Start Date
			Date updateStartDate = couponRepo.findByTitle("Shopping in the mall").getStartDate();
			Assert.assertNotEquals(coupon.getStartDate(), updateStartDate);
		}

		/**
		 * Test for updateCoupon method of CompanyFacade.
		 * If the attempt to update the coupon amount is made,
		 * the method throws IllegalUpdateException,
		 * and the coupon is not updated.
		 */
		@Test (expected = IllegalUpdateException.class)
		public void test_3_020_companyUpdateCoupon() {
			CouponSystem couponsystem = new CouponSystem(ctx);
			Coupon coupon = couponRepo.findByTitle("Shopping in the mall");
			coupon.setAmount(50);
			CompanyFacade amdocs = (CompanyFacade) couponsystem.login("Amdocs", "456", ClientType.COMPANY);
			amdocs.updateCoupon(coupon);

			//Cannot update amount
			int updateAmount = couponRepo.findByTitle("Shopping in the mall").getAmount();
			Assert.assertNotSame(coupon.getAmount(), updateAmount);
		}


		/**
		 * Test for updateCoupon method of CompanyFacade.
		 * If the attempt to update the coupon type is made,
		 * the method throws IllegalUpdateException,
		 * and the coupon is not updated.
		 */
		@Test (expected = IllegalUpdateException.class)
		public void test_3_021_companyUpdateCoupon() {
			CouponSystem couponsystem = new CouponSystem(ctx);
			Coupon coupon = couponRepo.findByTitle("Shopping in the mall");
			coupon.setType(CouponType.ELECTRICITY);
			CompanyFacade amdocs = (CompanyFacade) couponsystem.login("Amdocs", "456", ClientType.COMPANY);
			amdocs.updateCoupon(coupon);

			//Cannot update type
			CouponType updateType = couponRepo.findByTitle("Shopping in the mall").getType();
			Assert.assertNotEquals(coupon.getType(), updateType);
		}



		/**
		 * Test for updateCoupon method of CompanyFacade.
		 * If the attempt to update the coupon message is made,
		 * the method throws IllegalUpdateException,
		 * and the coupon is not updated.
		 */
		@Test (expected = IllegalUpdateException.class)
		public void test_3_022_companyUpdateCoupon() {
			CouponSystem couponsystem = new CouponSystem(ctx);
			Coupon coupon = couponRepo.findByTitle("Shopping in the mall");
			coupon.setMessage("Hello");
			CompanyFacade amdocs = (CompanyFacade) couponsystem.login("Amdocs", "456", ClientType.COMPANY);
			amdocs.updateCoupon(coupon);

			//Cannot update message
			String updateMessage = couponRepo.findByTitle("Shopping in the mall").getMessage();
			Assert.assertNotEquals(coupon.getMessage(), updateMessage);
		}

		/**
		 * Test for updateCoupon method of CompanyFacade.
		 * If the attempt to update the coupon image is made,
		 * the method throws IllegalUpdateException,
		 * and the coupon is not updated.
		 * @throws IOException 
		 */
		@Test (expected = IllegalUpdateException.class)
		public void test_3_023_companyUpdateCoupon() throws IOException {
			CouponSystem couponsystem = new CouponSystem(ctx);
			Coupon coupon = couponRepo.findByTitle("Shopping in the mall");
			
			//coupon.setImageFromFile("C:/temp/coupon1.jpg"); // use for image in PC C:/temp folder
			//coupon.setImageFromUrl("http://localhost:8080/images/coupon1.jpg"); //use for image folder when the application is up

			coupon.setImageFromFile("images/coupon1.jpg"); //local images folder
					
			CompanyFacade amdocs = (CompanyFacade) couponsystem.login("Amdocs", "456", ClientType.COMPANY);
			amdocs.updateCoupon(coupon);

			//Cannot update image
			byte[] updateImage = couponRepo.findByTitle("Shopping in the mall").getImage();
			
			System.err.println("input image length= " + coupon.getImage().length);
			System.err.println("db image length= " + updateImage.length);
			
			Assert.assertFalse(coupon.getImage().length == updateImage.length);
		}


		/**
		 * Test for updateCoupon method of CompanyFacade.
		 * If the coupon with the same title of the same company does not exist in the database,
		 * the method throws CouponNotFoundException,
		 * and the coupon is not updated.
		 */
		@Test (expected = CouponNotFoundException.class)
		public void test_3_024_companyUpdateCoupon() {
			CouponSystem couponsystem = new CouponSystem(ctx);

			Coupon coupon = new Coupon();
			coupon.setTitle("New coupon");
			coupon.setPrice(100);

			CompanyFacade amdocs = (CompanyFacade) couponsystem.login("Amdocs", "456", ClientType.COMPANY);
			amdocs.updateCoupon(coupon);

			//Cannot update coupon that does not exist
			Assert.assertFalse(couponRepo.existsById(coupon.getId()));
		}

		//Get coupon
		/**
		 * Test for getCoupon method of CompanyFacade.
		 * If the coupon of the logged in company with the given ID exists in the database,
		 * the method displays the coupon object from the database. 
		 */
		@Test
		public void test_3_025_companyGetCoupon() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			CompanyFacade teva = (CompanyFacade) couponsystem.login("Teva", "123", ClientType.COMPANY);
			Coupon coupon = teva.getCoupon(1);
			System.out.println(coupon);

			Assert.assertNotNull(coupon);
			Assert.assertEquals(1, coupon.getId());
		}

		/**
		 * Test for getCoupon method of CompanyFacade.
		 * If the coupon  with the given ID does not belong to the logged in company,
		 * the method throws CouponNotFoundException, and no coupon is displayed. 
		 */
		@Test  (expected = CouponNotFoundException.class)
		public void test_3_026_companyGetCoupon() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			CompanyFacade teva = (CompanyFacade) couponsystem.login("Teva", "123", ClientType.COMPANY);
			Coupon coupon = teva.getCoupon(3);
			System.out.println(coupon);

			//Cannot get coupon that does not exist in this company		
			Assert.assertNull(coupon);
		}

		//Get all coupons

		/**
		 * Test for getAllCoupons method of CompanyFacade.
		 * If there are any coupons of the logged in company in the database
		 * the method displays all these coupons.
		 */
		@Test
		public void test_3_027_companyGetAllCoupons() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			CompanyFacade teva = (CompanyFacade) couponsystem.login("Teva", "123", ClientType.COMPANY);
			Collection <Coupon> coupons = teva.getAllCoupons();
			System.out.println(coupons);

			Assert.assertNotNull(coupons);

			int sizeInDb= couponRepo.findCouponByCompanyId(teva.getLoginCompany().getId()).size();

			Assert.assertEquals(sizeInDb, coupons.size());

		}

		/**
		 * Test for getAllCoupons method of CompanyFacade.
		 * If there are any coupons of the logged in company in the database
		 * the method displays all these coupons.
		 */
		@Test
		public void test_3_028_companyGetAllCoupons() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			CompanyFacade amdocs = (CompanyFacade) couponsystem.login("Amdocs", "456", ClientType.COMPANY);
			Collection <Coupon> coupons = amdocs.getAllCoupons();
			System.out.println(coupons);

			Assert.assertNotNull(coupons);

			int sizeInDb= couponRepo.findCouponByCompanyId(amdocs.getLoginCompany().getId()).size();

			Assert.assertEquals(sizeInDb, coupons.size());
		}


		/**
		 * Test for getAllCoupons method of CompanyFacade.
		 * If there are no coupons of the logged in company in the database
		 * the method throws CouponNotFoundException, and no coupons are displayed. 
		 */
		@Test (expected = CouponNotFoundException.class)
		public void test_3_029_companyGetAllCoupons() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			CompanyFacade sonol = (CompanyFacade) couponsystem.login("Sonol", "444", ClientType.COMPANY);
			Collection <Coupon> coupons = sonol.getAllCoupons();

			//cannot get coupons if the company does not have any coupons
			Assert.assertNull(coupons);
		}

		//Get coupons by type
		/**
		 * Test for getCouponsByType method of CompanyFacade.
		 * If there are coupons of the logged in company of the given type
		 * in the database, the method displays all these coupons. 
		 */
		@Test
		public void test_3_030_companyGetCouponsByType() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			CompanyFacade teva = (CompanyFacade) couponsystem.login("Teva", "123", ClientType.COMPANY);
			Collection <Coupon> coupons = teva.getCouponsByType(CouponType.CAMPING);
			System.out.println(coupons);

			Assert.assertNotNull(coupons);

			int sizeInDb= couponRepo.findCouponByTypeAndCompanyId(CouponType.CAMPING, teva.getLoginCompany().getId()).size();

			Assert.assertEquals(sizeInDb, coupons.size());

		}

		/**
		 * Test for getCouponsByType method of CompanyFacade.
		 * If in the database there are no coupons of the logged in company of the given type 
		 * the method throws CouponNotFoundException, and no coupons are displayed. 
		 */
		@Test  (expected = CouponNotFoundException.class)
		public void test_3_031_companyGetCouponsByType() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			CompanyFacade teva = (CompanyFacade) couponsystem.login("Teva", "123", ClientType.COMPANY);
			Collection <Coupon> coupons = teva.getCouponsByType(CouponType.FOOD);
			System.out.println(coupons);

			//cannot get coupons if the company does not have any coupons of this type
			Assert.assertNull(coupons);

		}

		//Get coupons by price
		/**
		 * Test for getCouponsByPrice method of CompanyFacade.
		 * If in the database there are coupons of the logged in company the price of which is lower
		 * than the given price, the method displays all these coupons. 
		 */
		@Test
		public void test_3_032_companyGetCouponsByPrice() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			CompanyFacade teva = (CompanyFacade) couponsystem.login("Teva", "123", ClientType.COMPANY);
			Collection <Coupon> coupons = teva.getCouponsByPrice(200);
			System.out.println(coupons);

			Assert.assertNotNull(coupons);

			int sizeInDb= couponRepo.findByMaxPriceAndCompanyId(200, teva.getLoginCompany().getId()).size();

			Assert.assertEquals(sizeInDb, coupons.size());

		}

		/**
		 * Test for getCouponsByPrice method of CompanyFacade.
		 * If in the database there are no coupons of the logged in company
		 * the price of which is lower than the given price, 
		 * the method throws CouponNotFoundException, and no coupons are displayed. 
		 */
		@Test  (expected = CouponNotFoundException.class)
		public void test_3_033_companyGetCouponsByPrice() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			CompanyFacade teva = (CompanyFacade) couponsystem.login("Teva", "123", ClientType.COMPANY);
			Collection <Coupon> coupons = teva.getCouponsByPrice(50);
			System.out.println(coupons);

			//cannot get coupons if the company does not have any coupons under this price
			Assert.assertNull(coupons);
		}

		//Get coupons by end date
		/**
		 * Test for getCouponsByEndDate method of CompanyFacade.
		 * If in the database there are coupons of the logged in company the end date of which is before
		 * the given end date, the method displays all these coupons. 
		 * 
		 * @throws ParseException if the end date could not be generated properly while parsing
		 */
		@Test
		public void test_3_034_companyGetCouponsByEndDate() throws ParseException {

			CouponSystem couponsystem = new CouponSystem(ctx);
			CompanyFacade teva = (CompanyFacade) couponsystem.login("Teva", "123", ClientType.COMPANY);
			Date endDate=this.dateFormat.parse("2018-07-01");
			Collection <Coupon> coupons = 
					teva.getCouponsByEndDate(endDate);
			System.out.println(coupons);

			Assert.assertNotNull(coupons);

			int sizeInDb= couponRepo.findByMaxEndDateAndCompanyId(endDate,
					teva.getLoginCompany().getId()).size();

			Assert.assertEquals(sizeInDb, coupons.size());

		}

		/**
		 * Test for getCouponsByEndDate method of CompanyFacade.
		 * If in the database there are no coupons of the logged in company
		 * the end date of which is before the given end date, 
		 * the method throws CouponNotFoundException, and no coupons are displayed.
		 * 
		 * @throws ParseException if the end date could not be generated properly while parsing
		 */
		 
		@Test  (expected = CouponNotFoundException.class)
		public void test_3_035_companyGetCouponsByEndDate() throws ParseException {

			CouponSystem couponsystem = new CouponSystem(ctx);
			CompanyFacade teva = (CompanyFacade) couponsystem.login("Teva", "123", ClientType.COMPANY);
			Date endDate=this.dateFormat.parse("2018-01-01");
			Collection <Coupon> coupons = 
					teva.getCouponsByEndDate(endDate);
			System.out.println(coupons);

			//cannot get coupons if the company does not have any coupons ending after this date
			Assert.assertNull(coupons);
		}
		
		
		//Test that if a company is removed all its coupons are also removed.

		



		//3. Test Customer Facade methods

		// login as a customer
		/**
		 * Test for login method of CustomerFacade class.
		 * When the username and password are correct, the method returns
		 * the instance of CustomerFacade.
		 */
		@Test
		public void test_4_001_customerLogin() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			CustomerFacade avi = (CustomerFacade) couponsystem.login("Avi", "111", ClientType.CUSTOMER);

			Assert.assertNotNull(avi);
		}

		/**
		 * Test for login method of CustomerFacade class.
		 * When the the password is incorrect, the method throws
		 * WrongPasswordException and returns null.
		 */
		@Test (expected = WrongPasswordException.class)
		public void test_4_002_customerLogin() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			CustomerFacade avi = (CustomerFacade) couponsystem.login("Avi", "123", ClientType.CUSTOMER);

			Assert.assertNull(avi);
		}


		/**
		 * Test for login method of CustomerFacade class.
		 * When the the username does not exist in the database,
		 * the method throws UserNotFoundException and returns null.
		 */
		@Test (expected = UserNotFoundException.class)
		public void test_4_003_customerLogin() { 

			CouponSystem couponsystem = new CouponSystem(ctx);
			CustomerFacade yuval = (CustomerFacade) couponsystem.login("Yuval", "123", ClientType.CUSTOMER);

			Assert.assertNull(yuval);
		}

		
		
		//Get coupon
			/**
			 * Test for getCoupon method of CustomerFacade.
			 * If the coupon with the given ID exists in the database,
			 * the method displays the coupon object from the database. 
			 */
			@Test
			public void test_4_004_customerGetCoupon() {

				CouponSystem couponsystem = new CouponSystem(ctx);
				CustomerFacade avi = (CustomerFacade) couponsystem.login("Avi", "111", ClientType.CUSTOMER);
	         	Coupon coupon = avi.getCoupon(1);
				System.out.println(coupon);

				Assert.assertNotNull(coupon);
				Assert.assertEquals(1, coupon.getId());
			}

			/**
			 * Test for getCoupon method of CustomerFacade.
			 * If the coupon  with the given ID does not exist in the database,
			 * the method throws CouponNotFoundException, and no coupon is displayed. 
			 */
			@Test  (expected = CouponNotFoundException.class)
			public void test_4_005_customerGetCoupon() {

				CouponSystem couponsystem = new CouponSystem(ctx);
				CustomerFacade avi = (CustomerFacade) couponsystem.login("Avi", "111", ClientType.CUSTOMER);
	         	Coupon coupon = avi.getCoupon(55);
				System.out.println(coupon);

				//Cannot get coupon that does not exist in this company		
				Assert.assertNull(coupon);
			}
			
			
			//Get all coupons

			/**
			 * Test for getAllCoupons method of CustomerFacade.
			 * If there are coupons in the database
			 * the method displays all these coupons.
			 */
			@Test
			public void test_4_006_customerGetAllCoupons() {

				CouponSystem couponsystem = new CouponSystem(ctx);
				CustomerFacade avi = (CustomerFacade) couponsystem.login("Avi", "111", ClientType.CUSTOMER);
		        Collection <Coupon> coupons = avi.getAllCoupons();
				System.out.println(coupons);

				Assert.assertNotNull(coupons);

				long sizeInDb= couponRepo.count();
				Assert.assertEquals(sizeInDb, coupons.size());

			}


		//Purchase coupon
		/**
		 * Test for purchaseCoupon method of CustomerFacade.
		 * If the coupon details are correct and the logged in customer is allowed to purchase the coupon,
		 * the method adds it to the logged in customer's coupons in the database and updated the amount of available coupons. 
		 */
		@Test
		public void test_4_007_customerPurchaseCoupon() 	{

			CouponSystem couponsystem = new CouponSystem(ctx);
			
			Coupon c = couponRepo.findByTitle("Shopping in the mall");
			
			CustomerFacade avi = (CustomerFacade) couponsystem.login("Avi", "111", ClientType.CUSTOMER);
			

			int amountBefore= couponRepo.findByTitle("Shopping in the mall").getAmount();
			avi.purchaseCoupon(c);

			int amountAfter= couponRepo.findByTitle("Shopping in the mall").getAmount();

			Assert.assertNotNull(couponRepo.findCustomerCoupon(avi.getLoginCustomer().getId(), c.getId()));
			Assert.assertEquals(amountBefore -1, amountAfter);

		}

		/**
		 * Test for purchaseCoupon method of CustomerFacade.
		 * If the coupon details are correct and the logged in customer is allowed to purchase the coupon,
		 * the method adds it to the logged in customer's coupons in the database and updated the amount of available coupons. 
		 */
		@Test
		public void test_4_008_customerPurchaseCoupon() 	{

			CouponSystem couponsystem = new CouponSystem(ctx);
			CustomerFacade avi = (CustomerFacade) couponsystem.login("Avi", "111", ClientType.CUSTOMER);
			Coupon c = couponRepo.findByTitle("Free camping");

			int amountBefore= couponRepo.findByTitle("Free camping").getAmount();
			avi.purchaseCoupon(c);

			int amountAfter= couponRepo.findByTitle("Free camping").getAmount();

			Assert.assertNotNull(couponRepo.findCustomerCoupon(avi.getLoginCustomer().getId(), c.getId()));
			Assert.assertEquals(amountBefore -1, amountAfter);

		}

		/**
		 * Test for purchaseCoupon method of CustomerFacade.
		 * If the coupon does not exist in the database, the method throws CouponNotFoundException.
		 * The coupon is not added to the logged in customer's coupons and the amount of available coupons remains the same. 
		 */
		@Test (expected = CouponNotFoundException.class)
		public void test_4_009_customerPurchaseCoupon() 	{

			CouponSystem couponsystem = new CouponSystem(ctx);
			CustomerFacade avi = (CustomerFacade) couponsystem.login("Avi", "111", ClientType.CUSTOMER);
			Coupon c = new Coupon();
			c.setTitle("Empty coupon");

			avi.purchaseCoupon(c);

			//Cannot purchase coupon that does not exist
			Assert.assertNull(couponRepo.findCustomerCoupon(avi.getLoginCustomer().getId(), c.getId()));

		}	

		/**
		 * Test for purchaseCoupon method of CustomerFacade.
		 * If the coupon has been already purchased by the logged in customer,
		 * the method throws CouponAlreadyPurchasedException. 
		 * No coupon is added to the logged in customer's coupons and the amount of available coupons remains the same. 
		 */
		@Test (expected = CouponAlreadyPurchasedException.class)
		public void test_4_010_customerPurchaseCoupon() 	{

			CouponSystem couponsystem = new CouponSystem(ctx);
			CustomerFacade avi = (CustomerFacade) couponsystem.login("Avi", "111", ClientType.CUSTOMER);
			Coupon c = couponRepo.findByTitle("Shopping in the mall");

			int sizeBefore = couponRepo.findCustomerCoupons(avi.getLoginCustomer().getId()).size();

			//Cannot purchase coupon that was already purchased by this customer
			avi.purchaseCoupon(c);

			int sizeAfter = couponRepo.findCustomerCoupons(avi.getLoginCustomer().getId()).size();

			Assert.assertEquals(sizeBefore, sizeAfter);

		}
		

		@Test 
		public void test_4_011_customerPurchaseCoupon() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			CustomerFacade avi = (CustomerFacade) couponsystem.login("Avi", "111", ClientType.CUSTOMER);
			Coupon c = couponRepo.findByTitle("Flight to Ibiza");

			int amountBefore= couponRepo.findByTitle("Flight to Ibiza").getAmount();

			avi.purchaseCoupon(c);

			int amountAfter= couponRepo.findByTitle("Flight to Ibiza").getAmount();

			Assert.assertNotNull(couponRepo.findCustomerCoupon(avi.getLoginCustomer().getId(), c.getId()));
			Assert.assertEquals(amountBefore -1, amountAfter);

		}
		
		@Test 
		public void test_4_012_customerPurchaseCoupon() 	{

			CouponSystem couponsystem = new CouponSystem(ctx);
			CustomerFacade harel = (CustomerFacade) couponsystem.login("Harel", "666", ClientType.CUSTOMER);
			Coupon c = couponRepo.findByTitle("Shopping in the mall");

			int amountBefore= couponRepo.findByTitle("Shopping in the mall").getAmount();

			harel.purchaseCoupon(c);

			int amountAfter= couponRepo.findByTitle("Shopping in the mall").getAmount();

			Assert.assertNotNull(couponRepo.findCustomerCoupon(harel.getLoginCustomer().getId(), c.getId()));
			Assert.assertEquals(amountBefore -1, amountAfter);

		}

		@Test 
		public void test_4_013_customerPurchaseCoupon() 	{

			CouponSystem couponsystem = new CouponSystem(ctx);
			CustomerFacade harel = (CustomerFacade) couponsystem.login("Harel", "666", ClientType.CUSTOMER);
			Coupon c = couponRepo.findByTitle("Flight to Eilat");

			int amountBefore= couponRepo.findByTitle("Flight to Eilat").getAmount();

			harel.purchaseCoupon(c);

			int amountAfter= couponRepo.findByTitle("Flight to Eilat").getAmount();

			Assert.assertNotNull(couponRepo.findCustomerCoupon(harel.getLoginCustomer().getId(), c.getId()));
			Assert.assertEquals(amountBefore -1, amountAfter);

		}
		

		/**
		 * Test for purchaseCoupon method of CustomerFacade.
		 * If the coupon details are correct and the logged in customer is allowed to purchase the coupon,
		 * the method adds it to the logged in customer's coupons in the database and updated the amount of available coupons. 
		 */
		@Test
		public void test_4_014_customerPurchaseCoupon() 	{

			CouponSystem couponsystem = new CouponSystem(ctx);
			CustomerFacade gabi = (CustomerFacade) couponsystem.login("Gabi", "444", ClientType.CUSTOMER);
			Coupon c = couponRepo.findByTitle("Free camping");

			int amountBefore= couponRepo.findByTitle("Free camping").getAmount();

			gabi.purchaseCoupon(c);

			int amountAfter= couponRepo.findByTitle("Free camping").getAmount();

			Assert.assertNotNull(couponRepo.findCustomerCoupon(gabi.getLoginCustomer().getId(), c.getId()));
			Assert.assertEquals(amountBefore -1, amountAfter);

		}

		/**
		 * Test for purchaseCoupon method of CustomerFacade.
		 * If the coupon details are correct and the logged in customer is allowed to purchase the coupon,
		 * the method adds it to the logged in customer's coupons in the database and updated the amount of available coupons. 
		 */
		@Test
		public void test_4_015_customerPurchaseCoupon() 	{

			CouponSystem couponsystem = new CouponSystem(ctx);
			CustomerFacade gabi = (CustomerFacade) couponsystem.login("Gabi", "444", ClientType.CUSTOMER);
			Coupon c = couponRepo.findByTitle("Flight to Ibiza");

			int amountBefore= couponRepo.findByTitle("Flight to Ibiza").getAmount();

			gabi.purchaseCoupon(c);

			int amountAfter= couponRepo.findByTitle("Flight to Ibiza").getAmount();

			Assert.assertNotNull(couponRepo.findCustomerCoupon(gabi.getLoginCustomer().getId(), c.getId()));
			Assert.assertEquals(amountBefore -1, amountAfter);

		}

		/**
		 * Test for purchaseCoupon method of CustomerFacade.
		 * If the coupon is out of stock, the method throws CouponOutOfStockException. 
		 * No coupon is added to the logged in customer's coupons and the amount of available coupons remains the same. 
		 */
		@Test  (expected = CouponOutOfStockException.class)
		public void test_4_016_customerPurchaseCoupon() 	{

			CouponSystem couponsystem = new CouponSystem(ctx);
			CustomerFacade dudi = (CustomerFacade) couponsystem.login("Dudi", "555", ClientType.CUSTOMER);
			Coupon c = couponRepo.findByTitle("Free camping");

			int amountBefore= couponRepo.findByTitle("Free camping").getAmount();

			dudi.purchaseCoupon(c);

			int amountAfter= couponRepo.findByTitle("Free camping").getAmount();

			//Cannot purchase coupon that is out of stock
			Assert.assertNull(couponRepo.findCustomerCoupon(dudi.getLoginCustomer().getId(), c.getId()));
			Assert.assertEquals(amountBefore, amountAfter);

		}

		/**
		 * Test for purchaseCoupon method of CustomerFacade.
		 * If the coupon has expired, the method throws CouponExpiredException. 
		 * No coupon is added to the logged in customer's coupons and the amount of available coupons remains the same. 
		 */
		@Test (expected = CouponExpiredException.class)
		public void test_4_017_customerPurchaseCoupon() 	{

			CouponSystem couponsystem = new CouponSystem(ctx);
			CustomerFacade dudi = (CustomerFacade) couponsystem.login("Dudi", "555", ClientType.CUSTOMER);
			Coupon c = couponRepo.findByTitle("Dinner for two");

			int amountBefore= couponRepo.findByTitle("Dinner for two").getAmount();

			dudi.purchaseCoupon(c);

			int amountAfter= couponRepo.findByTitle("Dinner for two").getAmount();


			//Cannot purchase coupon that has expired
			Assert.assertNull(couponRepo.findCustomerCoupon(dudi.getLoginCustomer().getId(), c.getId()));
			Assert.assertEquals(amountBefore, amountAfter);

		}
		
		

		//Get all customer's coupons

		/**
		 * Test for getAllPurchasedCoupons method of CustomerFacade.
		 * If there are any coupons purchased by the logged in customer in the database, these coupons are displayed. 
		 */
		@Test
		public void test_4_018_customerGetAllPurchasedCoupons() 	{

			CouponSystem couponsystem = new CouponSystem(ctx);
			CustomerFacade avi = (CustomerFacade) couponsystem.login("Avi", "111", ClientType.CUSTOMER);
			Collection <Coupon> coupons = avi.getAllPurchasedCoupons();
			System.out.println(coupons);

			int sizeInDb= couponRepo.findCustomerCoupons(avi.getLoginCustomer().getId()).size();				

			Assert.assertNotNull(coupons);
			Assert.assertEquals(sizeInDb, coupons.size());

		}

		/**
		 * Test for getAllPurchasedCoupons method of CustomerFacade.
		 * If there are any coupons purchased by the logged in customer in the database, these coupons are displayed. 
		 */
		@Test
		public void test_4_019_customerGetAllPurchasedCoupons() 	{

			CouponSystem couponsystem = new CouponSystem(ctx);
			CustomerFacade gabi = (CustomerFacade) couponsystem.login("Gabi", "444", ClientType.CUSTOMER);
			Collection <Coupon> coupons = gabi.getAllPurchasedCoupons();
			System.out.println("GABI'S COUPONS: " + coupons);

			int sizeInDb= couponRepo.findCustomerCoupons(gabi.getLoginCustomer().getId()).size();				

			Assert.assertNotNull(coupons);
			Assert.assertEquals(sizeInDb, coupons.size());

		}

		/**
		 * Test for getAllPurchasedCoupons method of CustomerFacade.
		 * If there are no coupons purchased by the logged in customer in the database
		 * the method throws CouponNotFoundException, and no coupons are displayed. 
		 */
		@Test (expected = CouponNotFoundException.class)
		public void test_4_020_customerGetAllPurchasedCoupons() 	{

			CouponSystem couponsystem = new CouponSystem(ctx);
			CustomerFacade dudi = (CustomerFacade) couponsystem.login("Dudi", "555", ClientType.CUSTOMER);
			Collection <Coupon> coupons = dudi.getAllPurchasedCoupons();
			System.out.println(coupons);

			//Cannot get coupons if the customer does not have any coupons
			Assert.assertNull(coupons);
		}


		//Get customer's coupons by type

		/**
		 * Test for getPurchasedCouponsByType method of CustomerFacade.
		 * If in the database there are any coupons of the given type 
		 * purchased by the logged in customer, these coupons are displayed. 
		 */
		@Test
		public void test_4_021_customerGetAllPurchasedCouponsByType() 	{

			CouponSystem couponsystem = new CouponSystem(ctx);
			CustomerFacade avi = (CustomerFacade) couponsystem.login("Avi", "111", ClientType.CUSTOMER);
			Collection <Coupon> coupons = avi.getAllPurchasedCouponsByType(CouponType.CAMPING);
			System.out.println(coupons);

			int sizeInDb= couponRepo.findCustomerCouponsByType(avi.getLoginCustomer().getId(), CouponType.CAMPING).size();				

			Assert.assertNotNull(coupons);
			Assert.assertEquals(sizeInDb, coupons.size());

		}

		/**
		 * Test for getAllPurchasedCouponsByType method of CustomerFacade.
		 * If in the database there are any coupons of the given type 
		 * purchased by the logged in customer, these coupons are displayed. 
		 */
		@Test
		public void test_4_022_customerGetAllPurchasedCouponsByType() 	{

			CouponSystem couponsystem = new CouponSystem(ctx);
			CustomerFacade gabi = (CustomerFacade) couponsystem.login("Gabi", "444", ClientType.CUSTOMER);
			Collection <Coupon> coupons = gabi.getAllPurchasedCouponsByType(CouponType.TRAVELLING);
			System.out.println(coupons);

			int sizeInDb= couponRepo.findCustomerCouponsByType(gabi.getLoginCustomer().getId(), CouponType.TRAVELLING).size();				

			Assert.assertNotNull(coupons);
			Assert.assertEquals(sizeInDb, coupons.size());

		}

		/**
		 * Test for getPurchasedCouponsByType method of CustomerFacade.
		 * If in the database there are no coupons of the given type 
		 * purchased by the logged in customer,
		 * the method throws CouponNotFoundException, and no coupons are displayed. 
		 */
		@Test (expected = CouponNotFoundException.class)
		public void test_4_023_customerGetAllPurchasedCouponsByType() 	{

			CouponSystem couponsystem = new CouponSystem(ctx);
			CustomerFacade gabi = (CustomerFacade) couponsystem.login("Gabi", "444", ClientType.CUSTOMER);
			Collection <Coupon> coupons = gabi.getAllPurchasedCouponsByType(CouponType.ELECTRICITY);
			System.out.println(coupons);

			//Cannot get coupons if the customer does not have any coupons of this type
			Assert.assertNull(coupons);


		}

		//Get customer's coupons by price

		/**
		 * Test for getAllPurchasedCouponsByPrice method of CustomerFacade.
		 * If in the database there are any coupons the price of which is lower than the given price 
		 * purchased by the logged in customer, these coupons are displayed. 
		 */
		@Test
		public void test_4_024_customerGetCouponsAllPurchasedByPrice() 	{

			CouponSystem couponsystem = new CouponSystem(ctx);
			CustomerFacade avi = (CustomerFacade) couponsystem.login("Avi", "111", ClientType.CUSTOMER);
			Collection <Coupon> coupons = avi.getAllPurchasedCouponsByPrice(500);
			System.out.println(coupons);

			int sizeInDb= couponRepo.findCustomerCouponsByMaxPrice(avi.getLoginCustomer().getId(), 500).size();				

			Assert.assertNotNull(coupons);
			Assert.assertEquals(sizeInDb, coupons.size());

		}

		/**
		 * Test for getPurchasedCouponsByPrice method of CustomerFacade.
		 * If in the database there are any coupons the price of which is lower than the given price 
		 * purchased by the logged in customer, these coupons are displayed. 
		 */
		@Test
		public void test_4_025_customerGetAllPurchasedCouponsByPrice() 	{

			CouponSystem couponsystem = new CouponSystem(ctx);
			CustomerFacade gabi = (CustomerFacade) couponsystem.login("Gabi", "444", ClientType.CUSTOMER);
			Collection <Coupon> coupons = gabi.getAllPurchasedCouponsByPrice(400);
			System.out.println(coupons);

			int sizeInDb= couponRepo.findCustomerCouponsByMaxPrice(gabi.getLoginCustomer().getId(), 400).size();				

			Assert.assertNotNull(coupons);
			Assert.assertEquals(sizeInDb, coupons.size());

		}

		/**
		 * Test for getPurchasedCouponsByPrice method of CustomerFacade.
		 * If in the database there are no coupons the price of which is lower than the given price 
		 * purchased by the logged in customer,
		 * the method throws CouponNotFoundException, and no coupons are displayed. 
		 */
		@Test (expected = CouponNotFoundException.class)
		public void test_4_026_customerGetAllPurchasedCouponsByPrice() 	{

			CouponSystem couponsystem = new CouponSystem(ctx);
			CustomerFacade gabi = (CustomerFacade) couponsystem.login("Gabi", "444", ClientType.CUSTOMER);
			Collection <Coupon> coupons = gabi.getAllPurchasedCouponsByPrice(10);
			System.out.println(coupons);

			//Cannot get coupons if the customer does not have any coupons under this price
			Assert.assertNull(coupons);
		}
		
		/**
		 * Test for getAllNonPurchasedCoupons method of CustomerFacade.
		 * If there are any coupons that were not purchased by the logged in customer in the database, these coupons are displayed. 
		 */
		@Test
		public void test_4_027_customerGetAllNonPurchasedCoupons() 	{

			CouponSystem couponsystem = new CouponSystem(ctx);
			CustomerFacade avi = (CustomerFacade) couponsystem.login("Avi", "111", ClientType.CUSTOMER);
			Collection <Coupon> coupons = avi.getAllNonPurchasedCoupons();
			System.out.println("Non-purchased coupons are: " + coupons);

			int sizeInDb= (int) (couponRepo.count() -
					couponRepo.findCustomerCoupons(avi.getLoginCustomer().getId()).size());				

			Assert.assertNotNull(coupons);
			Assert.assertEquals(sizeInDb, coupons.size());
			
		}
		
		
		/**
		 * Test for getAllNonPurchasedCoupons method of CustomerFacade.
		 * If the customer purchased all the coupons - in the database,
		 * CouponNotFoundException is thrown.
		 * The test has 3 steps:
		 * 1. Company prolongs expired coupon in order that the customer could purchase it.
		 * 2. Customer purchases 2 coupons that he did not purchase yet.
		 * 3. Customer invokes getAllNonPurchasedCoupons method.
		 */
		
		@Test  (expected = CouponNotFoundException.class)
		public void test_4_028_customerGetAllNonPurchasedCoupons() 	{
			
			CouponSystem couponsystem = new CouponSystem(ctx);
			
			Coupon c1 = couponRepo.findByTitle("Shopping in the mall");
			Coupon c2 = couponRepo.findByTitle("Dinner for two");
			Coupon c3 = couponRepo.findByTitle("Flight to Eilat");
			Coupon c4 = couponRepo.findByTitle("Downhill skiing");
			
			c2.setEndDate("2019-10-01");
			CompanyFacade teva = (CompanyFacade) couponsystem.login("Teva", "123", ClientType.COMPANY);
			teva.updateCoupon(c2);
			
			CustomerFacade gabi = (CustomerFacade) couponsystem.login("Gabi", "444", ClientType.CUSTOMER);
			gabi.purchaseCoupon(c1);
			gabi.purchaseCoupon(c2);
			gabi.purchaseCoupon(c3);
			gabi.purchaseCoupon(c4);
			
			Collection <Coupon> coupons = gabi.getAllNonPurchasedCoupons();
		
			Assert.assertNull(coupons);
		}
		
		/**
		 * Test for removeCompany method of AdminFacade in case that a company has coupons purchased by customers.
		 * When the method removes the company from the database all its coupons are also removed. 
		 * This test removes company with one coupon that was purchased by a customer.
		 */
		@Test
		public void test_4_029_adminRemoveCompanyWithCoupons()  {

			CouponSystem couponsystem = new CouponSystem(ctx);
			AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
			Company arkia= companyRepo.findByName("Arkia");
			CustomerFacade harel = (CustomerFacade) couponsystem.login("Harel", "666", ClientType.CUSTOMER);
			
			int sizeBefore= harel.getAllPurchasedCoupons().size();
			
	        Assert.assertTrue(companyRepo.existsByCompanyName("Arkia"));		
			Assert.assertTrue(couponRepo.existsByTitle("Flight to Eilat"));
			
			adminFacade.removeCompany(arkia);
			
			int sizeAfter= harel.getAllPurchasedCoupons().size();

			Assert.assertFalse(companyRepo.existsByCompanyName("Arkia"));
			Assert.assertFalse(couponRepo.existsByTitle("Flight to Eilat"));
			
			Assert.assertTrue(sizeAfter==sizeBefore-1);
		}

		/**
		 * Test for removeCoupon method of CompanyFacade in case that a coupon is purchased by customer.
		 * When the method removes the coupon from the database, it is removed also from customer's coupons. 
		 */
		@Test
		public void test_4_030_adminRemoveCouponWithCustomers() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			
			CustomerFacade gabi = (CustomerFacade) couponsystem.login("Gabi", "444", ClientType.CUSTOMER);  
	        Coupon coupon = couponRepo.findByTitle("Flight to Ibiza");
	        
	        int sizeBefore= gabi.getAllPurchasedCoupons().size();
	        
	        CompanyFacade amdocs = (CompanyFacade) couponsystem.login("Amdocs", "456", ClientType.COMPANY);
			amdocs.removeCoupon(coupon);
			
			int sizeAfter= gabi.getAllPurchasedCoupons().size();
			
			Assert.assertTrue(sizeAfter==sizeBefore-1);
		}
		
		/**
		 * Test for removeCustomer method of AdminFacade in case that a customer has coupons.
		 * When the method removes the customer from the database all his coupons
		 * are also removed from joint coupon-customer table.
		 */
		@Test
		public void test_4_031_adminRemoveCustomerWithCoupons() {

			CouponSystem couponsystem = new CouponSystem(ctx);
			AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
		    Customer harel = customerRepo.findByName("Harel"); 
		    
	        Assert.assertTrue(customerRepo.existsByCustomerName("Harel"));
	        
	        int sizeBefore= couponRepo.findByTitle("Shopping in the mall").getCustomers().size();
			
			adminFacade.removeCustomer(harel);
			
			Assert.assertFalse(customerRepo.existsByCustomerName("Harel"));
			
			int sizeAfter= couponRepo.findByTitle("Shopping in the mall").getCustomers().size();
			 
			Assert.assertTrue(sizeAfter==sizeBefore-1);
	}

}

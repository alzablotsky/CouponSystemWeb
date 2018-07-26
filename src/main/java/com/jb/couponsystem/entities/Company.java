package com.jb.couponsystem.entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
 * This class represents the company entity object.
 * The COMPANIES table in the database is generated from this entity, 
 * while its attributes form the columns of the table.
 * 
 * @author Alexander Zablotsky
 *
 */
@Entity(name="COMPANIES")
@XmlRootElement
public class Company implements Serializable {
	
//Attributes
	
	//Primary key of the table, generated automatically
	@Id @GeneratedValue (strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column
	private String companyName;
	

	@Column
	private String password;
	

	@Column
	private String email;

	/*
	 * The Coupons attribute is connected to the COUPONS table.
	 * The connection is one-to-many: one company can issue many coupons, but each coupon is associated with only one company.
	 * This connection creates the join column COMPANY_ID in COUPONS table.
	 * Fetch type EAGER: when a company is loaded from the database, all its coupons are loaded with it.
	 * Cascade type ALL: any change in company entity (save, delete etc.) is also cascaded to all coupons entities associated with it. 
	 * 
	 */
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL,
			    orphanRemoval = true)
	@JoinColumn(name="company_id")
	@JsonIgnore
	private Collection<Coupon> coupons;
	
//CTORS
	/**
	 * 
	 * @param companyName company name 
	 * @param password company's password
	 * @param email company's email address
	 * @param coupons collection of company's coupons
	 */
	public Company(String companyName, String password, String email, Collection<Coupon> coupons) {
		super();
		this.companyName = companyName;
		this.password = password;
		this.email = email;
		this.coupons= coupons;
		
	}

	public Company() {
		super();
	}
	
	
	/**
	 * 
	 * @param companyName company name 
	 * @param password company's password
	 * @param email company's email address
	 */
public Company(String companyName, String password, String email) {
		super();
		this.companyName = companyName;
		this.password = password;
		this.email = email;
	}



//Getters and setters	
		
/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the companyName
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * @param companyName the companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the coupons
	 */
	public Collection<Coupon> getCoupons() {
		return coupons;
	}

	/**
	 * @param coupons the coupons to set
	 */
	public void setCoupons(Collection<Coupon> coupons) {
		this.coupons = coupons;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Company [id=" + id + ", companyName=" + companyName + ", password=" + password + ", email=" + email
				+ ", coupons=" + coupons + "]";
	}

		
	
	
}

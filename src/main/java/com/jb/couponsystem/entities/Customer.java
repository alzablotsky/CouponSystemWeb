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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
 * This class represents the customer entity object.
 * The CUSTOMERS table in the database is generated from this entity, 
 * while its attributes form the columns of the table.
 * 
 * @author Alexander Zablotsky
 *
 */
@Entity(name="CUSTOMERS")
@XmlRootElement
public class Customer  implements Serializable {

	//Attributes
	@Id @GeneratedValue (strategy = GenerationType.IDENTITY)
	private long id;

	@Column
	private String customerName;

	@Column
	private String password;

	/*
	 * The Coupons attribute is connected to the COUPONS table.
	 * The connection is many-to-many: one customer can own many coupons, and one coupon also can be owned by many customers.
	 * This connection is mapped in customers field in Coupon entity.
	 */
	 
//		@ManyToMany(fetch=FetchType.EAGER, cascade = {CascadeType.DETACH , CascadeType.REFRESH}) //CascadeType.MERGE,
//		@JoinTable(name = "customer_coupon",
//		joinColumns = @JoinColumn(name = "customer_id"),
//		inverseJoinColumns = @JoinColumn(name = "coupon_id"))
	
	@ManyToMany(mappedBy = "customers")
	@JsonIgnore
	private Collection<Coupon> coupons;


	//CTORS

	public Customer() {
		super();
	}


	/**
	 * 
	 * @param customerName customer name
	 * @param password customer's password
	 * @param coupons the collection of the customer's coupons
	 */
	public Customer(String customerName, String password, Collection<Coupon> coupons) {
		super();
		this.customerName = customerName;
		this.password = password;
		this.coupons = coupons;
	}


	/**
	 * 
	 * @param customerName customer name
	 * @param password customer's password
	 */
	public Customer(String customerName, String password) {
		super();
		this.customerName = customerName;
		this.password = password;

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
	 * @return the customerName
	 */
	public String getCustomerName() {
		return customerName;
	}


	/**
	 * @param customerName the customerName to set
	 */
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
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
		return "Customer [id=" + id + ", customerName=" + customerName + ", password=" + password + "]"; 
		//+ ", coupons=" + coupons 
	}



}

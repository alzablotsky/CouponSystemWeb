package com.jb.couponsystem.connection;

/**
 * This class represents a connection to the database
 * 
 * 
 * @author Alexander Zablotsky
 *
 */
public class DbConnection {

	//Attributes
	private int port;
	private String url;
	
	//CTOR
	/**
	 *  Constructor
	 *  
	 * @param port connection port
	 * @param url connection url
	 */
	public DbConnection(int port, String url) {
		super();
		this.port = port;
		this.url = url;
	}
	
	

	/**
	 * Default/empty constructor
	 * 
	 */
	public DbConnection() {
		super();
	}



	////Getters and setters
	/**
	 * Getter for the port
	 * 
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 *  Setter for the port
	 * 
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Getter for the url
	 * 
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 *  Setter for the url
	 * 
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DbConnection [port=" + port + ", url=" + url + "]";
	}
	
	
	
	
	
	
	
	
	
	
	
	

}

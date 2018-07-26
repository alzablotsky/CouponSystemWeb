package com.jb.couponsystem.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This class represents a pool of connections to the database that are given to the system users.
 * The number of connections is limited. Therefore, he class is of the Singleton pattern,
 * in order to prevent making simultaneous connections by several users beyond the limit
 * defined by the pool.  
 *  
 * @author Alexander Zablotsky
 *
 */

public class ConnectionPool {

	//Attributes
	// maximum number of connections
	private static final int NUMBER_OF_CONNECTIONS = 5;

	//instance for the class - initially is null
	private static ConnectionPool _instance = null;

	// list of connections
	private ArrayList<Connection> connections = null;

	// Private CTOR - creates the allowed number of connections and adds them to the list
	private ConnectionPool() throws SQLException {

		// create new list of connections
		this.connections = new ArrayList<>();

		// add the allowed number of connections to the list
		for (int i = 0; i < NUMBER_OF_CONNECTIONS; i++)
		{
			// create DB connection
			//DbConnection dbConnection = new DbConnection();
			
			Connection dbConnection = DriverManager.getConnection(
					   "jdbc:postgresql://localhost/webproject","postgres", "admin");

			// add DBConnection to the list
			this.connections.add( dbConnection );
		}
	}


	//Methods
	/**
	 * This method returns the instance of the class, so that only one instance can be created.
	 * First it checks that there are no other instances, if this is true it creates the new instance.
	 * If not - no new instance is created. Finally the created instance is returned.
	 * 
	 * 
	 * @return instance of the  ConnectionPull class
	 * @throws SQLException 
	 */
	public static synchronized ConnectionPool getInstance() throws SQLException
	{
		if (_instance == null)
		{
			_instance = new ConnectionPool();
		}
		return _instance;
	}


	/**
	 * This method gives the connection to the database from the list of allowed connections
	 * to the current thread (user).
	 * First it checks if the connections list is empty, in which case the thread is told to wait.
	 * If the list is not empty - the first connection is discarded from the list and returned, i.e. given to the thread (user).
	 *    
	 * @return connection connection object
	 * @throws InterruptedException if the thread is interrupted
	 */
	public synchronized Connection getConnection() throws InterruptedException {
		while (this.connections.size() == 0) {
			System.err.println("Thread " + Thread.currentThread().getName() + " is waiting now since there are no available connections.");
			wait();
		}
		
		Connection connection = this.connections.get(0);
		this.connections.remove(0);
		System.out.println("Giving connection : " + connection + " to thread " + Thread.currentThread().getName());
		
		return connection;

	}

	/**
	 * This method adds the connection that the thread finished to use to the pool of connections.
	 * If there is a waiting thread - it is notified.
	 * 
	 * @param connection connection object
	 */
	public synchronized void returnConnection(Connection connection)
	{
		this.connections.add(connection);
		System.out.println("Returning connection "  + connection +   ". Thread " + Thread.currentThread().getName() + " is calling notify.");
		notify();
	}
	
	/**
	 * This method removes all connections from the pool
	 * in order to close the system for the users.
	 * 
	 */
	public synchronized void closeAllConnections() {
		this.connections.clear();
		System.out.println("All connections are closed.");
	}

}
package com.apddemo.core.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class QueryDataResult {

	private Connection connection;
	private ResultSet resultSet;
	private PreparedStatement pStatement;
	
	public QueryDataResult(){
		
	}
	
	public QueryDataResult(Connection connection, ResultSet resultSet,
                           PreparedStatement pStatement) {
		this.connection = connection;
		this.resultSet = resultSet;
		this.pStatement = pStatement;
	}
	public Connection getConnection() {
		return connection;
	}
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	public ResultSet getResultSet() {
		return resultSet;
	}
	public void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}
	public PreparedStatement getpStatement() {
		return pStatement;
	}
	public void setpStatement(PreparedStatement pStatement) {
		this.pStatement = pStatement;
	}
}

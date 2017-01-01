package com.coupon.test.utils;


public class TestCondition {

	private static TestCondition instance = null;
	public static final String METHOD_ALL = "ALL";
	public static final String METHOD_POST = "POST";

	private String method = METHOD_ALL;
	
	protected TestCondition() { }	// exists only to defeat instantiation

	public static TestCondition getInstance() {

		if(instance == null) {
			instance = new TestCondition();
		}
		
		return instance;
	}

	public String getMethod() {
		return method;
	}
	
	public void setMethod(String method) {
		this.method = method;
	}

}
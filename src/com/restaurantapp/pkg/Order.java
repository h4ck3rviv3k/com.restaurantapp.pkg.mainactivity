package com.restaurantapp.pkg;

import android.app.Activity;

public class Order extends Activity {

	private String currentOrder;

	public Order(String taskname)
	{
	currentOrder=taskname;
	}
	
	public void setTask(String currentOrder){
		this.currentOrder=currentOrder;
	}
	
	public String getTask()
	{
		return currentOrder;
	}
	
	public String toString(){
		return currentOrder;
	}
}

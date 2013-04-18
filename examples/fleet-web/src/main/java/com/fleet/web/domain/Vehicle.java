package com.fleet.web.domain;

public class Vehicle {	
			private String vin;
			private String modelYear;
			private String model;
	
			public String getVin(){
			return vin;
		}
			public String getModelYear(){
			return modelYear;
		}
			public String getModel(){
			return model;
		}
		
			public void setVin(String vin ){
			this.vin = vin;
		}
			public void setModelYear(String modelYear ){
			this.modelYear = modelYear;
		}
			public void setModel(String model ){
			this.model = model;
		}
		
	public void populateWithSample(){
					vin = "Sample Value 1";
					modelYear = "Sample Value 2";
					model = "Sample Value 3";
			}
	
	public String toString(){
		return
												"vin = " + vin
																+ ", modelYear = " + modelYear
																+ ", model = " + model
										;
	}
}

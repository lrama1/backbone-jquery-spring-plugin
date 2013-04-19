package com.fleet.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

//import the domain
import com.fleet.web.domain.Vehicle;


@Controller
public class VehicleController {
	List<Vehicle> allVehicle = new ArrayList<Vehicle>();
	
	@PostConstruct
	public void init(){
		Vehicle vehicle1 = new Vehicle();		
		vehicle1.setVin("0001");
		vehicle1.setModel("tuarus");
		allVehicle.add(vehicle1);
		
		Vehicle vehicle2 = new Vehicle();		
		vehicle2.setVin("0002");
		vehicle2.setModel("corolla");
		allVehicle.add(vehicle2);
		
		Vehicle vehicle3 = new Vehicle();		
		vehicle3.setVin("0003");
		vehicle3.setModel("hummer");
		allVehicle.add(vehicle3);
		
		Vehicle vehicle4 = new Vehicle();		
		vehicle4.setVin("0004");
		vehicle4.setModel("hummer");
		allVehicle.add(vehicle4);
		
		Vehicle vehicle5 = new Vehicle();		
		vehicle5.setVin("0005");
		vehicle5.setModel("hummer");
		allVehicle.add(vehicle5);
		
		Vehicle vehicle6 = new Vehicle();		
		vehicle6.setVin("0006");
		vehicle6.setModel("hummer");
		allVehicle.add(vehicle6);
		
		Vehicle vehicle7 = new Vehicle();		
		vehicle7.setVin("0007");
		vehicle7.setModel("hummer");
		allVehicle.add(vehicle7);
		
		Vehicle vehicle8 = new Vehicle();		
		vehicle8.setVin("0008");
		vehicle8.setModel("hummer");
		allVehicle.add(vehicle8);
		
		Vehicle vehicle9 = new Vehicle();		
		vehicle9.setVin("0009");
		vehicle9.setModel("hummer");
		allVehicle.add(vehicle9);
		
		Vehicle vehicle10 = new Vehicle();		
		vehicle10.setVin("0010");
		vehicle10.setModel("hummer");
		allVehicle.add(vehicle10);
		
		Vehicle vehicle11 = new Vehicle();		
		vehicle11.setVin("0011");
		vehicle11.setModel("hummer");
		allVehicle.add(vehicle11);
	}
	
	@RequestMapping("/controller/home")
	public String showHomePage(){
		return "/WEB-INF/index.jsp";
	}
	
	@RequestMapping(value = "/controller/vehicle/{id}", method = RequestMethod.GET)
	public @ResponseBody Vehicle getVehicle(@PathVariable("id") String id){
		 Vehicle  vehicle = new  Vehicle();
		  vehicle.populateWithSample();
		 return vehicle;
	}
	
	
	@RequestMapping(value = "/controller/vehicle", headers = {"accept=application/json"}, method = RequestMethod.POST)
	public @ResponseBody String saveNewVehicle(@RequestBody Vehicle vehicle){
		System.out.println(vehicle.toString());
		return "";
	}
	
	@RequestMapping(value = "/controller/vehicle/{id}", headers = {"accept=application/json"}, method = RequestMethod.PUT)
	public  @ResponseBody String  updateVehicle(@RequestBody Vehicle vehicle){
		System.out.println(vehicle.toString());
		return "";
	}
	
	@RequestMapping("/controller/vehicles")
	public @ResponseBody ListWrapper getAllVehicles(@RequestParam("page") int page, @RequestParam("per_page") int pageSize){
		List<Vehicle> partialPage = new ArrayList<Vehicle>();
		int end = (page * pageSize);
		int start = (end) - pageSize;
		int totalPages = roundUp(allVehicle.size(),  pageSize);
		
		if(end > allVehicle.size())
			end = allVehicle.size();
		if(start < allVehicle.size())
			partialPage = allVehicle.subList(start, end);
		
		
		ListWrapper listWrapper = new ListWrapper();
		listWrapper.setVehicles(partialPage);
		listWrapper.setTotalRecords(allVehicle.size());
		listWrapper.setLastPage(totalPages);
		return listWrapper;
	}
	
	private  int roundUp(int num, int divisor) {
	    return (num + divisor - 1) / divisor;
	}
	
	public class ListWrapper{
		private List<Vehicle> vehicles;
		private int totalRecords;
		private int lastPage;
		
		public List<Vehicle> getVehicles() {
			return vehicles;
		}

		public void setVehicles(List<Vehicle> vehicles) {
			this.vehicles = vehicles;
		}

		public int getTotalRecords() {
			return totalRecords;
		}

		public void setTotalRecords(int totalRecords) {
			this.totalRecords = totalRecords;
		}

		public int getLastPage() {
			return lastPage;
		}

		public void setLastPage(int lastPage) {
			this.lastPage = lastPage;
		}
		
		
	}
	
}

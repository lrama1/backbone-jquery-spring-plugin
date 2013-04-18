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
		vehicle1.setVin("1F2221111");
		vehicle1.setModel("tuarus");
		allVehicle.add(vehicle1);
		
		Vehicle vehicle2 = new Vehicle();		
		vehicle2.setVin("8F59999");
		vehicle2.setModel("corolla");
		allVehicle.add(vehicle2);
		
		Vehicle vehicle3 = new Vehicle();		
		vehicle3.setVin("666666");
		vehicle3.setModel("hummer");
		allVehicle.add(vehicle3);
		
		Vehicle vehicle4 = new Vehicle();		
		vehicle4.setVin("6666661");
		vehicle4.setModel("hummer");
		allVehicle.add(vehicle4);
		
		Vehicle vehicle5 = new Vehicle();		
		vehicle5.setVin("6666662");
		vehicle5.setModel("hummer");
		allVehicle.add(vehicle5);
		
		Vehicle vehicle6 = new Vehicle();		
		vehicle6.setVin("6666663");
		vehicle6.setModel("hummer");
		allVehicle.add(vehicle6);
		
		Vehicle vehicle7 = new Vehicle();		
		vehicle7.setVin("6666664");
		vehicle7.setModel("hummer");
		allVehicle.add(vehicle7);
		
		Vehicle vehicle8 = new Vehicle();		
		vehicle8.setVin("6666665");
		vehicle8.setModel("hummer");
		allVehicle.add(vehicle8);
		
		Vehicle vehicle9 = new Vehicle();		
		vehicle9.setVin("66666676");
		vehicle9.setModel("hummer");
		allVehicle.add(vehicle9);
		
		Vehicle vehicle10 = new Vehicle();		
		vehicle10.setVin("66666676xxx");
		vehicle10.setModel("hummer");
		allVehicle.add(vehicle10);
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
		int end = (page * pageSize) -1;
		int start = (end +1) - pageSize;
		
		if(end > allVehicle.size())
			end = allVehicle.size() -1;
		if(start < allVehicle.size() -1)
			partialPage = allVehicle.subList(start, end);
		
		
		ListWrapper listWrapper = new ListWrapper();
		listWrapper.setVehicles(partialPage);
		listWrapper.setTotalRecords(allVehicle.size());
		return listWrapper;
	}
	
	public class ListWrapper{
		private List<Vehicle> vehicles;
		private int totalRecords;
		
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
		
		
	}
	
}

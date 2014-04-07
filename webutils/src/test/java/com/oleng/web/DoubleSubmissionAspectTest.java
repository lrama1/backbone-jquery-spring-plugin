package com.oleng.web;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

import org.junit.Test;

public class DoubleSubmissionAspectTest {

	DoubleSubmissionAspect doubleSubmissionAspect = new DoubleSubmissionAspect();
	
	class Owner{
		private String name;
		private String title;			
		
		public Owner(String name, String title) {
			super();
			this.name = name;
			this.title = title;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}			
	}
	
	class Account{
		private Integer accountId;
		private String accountName;
		private String address;
		private List<String> locations = new ArrayList<String>();
		private List<Owner> owners = new ArrayList<Owner>();
		private String[] metas = {"bb", "xx"};
		private Date dateStarted;
		
		public Integer getAccountId() {
			return accountId;
		}
		public void setAccountId(Integer accountId) {
			this.accountId = accountId;
		}
		public String getAccountName() {
			return accountName;
		}
		public void setAccountName(String accountName) {
			this.accountName = accountName;
		}
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public List<String> getLocations() {
			return locations;
		}
		public void setLocations(List<String> locations) {
			this.locations = locations;
		}
		public List<Owner> getOwners() {
			return owners;
		}
		public void setOwners(List<Owner> owners) {
			this.owners = owners;
		}
		public String[] getMetas() {
			return metas;
		}
		public void setMetas(String[] metas) {
			this.metas = metas;
		}
		public Date getDateStarted() {
			return dateStarted;
		}
		public void setDateStarted(Date dateStarted) {
			this.dateStarted = dateStarted;
		}		
	}
	
	@Test
	public void testFirst(){
		DoubleSubmissionAspect aspect = new DoubleSubmissionAspect();		
		
		Account account = new Account();
		account.setAccountId(9900);
		account.setAccountName("Axl Rama");
		account.setAddress("test");
		account.getLocations().add("hey you");
		account.getLocations().add("yea you");
		Calendar calendar = Calendar.getInstance();
		calendar.set(2014, 01, 01, 11, 30, 30);
		account.setDateStarted(calendar.getTime());
		
		Owner owner = new Owner("ols", "mr");
		account.getOwners().add(owner);
		
		try {
			String concatVals = aspect.concatFields(new StringWriter(), account);
			//System.out.println(concatVals);
			assertEquals("bbxxmrolsSat Feb 01 11:30:30 EST 2014hey youyea youtestAxl Rama9900", concatVals);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

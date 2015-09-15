package org.olengski.web.security;

import static org.junit.Assert.*;

import org.junit.Test;

public class XSSAspectTest {

	public class TestPayload{
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		
	}
	
	@Test
	public void testEncode(){
		TestPayload payload = new TestPayload();
		payload.setName("<script>alert('testing sss');</script>");
		
		XSSAspect aspect = new XSSAspect();
		try {
			aspect.encodeFields(payload);
			assertEquals("", payload.getName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

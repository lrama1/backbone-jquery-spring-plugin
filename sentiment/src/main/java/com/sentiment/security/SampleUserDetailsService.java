package com.sentiment.security;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.ArrayList;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("sampleUserDetailsService")
public class SampleUserDetailsService implements UserDetailsService {

	public UserDetails loadUserByUsername(String userId)
			throws UsernameNotFoundException {

		/*
			This is just a sample implementation of how you would authenticate and obtain
			authorization roles for a user.  Make sure to replace the following code
			with your own implementation.
			A simple flow would be is to:
			1.  find the userId in your datastore (whether DB or LDAP or something else)
			2.  Instantiate User with the attributes from the datastore
			3.  loop thru the roles found in the datastore and add them as 'Granted Authorities'
			     to the User.
		 */
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		//User user = new User(userId, "test", authorities);		
		//return user;
		SampleUserDetails userDetails = new SampleUserDetails(userId,
				"testing", "SaMpLeToKeN", authorities);
		return userDetails;
	}

}

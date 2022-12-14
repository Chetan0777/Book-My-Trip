package com.app.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.exception.CustomerException;
import com.app.model.CurrentUserSession;
import com.app.model.Customer;
import com.app.model.CustomerLogin;
import com.app.repository.CurrentUserSessiondDao;
import com.app.repository.CustomerDao;
import com.app.repository.CustomerLoginDao;

@Service
public class CustomerLoginServiceImpl implements CustomerLoginService{
	
	@Autowired
	private CustomerLoginDao cld;
	
	@Autowired
	private CustomerDao cd;

	@Autowired
	private CurrentUserSessiondDao cusd;

	@Override
	public String Customerlogin(CustomerLogin al) throws CustomerException {
		
		Customer customer= cd.findById(al.getUserId()).orElseThrow(()->new CustomerException("Customer not found"));
		if(!customer.getUsername().equals(al.getCustomerName())) {
			 throw new CustomerException("Incorrect username");
		}
		if(!customer.getPassword().equals(al.getPassword())) {
			throw new CustomerException("Incorrect Password");			
		}
		
		Optional<CurrentUserSession> cu=cusd.findByuserId(al.getUserId());
		if(cu.isPresent()) {
			throw new CustomerException("User Alredy Loged in");	
		}
		
		
		CurrentUserSession cus= new CurrentUserSession();
		RandomNumservice rns=new RandomNumservice();
		cus.setUserId(al.getUserId());
		cus.setLocalDateTime(LocalDateTime.now());
		cus.setUuid(rns.RandomNumber());
		
		cld.save(al);
		
		CurrentUserSession current=	cusd.save(cus);
		
		return current.toString();
	}

	@Override
	public String Customerlogout(String key) throws CustomerException {
		
		CurrentUserSession cus=cusd.findByuuid(key).orElseThrow(()->new CustomerException("Please Enter Right key"));
		
		CustomerLogin cu=cld.findById(cus.getUserId()).orElseThrow(()->new CustomerException("Customer Not Availble"));
		
		cld.delete(cu);
		
		cusd.delete(cus);
				
		return "Logout successfully";
	}

}

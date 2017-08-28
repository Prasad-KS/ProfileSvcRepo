package com.prasad.examples.jersey;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Profile 
{
	private String first_name;
	private String last_name;
	private String email;
	private String phone;
	
	public Profile() 
	{
	}
	
	public Profile(String first_name, String last_name, String email, String phone) 
	{
		this.first_name = first_name;
		this.setLast_name(last_name);
		this.setEmail(email);
		this.setPhone(phone);
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
}
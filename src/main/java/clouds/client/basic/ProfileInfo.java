package clouds.client.basic;

import xdi2.core.xri3.XDI3Segment;

public class ProfileInfo implements PersonalCloudEntity {

	private String email;
	private String phone;
	
	public XDI3Segment getAddress(PersonalCloud pc) {
		
		return pc.getCloudNumber(); 
	}

	//public ValueObject zip;
	
	public String getEmail() {

		return this.email;
	}

	public void setEmail(String email) {

		this.email = email;
	}

	public String getPhone() {

		return this.phone;
	}

	public void setPhone(String phone) {

		this.phone = phone;
	}

	@Override
	public String toString() {

		return null;
	}
}

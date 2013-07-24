package clouds.client.basic;

import xdi2.core.xri3.XDI3Segment;

public class ContactInfo implements PersonalCloudEntity {

	private String id;
	private XDI3Segment cloudName;
	private XDI3Segment cloudNumber;
	private String xdiEndpoint;
	private String email;
	private String phone;

	public XDI3Segment getAddress(PersonalCloud pc) {
		
		return cloudNumber; 
	}

	public String getId() {

		return this.id;
	}

	public void setId(String id) {

		this.id = id;
	}

	public XDI3Segment getCloudName() {

		return this.cloudName;
	}

	public void setCloudName(XDI3Segment cloudName) {

		this.cloudName = cloudName;
	}

	public XDI3Segment getCloudNumber() {

		return this.cloudNumber;
	}

	public void setCloudNumber(XDI3Segment cloudNumber) {

		this.cloudNumber = cloudNumber;
	}

	public String getXdiEndpoint() {

		return this.xdiEndpoint;
	}

	public void setXdiEndpoint(String xdiEndpoint) {

		this.xdiEndpoint = xdiEndpoint;
	}

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

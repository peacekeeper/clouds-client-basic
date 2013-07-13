package clouds.client.basic;

public class ProfileInfo implements PersonalCloudEntity {

	private String email;
	private String phone;

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

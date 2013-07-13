package clouds.client.basic;

import xdi2.core.xri3.XDI3Segment;

public class PersonalCloud {

	public static XDI3Segment XRI_S_DEFAULT_LINKCONTRACT = XDI3Segment.create("$do");
	
	/*
	 * factory methods for opening personal clouds
	 */

	public static PersonalCloud open(XDI3Segment cloudNameOrCloudNumber, String secretToken, XDI3Segment linkContractAddress) {

		return null;
	}

	public static PersonalCloud open(XDI3Segment cloudNameOrCloudNumber, XDI3Segment linkContractAddress) {

		return null;
	}

	/*
	 * profile info
	 */

	public void saveProfileInfo(ProfileInfo profileInfo) {

	}

	public ProfileInfo getProfileInfo() {

		return null;
	}

	/*
	 * contact info
	 */

	public void saveContactInfo(XDI3Segment cloudNameOrCloudNumber, ContactInfo contactInfo) {

	}

	public ContactInfo getContactInfo(XDI3Segment cloudNameOrCloudNumber) {

		return null;
	}

	public ContactInfo findContactInfoById(String id) {

		return null;
	}

	public ContactInfo findContactInfoByEmail(String email) {

		return null;
	}

	/*
	 * access control
	 */

	/**
	 * 
	 * @param entity The entity (e.g. ProfileInfo, ContactInfo, etc.) to allow access to
	 * @param permissionXris The allowed XDI operation(s), e.g. $get, $set, $del. If null, no access is allowed.
	 * @param assignees The Cloud Name(s) or Cloud Number(s) of the assigned people/organizations. If null, allow public access.
	 * @return Address of the link contract that allows this access.
	 */
	public String allowAccess(PersonalCloudEntity entity, XDI3Segment[] permissionXris, XDI3Segment[] assignees) {

		return null;
	}
}

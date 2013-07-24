package clouds.client.basic.example;

import xdi2.core.xri3.XDI3Segment;
import clouds.client.basic.ContactInfo;
import clouds.client.basic.PersonalCloud;
import clouds.client.basic.ProfileInfo;

public class Test {

	public static void testMyOwnPersonalCloud() {

		// open my own personal cloud

		PersonalCloud cloud = PersonalCloud.open(XDI3Segment.create("=markus"), "s3cr3t", null,"");

		// store my profile info

		ProfileInfo profileInfo = new ProfileInfo();
		profileInfo.setEmail("markus.sabadello@gmail.com");
		profileInfo.setPhone("+43 664 3154848");

		cloud.saveProfileInfo(profileInfo);

		// store other people's contact info

		ContactInfo contactInfoAnimesh = new ContactInfo();
		contactInfoAnimesh.setCloudName(XDI3Segment.create("=animesh"));
		contactInfoAnimesh.setEmail("animesh.chowdhury@neustar.biz");

		cloud.saveContactInfo(XDI3Segment.create("=animesh"), contactInfoAnimesh);

		// look up someone's contact info

		ContactInfo contactInfoWilliam = cloud.findContactInfoById("william");
	}

	public static void testOnOtherPersonalCloudWithDiscovery() {

		// open someone else's personal cloud

		PersonalCloud cloud = PersonalCloud.open(XDI3Segment.create("=animesh"), null,"");

		// get profile info

		ProfileInfo profileInfoAnimesh = cloud.getProfileInfo();
	}
}

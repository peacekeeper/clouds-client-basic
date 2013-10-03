package clouds.client.basic.example;

import java.util.ArrayList;

import xdi2.core.Graph;
import xdi2.core.constants.XDIConstants;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.xri3.XDI3Segment;
import clouds.client.basic.ContactInfo;
import clouds.client.basic.PCAttribute;
import clouds.client.basic.PCAttributeCollection;
import clouds.client.basic.PDSXElement;
import clouds.client.basic.PDSXElementTemplate;
import clouds.client.basic.PDSXEntity;
import clouds.client.basic.PersonalCloud;
import clouds.client.basic.ProfileInfo;

public class Test {

	public static void testSaveProfile(String name , String passwd, String email , String phone) {
		// open my own personal cloud

		PersonalCloud cloud = PersonalCloud.open(XDI3Segment.create(name), passwd, PersonalCloud.XRI_S_DEFAULT_LINKCONTRACT,"","");

		// store my profile info

		ProfileInfo profileInfo = new ProfileInfo();
		profileInfo.setEmail(email);
		profileInfo.setPhone(phone);

		cloud.saveProfileInfo(profileInfo);		
	}
	public static void testMyOwnPersonalCloud(String name , String passwd) {

		// open my own personal cloud

		PersonalCloud cloud = PersonalCloud.open(XDI3Segment.create(name), passwd, PersonalCloud.XRI_S_DEFAULT_LINKCONTRACT,"","");

		// store my profile info

//		ProfileInfo profileInfo = new ProfileInfo();
//		profileInfo.setEmail("markus.sabadello@gmail.com");
//		profileInfo.setPhone("+43 664 3154848");
//
//		cloud.saveProfileInfo(profileInfo);

		// store other people's contact info

//		ContactInfo contactInfoAnimesh = new ContactInfo();
//		contactInfoAnimesh.setCloudName(XDI3Segment.create("=animesh"));
//		contactInfoAnimesh.setEmail("animesh.chowdhury@neustar.biz");
//
//		cloud.saveContactInfo(XDI3Segment.create("=animesh"), contactInfoAnimesh);

		// look up someone's contact info

//		ContactInfo contactInfoWilliam = cloud.findContactInfoById("william");
		cloud.getWholeGraph();
	}

	public static void testOnOtherPersonalCloudWithDiscovery(String name) {

		PersonalCloud pc1 = PersonalCloud.open(
				 XDI3Segment.create("=dev.animesh"), "animesh123",
				 PersonalCloud.XRI_S_DEFAULT_LINKCONTRACT, "","");
		// open someone else's personal cloud
		 PersonalCloud pc_markus = PersonalCloud.open(
		 XDI3Segment.create(name),pc1.getCloudNumber(),
		 XDI3Segment.create(pc1.getCloudNumber().toString() +"$do"), "");
		 

	}
	
	public static void testSaveAndGet(){
		
		PersonalCloud pc1 = PersonalCloud.open(
				 XDI3Segment.create("=dev.animesh"), "animesh123",
				 PersonalCloud.XRI_S_DEFAULT_LINKCONTRACT, "","");
		
		 PCAttributeCollection todoList = new PCAttributeCollection("TODO");
		 PCAttribute task1 = new PCAttribute("Task1",
		 "Find out meaning of life", todoList);
		 PCAttribute task2 = new PCAttribute("Task2",
		 "Tell everyone that you've found out the meaning of life", todoList);
		 pc1.save(todoList);
		
		 PCAttributeCollection gadgets = new PCAttributeCollection("home.devices");
		 PCAttribute comp1 = new PCAttribute("device1",
		 "Animesh's desnktop", gadgets);
		 PCAttribute comp2 = new PCAttribute("device2",
		 "Macbook Air", gadgets);
		 PCAttribute comp3 = new PCAttribute("device3",
				 "Trina's iPad", gadgets);
		 pc1.save(gadgets);
		PCAttributeCollection todoListR = new PCAttributeCollection("TODO");
		PCAttribute tsk1 = pc1.readAttr(todoListR, "Task1");
		System.out.println("Task1 : " + tsk1.getValue());

		
	}
	public static void testAccessGranting(){
		PersonalCloud pc1 = PersonalCloud.open(
				 XDI3Segment.create("=dev.animesh"), "animesh123",
				 PersonalCloud.XRI_S_DEFAULT_LINKCONTRACT, "","");

				ProfileInfo pc1Prof = new ProfileInfo();
				 pc1Prof.setEmail("animesh.chowdhury@neustar.biz");
				 pc1Prof.setPhone("1-240-620-4205");
				 pc1.saveProfileInfo(pc1Prof);
				pc1.allowAccess(pc1Prof, XDILinkContractConstants.XRI_S_GET,
				XDI3Segment.create("=markus"));
				Graph pc1Graph = pc1.getWholeGraph();
				
				// open someone else's personal cloud
//				 PersonalCloud pc_markus = PersonalCloud.open(
//				 XDI3Segment.create("=markus"),pc1.getCloudNumber(),
//				 XDI3Segment.create(pc1.getCloudNumber().toString() +"$do"), "");				
//				
//				System.out.println(pc_markus.getProfileInfo().getPhone());


				// pc1.allowAccess(todoList, XDILinkContractConstants.XRI_S_GET,
				// XDI3Segment.create("=markus"));


	}
	public static void testAccessRemoval(){
		
		PersonalCloud pc1 = PersonalCloud.open(
				 XDI3Segment.create("=dev.animesh"), "animesh123",
				 PersonalCloud.XRI_S_DEFAULT_LINKCONTRACT, "","");

				ProfileInfo pc1Prof = new ProfileInfo();
				 pc1.removeAccess( null,XDI3Segment.create("=markus"));
				 Graph pc1Graph = pc1.getWholeGraph();		
		
	}
	public static void testSharedDataAccess() {
		PersonalCloud pc1 = PersonalCloud.open(
				 XDI3Segment.create("=markus"), "markus",
				 PersonalCloud.XRI_S_DEFAULT_LINKCONTRACT, "","");
		PersonalCloud pc2 = PersonalCloud.open(
				 XDI3Segment.create("=dev.animesh"), pc1.getCloudNumber(),
				 PersonalCloud.XRI_S_DEFAULT_LINKCONTRACT, "");
		String linkContract = pc1.getCloudNumber().toString() + "$do";
		pc2.setLinkContractAddress(XDI3Segment.create(linkContract));
		System.out.println("Shared PC's phone:" + pc2.getProfileInfo().getPhone());
		
	}
	public static void getAllCollections(){
		PersonalCloud pc1 = PersonalCloud.open(
				 XDI3Segment.create("=dev.animesh"), "animesh123",
				 PersonalCloud.XRI_S_DEFAULT_LINKCONTRACT, "","");
		ArrayList<PCAttributeCollection> allColls = pc1.geAllCollections();
	}
	public static void testPDSXOps(){
		PersonalCloud pc1 = PersonalCloud.open(
				 XDI3Segment.create("=dev.animesh"), "animesh123",
				 PersonalCloud.XRI_S_DEFAULT_LINKCONTRACT, "","");
		PDSXElementTemplate profileNameTemplate = new PDSXElementTemplate("myProfileName","Name", true, "text", "What is your name?");
		PDSXElementTemplate profileEmailTemplate = new PDSXElementTemplate("myProfileEmail","Email", true, "text", "Your home email address");
		PDSXEntity.addTemplate("Person",profileNameTemplate);
		PDSXEntity.addTemplate("Person",profileEmailTemplate);
		
		PDSXEntity ako = PDSXEntity.get(pc1, "Person", "ako");
		
		PDSXEntity alexContact = new PDSXEntity("Person", "Contact information for a person", "ako");	
		
		PDSXElement alexName = new PDSXElement(alexContact, profileNameTemplate, "Alex Olson");
		PDSXElement alexEmail = new PDSXElement(alexContact, profileEmailTemplate, "ako@nynetx.com");
		
		alexContact.save(pc1);
		
		PDSXEntity markusContact = new PDSXEntity("Person", "Contact information for a person", "markus");		
		PDSXElement markusName = new PDSXElement(markusContact, profileNameTemplate, "Markus Sabadello");
		PDSXElement markusEmail = new PDSXElement(markusContact, profileEmailTemplate, "markus.sabadello.@gmail.com");
		
		markusContact.save(pc1);
		
		PDSXEntity person1 = PDSXEntity.get(pc1, "Person", "ako");
		System.out.println(person1.toString());
		//PDSXEntity.get(pc1, "Person", 1);
	}
	public static void testDefaultLCs(){
		PersonalCloud pc1 = PersonalCloud.open(
				 XDI3Segment.create("=dev.animesh3"), "animesh123",
				 PersonalCloud.XRI_S_DEFAULT_LINKCONTRACT, "","");
		pc1.createDefaultLinkContracts();
		pc1.getWholeGraph();
	}
	public static void testRelationships(){
		PersonalCloud pc_animesh = PersonalCloud.open(
				 XDI3Segment.create("=dev.animesh3"), "animesh123",
				 PersonalCloud.XRI_S_DEFAULT_LINKCONTRACT, "","");
		PersonalCloud pc_markus = PersonalCloud.open(
				 XDI3Segment.create("=markus"),pc_animesh.getCloudNumber(),
				 XDI3Segment.create("$public$do"), "");
		//pc_animesh.createRelationship(pc_markus, XDI3Segment.create("+friend"),XDI3Segment.create("+friend"));
		
//		PDSXElementTemplate profileNameTemplate = new PDSXElementTemplate("myProfileName","Name", true, "text", "What is your name?");
//		PDSXElementTemplate profileEmailTemplate = new PDSXElementTemplate("myProfileEmail","Email", true, "text", "Your home email address");
//		PDSXEntity.addTemplate("Person",profileNameTemplate);
//		PDSXEntity.addTemplate("Person",profileEmailTemplate);
//		
//		
//		PDSXEntity trungContact = new PDSXEntity("Person", "Contact information for a person", "trung");	
//		
//		PDSXElement trungName = new PDSXElement(trungContact, profileNameTemplate, "Trung Tran");
//		PDSXElement trungEmail = new PDSXElement(trungContact, profileEmailTemplate, "trung.tran@neustar.biz");
//		trungContact.save(pc_animesh);
//		
//		PDSXEntity trung = PDSXEntity.get(pc_animesh, "Person", "trung");
		//pc_animesh.allowAccessToRelationship(XDI3Segment.create(pc_animesh.getCloudNumber().toString() + "<+email>&"),null,XDI3Segment.create("$get"), XDI3Segment.create("+friend"),XDI3Segment.create("+friend"),XDI3Segment.create("=markus"));
		pc_animesh.allowAccessToRelationship(XDI3Segment.create("=animesh" + "<+personal_email>&"),XDI3Segment.create(pc_animesh.getCloudNumber().toString() + "<+email>&"),XDI3Segment.create("$get"), XDI3Segment.create("+friend"),XDI3Segment.create("+friend"),XDI3Segment.create("=markus"));
		
		pc_animesh.getWholeGraph();
		PersonalCloud pc_markus2 = PersonalCloud.open(
				 XDI3Segment.create("=markus"), "markus",
				 PersonalCloud.XRI_S_DEFAULT_LINKCONTRACT, "","");
		
		PersonalCloud pc_animesh2 = PersonalCloud.open(
				 XDI3Segment.create("=dev.animesh3"),pc_markus2.getCloudNumber(),
				 XDI3Segment.create("$public$do"), "");
		//pc_animesh2.getPCEntity(XDI3Segment.create(pc_animesh2.getCloudNumber().toString() + "[+Person]*trung"), XDI3Segment.create(pc_animesh2.getCloudNumber().toString() + "+friend$do" ));
		//pc_markus.getPCEntity(XDI3Segment.create(pc_animesh2.getCloudNumber().toString() + "<+email>&"), XDI3Segment.create(pc_animesh2.getCloudNumber().toString() + "+friend$do" ),pc_animesh2);
		pc_markus2.getPCEntity(XDI3Segment.create("=animesh" + "<+personal_email>&"), XDI3Segment.create(pc_animesh2.getCloudNumber().toString() + "+friend$do" ),pc_animesh2);
		
	}
	public static void testDeleteNode(){
		PersonalCloud pc_animesh = PersonalCloud.open(
				 XDI3Segment.create("=dev.animesh"), "animesh123",
				 PersonalCloud.XRI_S_DEFAULT_LINKCONTRACT, "","");
		pc_animesh.deleteNodeTree(XDI3Segment.create("[=]!:uuid:0c5525d0-2744-ecf4-0c55-25d02744ecf4+friend"));
		pc_animesh.deleteNodeTree(XDI3Segment.create("[=]!:uuid:0c5525d0-2744-ecf4-0c55-25d02744ecf4+family"));
		pc_animesh.deleteNodeTree(XDI3Segment.create("[=]!:uuid:0c5525d0-2744-ecf4-0c55-25d02744ecf4+coworker"));
		pc_animesh.deleteNodeTree(XDI3Segment.create("[=]!:uuid:0c5525d0-2744-ecf4-0c55-25d02744ecf4[+Person]*ako"));
		pc_animesh.deleteNodeTree(XDI3Segment.create("[=]!:uuid:0c5525d0-2744-ecf4-0c55-25d02744ecf4[+Person]*markus"));
		pc_animesh.deleteNodeTree(XDI3Segment.create("[=]!:uuid:0c5525d0-2744-ecf4-0c55-25d02744ecf4[+Person]*les"));
	}
	public static void main(String args[]) {
		 
		//Test.testAccessGranting();
		//Test.testSharedDataAccess();
//		Test.testAccessRemoval();
		//Test.testSharedDataAccess();
		//Test.getAllCollections();
		//Test.testOnOtherPersonalCloudWithDiscovery("=dev.ako");
	//Test.testMyOwnPersonalCloud("=dev.ako", "ga3169723");
		//Test.testSaveProfile("=dev.ako", "ga3169723", "ako@kynetx.com", "1234567890");
		//Test.testPDSXOps();
//		PersonalCloud pc_peer3 = PersonalCloud.open(
//				 XDI3Segment.create("=dev.animesh3"),XDI3Segment.create("[=]!:uuid:91f28153-f600-ae24-91f2-8153f600ae24"),
//				 XDI3Segment.create("$public$do"), "");
//		PersonalCloud pc_peer2 = PersonalCloud.open(
//				 XDI3Segment.create("[=]!:uuid:17864069-1ad0-8bfa-1786-40691ad08bfa"),XDI3Segment.create("[=]!:uuid:91f28153-f600-ae24-91f2-8153f600ae24"),
//				 XDI3Segment.create("$public$do"), "");
//		PersonalCloud pc_peer = PersonalCloud.open(
//				 XDI3Segment.create("[=]!:uuid:91f28153-f600-ae24-91f2-8153f600ae24"),XDI3Segment.create("[=]!:uuid:17864069-1ad0-8bfa-1786-40691ad08bfa"),
//				 XDI3Segment.create("$public$do"), "");
		
		
//		PersonalCloud pc_markus = PersonalCloud.open(
//				 XDI3Segment.create("=markus"), "markus",
//				 PersonalCloud.XRI_S_DEFAULT_LINKCONTRACT, "");
//		PersonalCloud pc_animesh = PersonalCloud.open(
//				 XDI3Segment.create("=dev.animesh3"),pc_markus.getCloudNumber(),
//				 XDI3Segment.create("$public$do"), "");
//		PersonalCloud pc_animesh2 = PersonalCloud.open(
//				 XDI3Segment.create("=dev.animesh3"), "animesh123",
//				 PersonalCloud.XRI_S_DEFAULT_LINKCONTRACT, "");
//		ProfileInfo profileInfo = new ProfileInfo();
//		profileInfo.setEmail("animesh.chowdhury@gmail.com");
//		profileInfo.setPhone("703-724-7686");
//
//		pc1.saveProfileInfo(profileInfo);
		//pc_animesh.getWholeGraph();
		//Test.testDeleteNode();
//		Test.testDefaultLCs();
		//Test.testRelationships();
//		String cn = pc_animesh.getCloudNumber().toString();
//		
//		String reqURI = pc_markus.requestForAccess(XDI3Segment.create(cn+"+home<+phone>&"), XDI3Segment.create("$get"), XDI3Segment.create("+friend"), XDI3Segment.create("+friend"), pc_animesh);
//		
//		
//		pc_animesh2.approveAccess(XDI3Segment.create(reqURI),null);
		PersonalCloud pc = PersonalCloud.open(XDI3Segment.create("=demo2"), "demo2", PersonalCloud.XRI_S_DEFAULT_LINKCONTRACT, "","");
		pc.getDataBucket("work");
		
		
	}
}

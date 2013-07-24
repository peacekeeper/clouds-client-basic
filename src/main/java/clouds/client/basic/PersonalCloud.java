package clouds.client.basic;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Hashtable;

import xdi2.client.XDIClient;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.http.XDIHttpClient;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.impl.memory.MemoryGraph;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.discovery.XDIDiscovery;
import xdi2.discovery.XDIDiscoveryResult;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;

public class PersonalCloud {

	public static XDI3Segment XRI_S_DEFAULT_LINKCONTRACT = XDI3Segment
			.create("$do");

	public static String DEFAULT_REGISTRY_URI = "http://mycloud.neustar.biz:12220/";
	private String secretToken;
	private XDI3Segment linkContractAddress;
	private XDI3Segment cloudNumber;
	private String registryURI;
	private String cloudEndpointURI;
	private ProfileInfo profileInfo;
	private Hashtable<String,ContactInfo> addressBook = new Hashtable<String, ContactInfo>();

	/*
	 * factory methods for opening personal clouds
	 */

	public static PersonalCloud open(XDI3Segment cloudNameOrCloudNumber,
			String secretToken, XDI3Segment linkContractAddress, String regURI) {

		// like My Cloud Sign-in in clouds.projectdanbe.org
		// 1. discover the endpoint
		// 2. Load profile if available
		PersonalCloud pc = new PersonalCloud();
		XDIHttpClient httpClient = null;
		if (regURI != null && regURI.length() > 0) {
			httpClient = new XDIHttpClient(regURI);
			pc.registryURI = regURI;
		} else {
			httpClient = new XDIHttpClient(DEFAULT_REGISTRY_URI);
			pc.registryURI = DEFAULT_REGISTRY_URI;
		}
		XDIDiscovery discovery = new XDIDiscovery();
		discovery.setRegistryXdiClient(httpClient);
		try {
			XDIDiscoveryResult discoveryResult = discovery
					.discoverFromXri(cloudNameOrCloudNumber);
			pc.cloudNumber = discoveryResult.getCloudNumber();
			pc.cloudEndpointURI = discoveryResult.getEndpointUri();
		} catch (Xdi2ClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		pc.linkContractAddress = linkContractAddress;
		pc.secretToken = secretToken;
		// System.out.println(pc.toString());
		pc.profileInfo = new ProfileInfo();

		// prepare XDI client to get profile info

		XDIClient xdiClient = new XDIHttpClient(pc.cloudEndpointURI);

		// prepare message envelope for getting email

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.getMessage(pc.cloudNumber, true);
		message.setLinkContractXri(linkContractAddress);
		message.setSecretToken(secretToken);
		message.setToAddress(XDI3Segment.create(XdiPeerRoot
				.createPeerRootArcXri(pc.cloudNumber)));

		message.createGetOperation(XDI3Segment.create(pc.cloudNumber.toString()
				+ "<+email>&"));

		//System.out.println("Message :\n" + messageEnvelope + "\n");
		try {
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(messageEnvelope.getGraph(), System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// send the message

		MessageResult messageResult;

		try {

			messageResult = xdiClient.send(messageEnvelope, null);
			//System.out.println(messageResult);
			MemoryGraph response = (MemoryGraph) messageResult.getGraph();
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(response, System.out);
			Literal emailLiteral = response.getDeepLiteral(XDI3Segment
					.create(pc.cloudNumber.toString() + "<+email>&"));
			String email = (emailLiteral == null) ? "" : emailLiteral.getLiteralData();
			pc.profileInfo.setEmail(email);

		} catch (Xdi2ClientException ex) {

			ex.printStackTrace();
		} catch (Exception ex) {

			ex.printStackTrace();
		}

		// prepare message envelope for getting phone

		MessageEnvelope messageEnvelope2 = new MessageEnvelope();
		Message message2 = messageEnvelope2.getMessage(pc.cloudNumber, true);
		message2.setLinkContractXri(linkContractAddress);
		message2.setSecretToken(secretToken);
		message2.setToAddress(XDI3Segment.create(XdiPeerRoot
				.createPeerRootArcXri(pc.cloudNumber)));

		message2.createGetOperation(XDI3Segment.create(pc.cloudNumber.toString()
				+ "+home<+phone>&"));

		//System.out.println("Message :\n" + messageEnvelope2 + "\n");
		try {
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(messageEnvelope2.getGraph(), System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// send the message

		MessageResult messageResult2;

		try {

			messageResult2 = xdiClient.send(messageEnvelope2, null);
			//System.out.println(messageResult2);
			MemoryGraph response = (MemoryGraph) messageResult2.getGraph();
			 XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(response, System.out);
			Literal phoneLiteral = response.getDeepLiteral(XDI3Segment
					.create(pc.cloudNumber.toString() + "+home<+phone>&"));
			String phone = (phoneLiteral == null) ? "" : phoneLiteral.getLiteralData();
			pc.profileInfo.setPhone(phone);

		} catch (Xdi2ClientException ex) {

			ex.printStackTrace();
		} catch (Exception ex) {

			ex.printStackTrace();
		}

		return pc;
	}

	@Override
	public String toString() {

		StringBuffer str = new StringBuffer();
		str.append("\n");
		str.append("CloudNumber\t:\t" + cloudNumber);
		str.append("\n");
		str.append("registryURI\t:\t" + registryURI);
		str.append("\n");
		try {
			str.append("Cloud endpoint URI\t:\t"
					+ URLDecoder.decode(cloudEndpointURI, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			str.append("Cloud endpoint URI\t:\tnull");
			e.printStackTrace();
		}
		str.append("\n");
		str.append("Link Contract Address\t:\t" + linkContractAddress);
		str.append("\n");

		return str.toString();

	}

	public static PersonalCloud open(XDI3Segment cloudNameOrCloudNumber,
			XDI3Segment linkContractAddress, String regURI) {

		// like My Cloud Sign-in in clouds.projectdanbe.org
		// 1. discover the endpoint
		// 2. test if the secret token is correct by sending a test message
		PersonalCloud pc = new PersonalCloud();
		XDIHttpClient httpClient = null;
		if (regURI != null && regURI.length() > 0) {
			httpClient = new XDIHttpClient(regURI);
			pc.registryURI = regURI;
		} else {
			httpClient = new XDIHttpClient(DEFAULT_REGISTRY_URI);
			pc.registryURI = DEFAULT_REGISTRY_URI;
		}
		XDIDiscovery discovery = new XDIDiscovery();
		discovery.setRegistryXdiClient(httpClient);
		try {
			XDIDiscoveryResult discoveryResult = discovery
					.discoverFromXri(cloudNameOrCloudNumber);
			pc.cloudNumber = discoveryResult.getCloudNumber();
			pc.cloudEndpointURI = discoveryResult.getEndpointUri();
		} catch (Xdi2ClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		pc.linkContractAddress = linkContractAddress;
		System.out.println(pc.toString());

		// prepare XDI client to send a $get on ()

		XDIClient xdiClient = new XDIHttpClient(pc.cloudEndpointURI);

		// prepare message envelope

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.getMessage(pc.cloudNumber, true);
		message.setLinkContractXri(linkContractAddress);

		message.setToAddress(XDI3Segment.create(XdiPeerRoot
				.createPeerRootArcXri(pc.cloudNumber)));

		message.createGetOperation(linkContractAddress);
		System.out.println("Message :\n" + messageEnvelope + "\n");

		// send the message

		MessageResult messageResult;

		try {

			messageResult = xdiClient.send(messageEnvelope, null);
			System.out.println(messageResult);

		} catch (Xdi2ClientException ex) {

			ex.printStackTrace();
		} catch (Exception ex) {

			ex.printStackTrace();
		}

		return pc;
	}
	
	public Graph getWholeGraph() {
		
		
		XDIClient xdiClient = new XDIHttpClient(cloudEndpointURI);

		// prepare message envelope for getting email

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.getMessage(cloudNumber, true);
		message.setLinkContractXri(linkContractAddress);
		message.setSecretToken(secretToken);
		message.setToAddress(XDI3Segment.create(XdiPeerRoot
				.createPeerRootArcXri(cloudNumber)));

		message.createGetOperation(XDI3Segment.create("()"));

		//System.out.println("Message :\n" + messageEnvelope + "\n");
		try {
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(messageEnvelope.getGraph(), System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// send the message

		MessageResult messageResult;

		try {

			messageResult = xdiClient.send(messageEnvelope, null);
			//System.out.println(messageResult);
			MemoryGraph response = (MemoryGraph) messageResult.getGraph();
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(response, System.out);
			return response;

		} catch (Xdi2ClientException ex) {

			ex.printStackTrace();
		} catch (Exception ex) {

			ex.printStackTrace();
		}
		
		return null;

	}
	/**
	 * 
	 * @param profileInfo
	 */

	public void saveProfileInfo(ProfileInfo profileInfo) {

		// construct the statements for Profiles's fields

		ArrayList<XDI3Statement> profileXDIStmts = new ArrayList<XDI3Statement>();

		if (profileInfo.getEmail() != null) {
			profileXDIStmts.add(XDI3Statement.create(cloudNumber.toString()
					+ "<+email>&/&/\"" + profileInfo.getEmail() + "\""));
		}
		if (profileInfo.getPhone() != null) {
			profileXDIStmts.add(XDI3Statement.create(cloudNumber.toString()
					+ "+home<+phone>&/&/\"" + profileInfo.getPhone() + "\""));
		}
		// send the message

		// prepare XDI client

		XDIClient xdiClient = new XDIHttpClient(cloudEndpointURI);

		// prepare message envelope

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.getMessage(cloudNumber, true);
		message.setLinkContractXri(linkContractAddress);

		message.setSecretToken(secretToken);

		message.setToAddress(XDI3Segment.create(XdiPeerRoot
				.createPeerRootArcXri(cloudNumber)));
		message.createSetOperation(profileXDIStmts.iterator());

		System.out.println("Message :\n" + messageEnvelope + "\n");

		// send the message

		MessageResult messageResult;

		try {

			messageResult = xdiClient.send(messageEnvelope, null);
			System.out.println(messageResult);

		} catch (Xdi2ClientException ex) {

			ex.printStackTrace();
		} catch (Exception ex) {

			ex.printStackTrace();
		}

		this.profileInfo = profileInfo;

	}

	public ProfileInfo getProfileInfo() {

		return this.profileInfo;
	}

	/*
	 * contact info
	 */

	public void saveContactInfo(XDI3Segment cloudNameOrCloudNumber,
			ContactInfo contactInfo) {
		// construct the statements for Contact's fields
		
		PersonalCloud contactPC = PersonalCloud.open(cloudNameOrCloudNumber,
				XDI3Segment.create("$public$do"), "");
		XDI3Segment contactCN = contactPC.cloudNumber;

		ArrayList<XDI3Statement> contactXDIStmts = new ArrayList<XDI3Statement>();

		if (contactInfo.getEmail() != null) {
			contactXDIStmts.add(XDI3Statement.create(contactCN.toString()
					+ "<+email>&/&/\"" + contactInfo.getEmail() + "\""));
		}
		if (contactInfo.getPhone() != null) {
			contactXDIStmts.add(XDI3Statement.create(contactCN.toString()
					+ "+home<+phone>&/&/\"" + contactInfo.getPhone() + "\""));
		}
		// send the message

		// prepare XDI client

		XDIClient xdiClient = new XDIHttpClient(cloudEndpointURI);

		// prepare message envelope

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.getMessage(cloudNumber, true);
		message.setLinkContractXri(linkContractAddress);

		message.setSecretToken(secretToken);

		message.setToAddress(XDI3Segment.create(XdiPeerRoot
				.createPeerRootArcXri(cloudNumber)));
		message.createSetOperation(contactXDIStmts.iterator());

		System.out.println("Message :\n" + messageEnvelope + "\n");

		// send the message

		MessageResult messageResult;

		try {

			messageResult = xdiClient.send(messageEnvelope, null);
			System.out.println(messageResult);

		} catch (Xdi2ClientException ex) {

			ex.printStackTrace();
		} catch (Exception ex) {

			ex.printStackTrace();
		}

		addressBook.put(contactCN.toString(), contactInfo);


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
	 * @param entity
	 *            The entity (e.g. ProfileInfo, ContactInfo, etc.) to allow
	 *            access to
	 * @param permissionXris
	 *            The allowed XDI operation(s), e.g. $get, $set, $del. If null,
	 *            no access is allowed.
	 * @param assignees
	 *            The Cloud Name(s) or Cloud Number(s) of the assigned
	 *            people/organizations. If null, allow public access.
	 * @return Address of the link contract that allows this access.
	 */
	public void allowAccess(PersonalCloudEntity entity,
			XDI3Segment permissionXri, XDI3Segment assignee) {

		
		PersonalCloud assigneePC = PersonalCloud.open(assignee,
				XDI3Segment.create("$public$do"), "");
		XDI3Segment assigneeCN = assigneePC.cloudNumber;
		
		XDIClient xdiClient = new XDIHttpClient(cloudEndpointURI);

		// prepare message envelope for getting email

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.getMessage(cloudNumber, true);
		message.setLinkContractXri(linkContractAddress);
		message.setSecretToken(secretToken);
		message.setToAddress(XDI3Segment.create(XdiPeerRoot
				.createPeerRootArcXri(cloudNumber)));

		message.createSetOperation(XDI3Statement.create(assigneeCN.toString() + "$do$if$and/$true/({$from}/$is/" + assigneeCN.toString() + ")"));
		message.createSetOperation(XDI3Statement.create(assigneeCN.toString() + "$do/" +  permissionXri.toString() + "/" + entity.getAddress(this)));

		//System.out.println("Message :\n" + messageEnvelope + "\n");
		try {
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(messageEnvelope.getGraph(), System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// send the message

		MessageResult messageResult;

		try {

			messageResult = xdiClient.send(messageEnvelope, null);
			//System.out.println(messageResult);
			MemoryGraph response = (MemoryGraph) messageResult.getGraph();
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(response, System.out);
			

		} catch (Xdi2ClientException ex) {

			ex.printStackTrace();
		} catch (Exception ex) {

			ex.printStackTrace();
		}

		
	}

	public XDI3Segment getLinkContractAddress() {
		return linkContractAddress;
	}

	public XDI3Segment getCloudNumber() {
		return cloudNumber;
	}

	public String getRegistryURI() {
		return registryURI;
	}

	public String getCloudEndpointURI() {
		return cloudEndpointURI;
	}

	public static void main(String args[]) {
		PersonalCloud pc1 = PersonalCloud.open(
				XDI3Segment.create("=dev.animesh"), "animesh123",
				PersonalCloud.XRI_S_DEFAULT_LINKCONTRACT, "");
		
		
//		PersonalCloud pc2 = PersonalCloud.open(XDI3Segment.create("=markus"),
//				XDI3Segment.create("$public$do"), "");

		ProfileInfo pc1Prof = new ProfileInfo();
		pc1Prof.setEmail("animesh.chowdhury@neustar.biz");
		pc1Prof.setPhone("1-240-620-4205");

		pc1.saveProfileInfo(pc1Prof);
		pc1.allowAccess(pc1Prof, XDI3Segment.create("$get"), XDI3Segment.create("=markus"));
		Graph pc1Graph = pc1.getWholeGraph();
		
		ContactInfo ci1 = new ContactInfo();
		ci1.setCloudName(XDI3Segment.create("=markus"));
		ci1.setEmail("markus.sabadello.@gmail.com");
		
		pc1.saveContactInfo(XDI3Segment.create("=markus"), ci1);
		
		//pc1.allowAccess(entity, permissionXris, assignees)
		// System.out.println(pc2);

	}
}

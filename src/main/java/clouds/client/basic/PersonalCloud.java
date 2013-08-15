package clouds.client.basic;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.UUID;

import xdi2.client.XDIClient;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.http.XDIHttpClient;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.impl.memory.MemoryGraph;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.discovery.XDIDiscovery;
import xdi2.discovery.XDIDiscoveryResult;
import xdi2.messaging.GetOperation;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;

public class PersonalCloud {

	public static XDI3Segment XRI_S_DEFAULT_LINKCONTRACT = XDI3Segment
			.create("$do");

	public static String DEFAULT_REGISTRY_URI = "http://mycloud.neustar.biz:12220/";
	private String secretToken = null;
	private XDI3Segment linkContractAddress = null;

	private XDI3Segment cloudNumber = null;
	private XDI3Segment senderCloudNumber = null;
	private String registryURI = null;
	private String cloudEndpointURI = null;
	private ProfileInfo profileInfo = null;
	private Hashtable<String, ContactInfo> addressBook = new Hashtable<String, ContactInfo>();

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
		pc.senderCloudNumber = pc.cloudNumber;
		// System.out.println(pc.toString());
		pc.getProfileInfo();
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
			XDI3Segment senderCN, XDI3Segment linkContractAddress, String regURI) {

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
		pc.senderCloudNumber = senderCN;
		System.out.println(pc.toString());
		// pc.getProfileInfo();
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

		// System.out.println("Message :\n" + messageEnvelope + "\n");
		try {
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(
					messageEnvelope.getGraph(), System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// send the message

		MessageResult messageResult;

		try {

			messageResult = xdiClient.send(messageEnvelope, null);
			// System.out.println(messageResult);
			MemoryGraph response = (MemoryGraph) messageResult.getGraph();
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(response,
					System.out);
			return response;

		} catch (Xdi2ClientException ex) {

			ex.printStackTrace();
		} catch (Exception ex) {

			ex.printStackTrace();
		}

		return null;

	}

	public ArrayList<PCAttributeCollection> geAllCollections() {

		Graph g = this.getWholeGraph();
		ContextNode root = g.getRootContextNode();
		ReadOnlyIterator<Literal> allLiterals = root.getAllLiterals();
		while (allLiterals.hasNext()) {
			Literal lit = allLiterals.next();
			String value = lit.getLiteralData().toString();
			String name = lit.getContextNode().toString();
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

		ProfileInfo profileInfo = new ProfileInfo();

		// prepare XDI client to get profile info

		XDIClient xdiClient = new XDIHttpClient(cloudEndpointURI);

		// prepare message envelope for getting email

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.getMessage(senderCloudNumber, true);
		message.setLinkContractXri(linkContractAddress);
		if (secretToken != null) {
			message.setSecretToken(secretToken);
		}
		message.setToAddress(XDI3Segment.create(XdiPeerRoot
				.createPeerRootArcXri(cloudNumber)));

		message.createGetOperation(XDI3Segment.create(cloudNumber.toString()
				+ "<+email>&"));

		// System.out.println("Message :\n" + messageEnvelope + "\n");
		try {
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(
					messageEnvelope.getGraph(), System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// send the message

		MessageResult messageResult;

		try {

			messageResult = xdiClient.send(messageEnvelope, null);
			// System.out.println(messageResult);
			MemoryGraph response = (MemoryGraph) messageResult.getGraph();
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(response,
					System.out);
			Literal emailLiteral = response.getDeepLiteral(XDI3Segment
					.create(cloudNumber.toString() + "<+email>&"));
			String email = (emailLiteral == null) ? "" : emailLiteral
					.getLiteralData().toString();
			profileInfo.setEmail(email);

		} catch (Xdi2ClientException ex) {

			ex.printStackTrace();
		} catch (Exception ex) {

			ex.printStackTrace();
		}

		// prepare message envelope for getting phone

		MessageEnvelope messageEnvelope2 = new MessageEnvelope();
		Message message2 = messageEnvelope2.getMessage(senderCloudNumber, true);
		message2.setLinkContractXri(linkContractAddress);
		if (secretToken != null) {
			message2.setSecretToken(secretToken);
		}
		message2.setToAddress(XDI3Segment.create(XdiPeerRoot
				.createPeerRootArcXri(cloudNumber)));

		message2.createGetOperation(XDI3Segment.create(cloudNumber.toString()
				+ "+home<+phone>&"));

		// System.out.println("Message :\n" + messageEnvelope2 + "\n");
		try {
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(
					messageEnvelope2.getGraph(), System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// send the message

		MessageResult messageResult2;

		try {

			messageResult2 = xdiClient.send(messageEnvelope2, null);
			// System.out.println(messageResult2);
			MemoryGraph response = (MemoryGraph) messageResult2.getGraph();
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(response,
					System.out);
			Literal phoneLiteral = response.getDeepLiteral(XDI3Segment
					.create(cloudNumber.toString() + "+home<+phone>&"));
			String phone = (phoneLiteral == null) ? "" : phoneLiteral
					.getLiteralData().toString();
			profileInfo.setPhone(phone);

		} catch (Xdi2ClientException ex) {

			ex.printStackTrace();
		} catch (Exception ex) {

			ex.printStackTrace();
		}

		this.profileInfo = profileInfo;
		return profileInfo;
	}

	public MessageResult setXDIStmts(ArrayList<XDI3Statement> XDIStmts) {

		// prepare XDI client

		XDIClient xdiClient = new XDIHttpClient(cloudEndpointURI);

		// prepare message envelope

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.getMessage(cloudNumber, true);
		message.setLinkContractXri(linkContractAddress);

		message.setSecretToken(secretToken);

		message.setToAddress(XDI3Segment.create(XdiPeerRoot
				.createPeerRootArcXri(cloudNumber)));
		message.createSetOperation(XDIStmts.iterator());

		// System.out.println("Message :\n" + messageEnvelope + "\n");
		try {
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(
					messageEnvelope.getGraph(), System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// send the message

		MessageResult messageResult = null;

		try {

			messageResult = xdiClient.send(messageEnvelope, null);
			// System.out.println(messageResult);
			try {
				XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(
						messageResult.getGraph(), System.out);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (Xdi2ClientException ex) {

			ex.printStackTrace();
		} catch (Exception ex) {

			ex.printStackTrace();
		}
		return messageResult;
	}

	public MessageResult getXDIStmts(XDI3Segment query, boolean isDeref) {

		XDIClient xdiClient = new XDIHttpClient(cloudEndpointURI);

		// prepare message envelope

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.getMessage(senderCloudNumber, true);
		message.setLinkContractXri(linkContractAddress);
		if (secretToken != null) {
			message.setSecretToken(secretToken);
		}
		message.setToAddress(XDI3Segment.create(XdiPeerRoot
				.createPeerRootArcXri(cloudNumber)));

		GetOperation getOp = message.createGetOperation(query);
		if (isDeref) {
			getOp.setParameter(XDI3SubSegment.create("$deref"), "true");
		}
		// System.out.println("Message :\n" + messageEnvelope + "\n");
		try {
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(
					messageEnvelope.getGraph(), System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// send the message

		MessageResult messageResult = null;

		try {

			messageResult = xdiClient.send(messageEnvelope, null);
			// System.out.println(messageResult);
			MemoryGraph response = (MemoryGraph) messageResult.getGraph();
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(response,
					System.out);

		} catch (Xdi2ClientException ex) {

			ex.printStackTrace();
		} catch (Exception ex) {

			ex.printStackTrace();
		}
		return messageResult;
	}

	/*
	 * contact info
	 */

	public void saveContactInfo(XDI3Segment cloudNameOrCloudNumber,
			ContactInfo contactInfo) {
		// construct the statements for Contact's fields

		PersonalCloud contactPC = PersonalCloud.open(cloudNameOrCloudNumber,
				cloudNumber, XDI3Segment.create("$public$do"), "");
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

	public PCAttribute readAttr(PCAttributeCollection coll, String attrName) {

		// prepare XDI client to get profile info

		XDIClient xdiClient = new XDIHttpClient(cloudEndpointURI);

		// prepare message envelope

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.getMessage(senderCloudNumber, true);
		message.setLinkContractXri(linkContractAddress);
		if (secretToken != null) {
			message.setSecretToken(secretToken);
		}
		message.setToAddress(XDI3Segment.create(XdiPeerRoot
				.createPeerRootArcXri(cloudNumber)));

		message.createGetOperation(XDI3Segment.create(cloudNumber.toString()
				+ "+" + coll.getName() + "<+" + attrName + ">&"));

		// System.out.println("Message :\n" + messageEnvelope + "\n");
		try {
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(
					messageEnvelope.getGraph(), System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// send the message

		MessageResult messageResult;

		try {

			messageResult = xdiClient.send(messageEnvelope, null);
			// System.out.println(messageResult);
			MemoryGraph response = (MemoryGraph) messageResult.getGraph();
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(response,
					System.out);
			Literal literalValue = response.getDeepLiteral(XDI3Segment
					.create(cloudNumber.toString() + "+" + coll.getName()
							+ "<+" + attrName + ">&"));
			String strVal = (literalValue == null) ? "" : literalValue
					.getLiteralData().toString();
			PCAttribute attr = new PCAttribute(attrName, strVal, coll);
			return attr;

		} catch (Xdi2ClientException ex) {

			ex.printStackTrace();
		} catch (Exception ex) {

			ex.printStackTrace();
		}

		return null;
	}

	public void deleteAttr(PCAttributeCollection coll, String attrName) {

		// prepare XDI client to get profile info

		XDIClient xdiClient = new XDIHttpClient(cloudEndpointURI);

		// prepare message envelope

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.getMessage(senderCloudNumber, true);
		message.setLinkContractXri(linkContractAddress);
		if (secretToken != null) {
			message.setSecretToken(secretToken);
		}
		message.setToAddress(XDI3Segment.create(XdiPeerRoot
				.createPeerRootArcXri(cloudNumber)));

		message.createDelOperation(XDI3Segment.create(cloudNumber.toString()
				+ "+" + coll.getName() + "<+" + attrName + ">&"));

		// System.out.println("Message :\n" + messageEnvelope + "\n");
		try {
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(
					messageEnvelope.getGraph(), System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// send the message

		MessageResult messageResult;

		try {

			messageResult = xdiClient.send(messageEnvelope, null);
			// System.out.println(messageResult);
			MemoryGraph response = (MemoryGraph) messageResult.getGraph();
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(response,
					System.out);

		} catch (Xdi2ClientException ex) {

			ex.printStackTrace();
		} catch (Exception ex) {

			ex.printStackTrace();
		}
		coll.deleteAttribute(attrName);

	}

	public void save(PCAttributeCollection coll) {
		// construct the statements for Profiles's fields

		ArrayList<XDI3Statement> profileXDIStmts = new ArrayList<XDI3Statement>();

		// for all attributes in the collection, create XDI statements
		Hashtable<String, PCAttribute> attrMap = coll.getAttributeMap();
		Iterator<PCAttribute> iter = attrMap.values().iterator();
		while (iter.hasNext()) {
			PCAttribute attr = iter.next();
			if (attr.getValue() != null) {
				XDI3Statement stmt = XDI3Statement.create(attr.getAddress(this)
						.toString() + "/&/\"" + attr.getValue() + "\"");
				profileXDIStmts.add(stmt);
			} else {
				XDI3Statement stmt = XDI3Statement.create(attr.getAddress(this)
						.toString() + "/&/\"" + "\"");
				profileXDIStmts.add(stmt);

			}
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

		try {
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(
					messageEnvelope.getGraph(), System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// System.out.println("Message :\n" + messageEnvelope + "\n");

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

	public void setLinkContractAddress(XDI3Segment linkContractAddress) {
		this.linkContractAddress = linkContractAddress;
	}

	/*
	 * access control
	 */

	/**
	 * 
	 * @param entity
	 *            The entity (e.g. ProfileInfo, ContactInfo, etc.) to allow
	 *            access to
	 * @param permissionXri
	 *            The allowed XDI operation, e.g. $get, $set, $del. If null, no
	 *            access is allowed.
	 * @param assignee
	 *            The Cloud Name or Cloud Number of the assigned
	 *            people/organization. If null, allow public access.
	 */
	public void allowAccess(PersonalCloudEntity entity,
			XDI3Segment permissionXri, XDI3Segment assignee) {

		PersonalCloud assigneePC = PersonalCloud.open(assignee, cloudNumber,
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

		message.createSetOperation(XDI3Statement.create(assigneeCN.toString()
				+ "$do$if$and/$true/({$from}/$is/" + assigneeCN.toString()
				+ ")"));
		message.createSetOperation(XDI3Statement.create(assigneeCN.toString()
				+ "$do/" + permissionXri.toString() + "/"
				+ entity.getAddress(this)));

		// System.out.println("Message :\n" + messageEnvelope + "\n");
		try {
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(
					messageEnvelope.getGraph(), System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// send the message

		MessageResult messageResult;

		try {

			messageResult = xdiClient.send(messageEnvelope, null);
			// System.out.println(messageResult);
			MemoryGraph response = (MemoryGraph) messageResult.getGraph();
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(response,
					System.out);

		} catch (Xdi2ClientException ex) {

			ex.printStackTrace();
		} catch (Exception ex) {

			ex.printStackTrace();
		}

	}

	public void deleteNodeTree(XDI3Segment target) {
		XDIClient xdiClient = new XDIHttpClient(cloudEndpointURI);

		// prepare message envelope for deleting the link contract for the
		// assignee

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.getMessage(cloudNumber, true);
		message.setLinkContractXri(linkContractAddress);
		message.setSecretToken(secretToken);
		message.setToAddress(XDI3Segment.create(XdiPeerRoot
				.createPeerRootArcXri(cloudNumber)));

		// message.createDelOperation(XDI3Statement.create(assigneeCN.toString()
		// + "$do$if$and/$true/({$from}/$is/" + assigneeCN.toString()
		// + ")"));
		message.createDelOperation(target);

		// System.out.println("Message :\n" + messageEnvelope + "\n");
		try {
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(
					messageEnvelope.getGraph(), System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// send the message

		MessageResult messageResult;

		try {

			messageResult = xdiClient.send(messageEnvelope, null);
			// System.out.println(messageResult);
			MemoryGraph response = (MemoryGraph) messageResult.getGraph();
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(response,
					System.out);

		} catch (Xdi2ClientException ex) {

			ex.printStackTrace();
		} catch (Exception ex) {

			ex.printStackTrace();
		}

	}

	public void removeAccess(PersonalCloudEntity entity, XDI3Segment assignee) {
		PersonalCloud assigneePC = PersonalCloud.open(assignee, cloudNumber,
				XDI3Segment.create("$public$do"), "");
		XDI3Segment assigneeCN = assigneePC.cloudNumber;

		XDIClient xdiClient = new XDIHttpClient(cloudEndpointURI);

		// prepare message envelope for deleting the link contract for the
		// assignee

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.getMessage(cloudNumber, true);
		message.setLinkContractXri(linkContractAddress);
		message.setSecretToken(secretToken);
		message.setToAddress(XDI3Segment.create(XdiPeerRoot
				.createPeerRootArcXri(cloudNumber)));

		// message.createDelOperation(XDI3Statement.create(assigneeCN.toString()
		// + "$do$if$and/$true/({$from}/$is/" + assigneeCN.toString()
		// + ")"));
		message.createDelOperation(XDI3Segment.create(assigneeCN.toString()
				+ "$do"));

		// System.out.println("Message :\n" + messageEnvelope + "\n");
		try {
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(
					messageEnvelope.getGraph(), System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// send the message

		MessageResult messageResult;

		try {

			messageResult = xdiClient.send(messageEnvelope, null);
			// System.out.println(messageResult);
			MemoryGraph response = (MemoryGraph) messageResult.getGraph();
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(response,
					System.out);

		} catch (Xdi2ClientException ex) {

			ex.printStackTrace();
		} catch (Exception ex) {

			ex.printStackTrace();
		}

	}

	public boolean requestForAccess(XDI3Segment requestedObjectXri,
			XDI3Segment operation, XDI3Segment fromRelationshipXri,
			XDI3Segment toRelationshipXri, PersonalCloud peerCloud) {

		XDIClient xdiClient = new XDIHttpClient(peerCloud.getCloudEndpointURI());
		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.getMessage(cloudNumber, true);
		message.setLinkContractXri(XDI3Segment
				.create("$public[+pendingrequest]$do"));
		
		message.setToAddress(XDI3Segment.create(XdiPeerRoot
				.createPeerRootArcXri(peerCloud.getCloudNumber())));

		String reqUUID = "!:uuid:" + UUID.randomUUID().toString();

		message.createSetOperation(XDI3Statement
				.create("$public[+pendingrequest]" + reqUUID
						+ "<+from_cn>&/&/\"" + cloudNumber.toString() + "\""));
		message.createSetOperation(XDI3Statement
				.create("$public[+pendingrequest]" + reqUUID
						+ "<+from_rel>&/&/\"" + fromRelationshipXri.toString()
						+ "\""));

		message.createSetOperation(XDI3Statement
				.create("$public[+pendingrequest]" + reqUUID
						+ "<+to_rel>&/&/\"" + toRelationshipXri.toString()
						+ "\""));
		message.createSetOperation(XDI3Statement
				.create("$public[+pendingrequest]" + reqUUID
						+ "<+requested_object>&/&/\""
						+ requestedObjectXri.toString() + "\""));
		message.createSetOperation(XDI3Statement
				.create("$public[+pendingrequest]" + reqUUID
						+ "<+requested_op>&/&/\"" + operation.toString() + "\""));
		try {
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(
					messageEnvelope.getGraph(), System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		// send the message

		MessageResult messageResult;

		try {

			messageResult = xdiClient.send(messageEnvelope, null);
			// System.out.println(messageResult);
			MemoryGraph response = (MemoryGraph) messageResult.getGraph();
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(response,
					System.out);

		} catch (Xdi2ClientException ex) {

			ex.printStackTrace();
			return false;
		} catch (Exception ex) {

			ex.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean approveAccess(XDI3Segment requestIdXri) {

		String from, from_rel, to_rel, operation, requested_object;
		XDIClient xdiClient = new XDIHttpClient(cloudEndpointURI);
		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.getMessage(cloudNumber, true);
		message.setLinkContractXri(linkContractAddress);
		message.setSecretToken(secretToken);
		message.setToAddress(XDI3Segment.create(XdiPeerRoot
				.createPeerRootArcXri(cloudNumber)));

		message.createGetOperation(requestIdXri);

		try {
			XDIWriterRegistry.forFormat("XDI/JSON", null).write(
					messageEnvelope.getGraph(), System.out);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		// send the message

		MessageResult messageResult;

		try {

			messageResult = xdiClient.send(messageEnvelope, null);
			// System.out.println(messageResult);
			MemoryGraph response = (MemoryGraph) messageResult.getGraph();
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(response,
					System.out);
			String jsonStr = response.toString("XDI/JSON",
					null);
			System.out.println(jsonStr);

		} catch (Xdi2ClientException ex) {

			ex.printStackTrace();
			return false;
		} catch (Exception ex) {

			ex.printStackTrace();
			return false;
		}
		
		//delete the request
		MessageEnvelope delMessageEnvelope = new MessageEnvelope();
		Message delMessage = delMessageEnvelope.getMessage(cloudNumber, true);
		delMessage.setLinkContractXri(linkContractAddress);
		delMessage.setSecretToken(secretToken);
		delMessage.setToAddress(XDI3Segment.create(XdiPeerRoot
				.createPeerRootArcXri(cloudNumber)));

		delMessage.createDelOperation(requestIdXri);
		
		MessageResult delMessageResult;
		
		try {

			delMessageResult = xdiClient.send(delMessageEnvelope, null);
			// System.out.println(messageResult);
			MemoryGraph response = (MemoryGraph) delMessageResult.getGraph();
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(response,
					System.out);
			

		} catch (Xdi2ClientException ex) {

			ex.printStackTrace();
			return false;
		} catch (Exception ex) {

			ex.printStackTrace();
			return false;
		}
		
		
		return true;
	}

	public boolean denyAccess(XDI3Segment requestIdXri) {
		//delete the request
		XDIClient xdiClient = new XDIHttpClient(cloudEndpointURI);
		MessageEnvelope delMessageEnvelope = new MessageEnvelope();
		Message delMessage = delMessageEnvelope.getMessage(cloudNumber, true);
		delMessage.setLinkContractXri(linkContractAddress);
		delMessage.setSecretToken(secretToken);
		delMessage.setToAddress(XDI3Segment.create(XdiPeerRoot
				.createPeerRootArcXri(cloudNumber)));

		delMessage.createDelOperation(requestIdXri);
		
		MessageResult delMessageResult;
		
		try {

			delMessageResult = xdiClient.send(delMessageEnvelope, null);
			// System.out.println(messageResult);
			MemoryGraph response = (MemoryGraph) delMessageResult.getGraph();
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(response,
					System.out);
			

		} catch (Xdi2ClientException ex) {

			ex.printStackTrace();
			return false;
		} catch (Exception ex) {

			ex.printStackTrace();
			return false;
		}
		
		
		return true;
	
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

	public void createDefaultLinkContracts() {
		XDIClient xdiClient = new XDIHttpClient(cloudEndpointURI);
		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.getMessage(cloudNumber, true);
		message.setLinkContractXri(linkContractAddress);
		message.setSecretToken(secretToken);
		message.setToAddress(XDI3Segment.create(XdiPeerRoot
				.createPeerRootArcXri(cloudNumber)));

		message.createSetOperation(XDI3Statement.create("$public"
				+ "[+pendingrequest]" + "$do/" + "$add" + "/" + "$public"
				+ "[+pendingrequest]"));
		message.createSetOperation(XDI3Statement.create("$public"
				+ "[+pendingrequest]" + "$do/" + "$set" + "/" + "$public"
				+ "[+pendingrequest]"));
		message.createSetOperation(XDI3Statement.create("$public"
				+ "[+pendingrequest]"
				+ "$do$if$and/$true/({$from}/$is/{$from})"));

		message.createSetOperation(XDI3Statement.create(cloudNumber + "+friend"
				+ "$do/" + "$all" + "/" + cloudNumber + "+friend"));

		message.createSetOperation(XDI3Statement.create(cloudNumber + "+friend"
				+ "$do$if$or/$true/(" + cloudNumber + "/"
				+ "+friend" + "/{$from}" + ")"));
		message.createSetOperation(XDI3Statement.create(cloudNumber + "+friend"
				+ "$do$if$or/$true/({$from}/$is/" + cloudNumber
				+ ")"));
		message.createSetOperation(XDI3Statement.create(cloudNumber + "+family"
				+ "$do/" + "$all" + "/" + cloudNumber + "+family"));

		message.createSetOperation(XDI3Statement.create(cloudNumber + "+family"
				+ "$do$if$or/$true/(" + cloudNumber + "/"
				+ "+family" + "/{$from}" + ")"));
		message.createSetOperation(XDI3Statement.create(cloudNumber + "+family"
				+ "$do$if$or/$true/({$from}/$is/" + cloudNumber
				+ ")"));
		message.createSetOperation(XDI3Statement.create(cloudNumber
				+ "+coworker" + "$do/" + "$all" + "/" + cloudNumber
				+ "+coworker"));

		message.createSetOperation(XDI3Statement.create(cloudNumber
				+ "+coworker" + "$do$if$or/$true/(" + cloudNumber
				+ "/" + "+coworker" + "/{$from}" + ")"));
		message.createSetOperation(XDI3Statement.create(cloudNumber
				+ "+coworker" + "$do$if$or/$true/({$from}/$is/"
				+ cloudNumber + ")"));

		// System.out.println("Message :\n" + messageEnvelope + "\n");
		try {
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(
					messageEnvelope.getGraph(), System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// send the message

		MessageResult messageResult;

		try {

			messageResult = xdiClient.send(messageEnvelope, null);
			// System.out.println(messageResult);
			MemoryGraph response = (MemoryGraph) messageResult.getGraph();
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(response,
					System.out);

		} catch (Xdi2ClientException ex) {

			ex.printStackTrace();
		} catch (Exception ex) {

			ex.printStackTrace();
		}

	}

	public void createRelationship(PersonalCloud otherPC,
			XDI3Segment relationship, XDI3Segment reverseRelationship) {
		XDIClient xdiClient = new XDIHttpClient(cloudEndpointURI);

		// prepare message envelope for getting email

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.getMessage(cloudNumber, true);
		message.setLinkContractXri(linkContractAddress);
		message.setSecretToken(secretToken);
		message.setToAddress(XDI3Segment.create(XdiPeerRoot
				.createPeerRootArcXri(cloudNumber)));

		message.createSetOperation(XDI3Statement.create(cloudNumber + "/"
				+ relationship + "/" + otherPC.getCloudNumber()));

		message.createSetOperation(XDI3Statement.create(otherPC
				.getCloudNumber()
				+ "/"
				+ reverseRelationship
				+ "/"
				+ cloudNumber));

		// System.out.println("Message :\n" + messageEnvelope + "\n");
		try {
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(
					messageEnvelope.getGraph(), System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// send the message

		MessageResult messageResult;

		try {

			messageResult = xdiClient.send(messageEnvelope, null);
			// System.out.println(messageResult);
			MemoryGraph response = (MemoryGraph) messageResult.getGraph();
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(response,
					System.out);

		} catch (Xdi2ClientException ex) {

			ex.printStackTrace();
		} catch (Exception ex) {

			ex.printStackTrace();
		}
	}

	public void allowAccessToRelationship(XDI3Segment target,XDI3Segment mapTarget,
			XDI3Segment permissionXri, XDI3Segment relationship,
			XDI3Segment reverseRelationship, XDI3Segment assignee) {

		PersonalCloud assigneePC = PersonalCloud.open(assignee, cloudNumber,
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

		if(mapTarget != null){
			message.createSetOperation(XDI3Statement.create(target + "/$rep/" +  mapTarget));
		}
		message.createSetOperation(XDI3Statement.create(cloudNumber.toString()
				+ relationship.toString() + "$do/" + permissionXri.toString()
				+ "/" + target));
		
	
		message.createSetOperation(XDI3Statement.create(cloudNumber + "/"
				+ relationship + "/" + assigneeCN));

		message.createSetOperation(XDI3Statement.create(assigneeCN + "/"
				+ reverseRelationship + "/" + cloudNumber));

		// System.out.println("Message :\n" + messageEnvelope + "\n");
		try {
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(
					messageEnvelope.getGraph(), System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// send the message

		MessageResult messageResult;

		try {

			messageResult = xdiClient.send(messageEnvelope, null);
			// System.out.println(messageResult);
			MemoryGraph response = (MemoryGraph) messageResult.getGraph();
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(response,
					System.out);

		} catch (Xdi2ClientException ex) {

			ex.printStackTrace();
		} catch (Exception ex) {

			ex.printStackTrace();
		}

	}

	public void getPCEntity(XDI3Segment targetAddress, XDI3Segment linkContract, PersonalCloud peerCloud) {

		XDIClient xdiClient = new XDIHttpClient(peerCloud.getCloudEndpointURI());

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.getMessage(cloudNumber, true);
		message.setLinkContractXri(linkContract);

		message.setToAddress(XDI3Segment.create(XdiPeerRoot
				.createPeerRootArcXri(peerCloud.getCloudNumber())));

		message.createGetOperation(targetAddress);

		// System.out.println("Message :\n" + messageEnvelope + "\n");
		try {
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(
					messageEnvelope.getGraph(), System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// send the message

		MessageResult messageResult;

		try {

			messageResult = xdiClient.send(messageEnvelope, null);
			// System.out.println(messageResult);
			MemoryGraph response = (MemoryGraph) messageResult.getGraph();
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(response,
					System.out);

		} catch (Xdi2ClientException ex) {

			ex.printStackTrace();
		} catch (Exception ex) {

			ex.printStackTrace();
		}
	}

}

package clouds.client.basic;

import java.net.URLEncoder;
import java.util.Arrays;

import xdi2.client.XDIClient;
import xdi2.client.http.XDIHttpClient;
import xdi2.core.Relation;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.constants.XDIConstants;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.target.interceptor.impl.authentication.secrettoken.DigestSecretTokenAuthenticator;

public  class CSPClient {

	public static final String RESPECT_NETWORK_REGISTRAR_XDI_ENDPOINT = "http://mycloud.neustar.biz:12230/";
	public static final XDI3Segment RESPECT_NETWORK_CLOUD_NUMBER = XDI3Segment
			.create("[@]!:uuid:299089fd-9d81-3c59-2990-89fd9d813c59");

	private static XDIClient xdiClientRespectNetworkRegistrar = new XDIHttpClient(
			RESPECT_NETWORK_REGISTRAR_XDI_ENDPOINT);
	private static XDIClient xdiClientHostingEnvironmentRegistry;

	public static void main(String[] args) throws Exception {

		CSP csp = new CSPOwnYourInfo();
		xdiClientHostingEnvironmentRegistry = new XDIHttpClient(
				csp.getHostingEnvironmentRegistryXdiEndpoint());
		
		String cloudName = args[0];
		String secretToken = new String("");
			if(args.length == 2){
				secretToken = args[1];
			}

		// step 1: Check if Cloud Name available

		XDI3Segment cloudNumber = checkCloudNameAvailable(csp, cloudName);

		// step 2: Register Cloud Name
		if (cloudNumber == null || cloudNumber.toString().length() == 0) {

			XDI3Segment cloudNumberPeerRootXri = registerCloudName(csp,
					cloudName);

			cloudNumber = XdiPeerRoot
					.getXriOfPeerRootArcXri(cloudNumberPeerRootXri
							.getFirstSubSegment());
			if (cloudNumberPeerRootXri != null
					&& cloudNumberPeerRootXri.toString().length() > 0) {
				// step 3: Register Cloud with Cloud Number and Shared Secret

				String xdiEndpoint = registerCloud(csp,
						XDI3Segment.create(cloudName), cloudNumber,
						cloudNumberPeerRootXri, secretToken);

				if (xdiEndpoint.length() > 0) {
					// step 4: Register Cloud XDI URL with Cloud Number

					registerCloudXdiUrl(csp, cloudNumberPeerRootXri,
							xdiEndpoint);
				}
			}
		}
	}

	public static XDI3Segment checkCloudNameAvailable(CSP csp,
			String cloudNameStr) throws Exception {

		MessageEnvelope messageEnvelope = new MessageEnvelope();

		Message message = messageEnvelope.getMessage(csp.getCSPCloudNumber(),
				true);
		message.setToAddress(XDI3Segment.fromComponent(XdiPeerRoot
				.createPeerRootArcXri(RESPECT_NETWORK_CLOUD_NUMBER)));
		message.setLinkContractXri(XDI3Segment.create("+registrar$do"));
		message.getContextNode().setDeepLiteral(XDI3Segment.create("" + XDIAuthenticationConstants.XRI_S_SECRET_TOKEN + XDIConstants.XRI_S_VALUE), csp.getCSPSecretToken());

		XDI3Segment cloudName = XDI3Segment.create(cloudNameStr);
		XDI3Segment cloudNamePeerRootXri = XDI3Segment
				.fromComponent(XdiPeerRoot.createPeerRootArcXri(cloudName));

		XDI3Segment targetAddress = cloudNamePeerRootXri;

		message.createGetOperation(targetAddress);

		MessageResult messageResult = xdiClientRespectNetworkRegistrar.send(
				messageEnvelope, null);

		if (messageResult.getGraph().isEmpty()) {

			System.out.println("Cloud Name " + cloudName + " is available");
			return null;
		} else {

			Relation relation = messageResult.getGraph().getDeepRelation(
					cloudNamePeerRootXri, XDIDictionaryConstants.XRI_S_REF);
			if (relation == null)
				throw new RuntimeException("Cloud Number not registered.");

			XDI3Segment cloudNumberPeerRootXri = relation
					.getTargetContextNodeXri();
			XDI3Segment cloudNumber = XdiPeerRoot
					.getXriOfPeerRootArcXri(cloudNumberPeerRootXri
							.getFirstSubSegment());

			System.out
					.println("Cloud Name " + cloudName
							+ " is already registered with Cloud Number "
							+ cloudNumber);

			return cloudNumber;
		}
	}

	public static XDI3Segment registerCloudName(CSP csp, String cloudNameStr)
			throws Exception {

		
		MessageEnvelope messageEnvelope = new MessageEnvelope();

		Message message = messageEnvelope.getMessage(csp.getCSPCloudNumber(),
				true);
		message.setToAddress(XDI3Segment.fromComponent(XdiPeerRoot
				.createPeerRootArcXri(RESPECT_NETWORK_CLOUD_NUMBER)));
		message.setLinkContractXri(XDI3Segment.create("+registrar$do"));
		message.getContextNode().setDeepLiteral(XDI3Segment.create("" + XDIAuthenticationConstants.XRI_S_SECRET_TOKEN + XDIConstants.XRI_S_VALUE), csp.getCSPSecretToken());

		XDI3Segment cloudName = XDI3Segment.create(cloudNameStr);
		XDI3Segment cloudNamePeerRootXri = XDI3Segment
				.fromComponent(XdiPeerRoot.createPeerRootArcXri(cloudName));
		XDI3Statement targetStatement = XDI3Statement.fromRelationComponents(
				cloudNamePeerRootXri, XDIDictionaryConstants.XRI_S_REF,
				XDIConstants.XRI_S_VARIABLE);

		message.createSetOperation(targetStatement);

		MessageResult messageResult = xdiClientRespectNetworkRegistrar.send(
				messageEnvelope, null);

		Relation relation = messageResult.getGraph().getDeepRelation(
				cloudNamePeerRootXri, XDIDictionaryConstants.XRI_S_REF);
		if (relation == null)
			throw new RuntimeException("Cloud Number not registered.");

		XDI3Segment cloudNumberPeerRootXri = relation.getTargetContextNodeXri();
		cloudNumberPeerRootXri = relation.getTargetContextNodeXri();
		XDI3Segment cloudNumber = XdiPeerRoot
				.getXriOfPeerRootArcXri(cloudNumberPeerRootXri
						.getFirstSubSegment());

		System.out.println("Cloud Name " + cloudName
				+ " registered with Cloud Number " + cloudNumber);
		return cloudNumberPeerRootXri;

	}

	public static String registerCloud(CSP csp, XDI3Segment cloudName,
			XDI3Segment cloudNumber, XDI3Segment cloudNumberPeerRootXri,
			String secretToken) throws Exception {

		MessageEnvelope messageEnvelope = new MessageEnvelope();

		Message message = messageEnvelope.getMessage(csp.getCSPCloudNumber(),
				true);
		message.setToAddress(XDI3Segment.fromComponent(XdiPeerRoot
				.createPeerRootArcXri(csp.getCSPCloudNumber())));
		message.setLinkContractXri(XDI3Segment.create("$do"));
		message.getContextNode().setDeepLiteral(XDI3Segment.create("" + XDIAuthenticationConstants.XRI_S_SECRET_TOKEN + XDIConstants.XRI_S_VALUE), csp.getCSPSecretToken());

		String digestSecretToken = DigestSecretTokenAuthenticator
				.localSaltAndDigestSecretToken(secretToken, csp.getGlobalSalt());
		String cloudXdiEndpoint = csp
				.getHostingEnvironmentCloudBaseXdiEndpoint()
				+ URLEncoder.encode(cloudNumber.toString(), "UTF-8");
		XDI3Segment cloudNamePeerRootXri = XDI3Segment
				.fromComponent(XdiPeerRoot.createPeerRootArcXri(cloudName));

		XDI3Statement[] targetStatements = new XDI3Statement[] {
				XDI3Statement.fromRelationComponents(cloudNamePeerRootXri, XDIDictionaryConstants.XRI_S_REF, cloudNumberPeerRootXri),
				XDI3Statement.fromLiteralComponents(XDI3Segment.create("" + cloudNumberPeerRootXri + XDIAuthenticationConstants.XRI_S_SECRET_TOKEN + XDIConstants.XRI_S_VALUE), digestSecretToken),
				XDI3Statement.fromLiteralComponents(XDI3Segment.create("" + cloudNumberPeerRootXri + "$xdi<$uri>&"), cloudXdiEndpoint)
		};

		message.createSetOperation(Arrays.asList(targetStatements).iterator());

		

		xdiClientHostingEnvironmentRegistry.send(messageEnvelope, null);

		System.out.println("Cloud " + "registered with Cloud Number "
				+ cloudNumber + " and Digest Secret Token " + secretToken
				+ " and Cloud XDI endpoint " + cloudXdiEndpoint);

		return cloudXdiEndpoint;
	}

	public static boolean registerCloudXdiUrl(CSP csp,
			XDI3Segment cloudNumberPeerRootXri, String cloudXdiEndpoint)
			throws Exception {

		MessageEnvelope messageEnvelope = new MessageEnvelope();

		Message message = messageEnvelope.getMessage(csp.getCSPCloudNumber(),
				true);
		message.setToAddress(XDI3Segment.fromComponent(XdiPeerRoot
				.createPeerRootArcXri(RESPECT_NETWORK_CLOUD_NUMBER)));
		message.setLinkContractXri(XDI3Segment.create("+registrar$do"));
		
		message.getContextNode().setDeepLiteral(XDI3Segment.create("" + XDIAuthenticationConstants.XRI_S_SECRET_TOKEN + XDIConstants.XRI_S_VALUE), csp.getCSPSecretToken());
		XDI3Statement targetStatement = XDI3Statement
				.fromLiteralComponents(
						XDI3Segment.create("" + cloudNumberPeerRootXri
								+ "$xdi<$uri>&"), cloudXdiEndpoint);

		message.createSetOperation(targetStatement);

		xdiClientRespectNetworkRegistrar.send(messageEnvelope, null);

		System.out.println("Cloud XDI URL registered - " + cloudXdiEndpoint);

		return true;
	}
}

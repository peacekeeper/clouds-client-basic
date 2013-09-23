package clouds.client.basic;

import xdi2.core.xri3.XDI3Segment;

public class CSPOwnYourInfo implements CSP {

	public static final XDI3Segment CSP_CLOUD_NUMBER = XDI3Segment.create("[@]!:uuid:f34559e4-6b2b-d962-f345-59e46b2bd962");
	public static final String CSP_SECRET_TOKEN = "ofniruoynwo";
	public static final String CSP_GLOBAL_SALT = "8b46d9b1-efff-4f7b-8cc2-2d41c8ac8d32";

	public static final String HOSTING_ENVIRONMENT_REGISTRY_XDI_ENDPOINT = "http://clouds.ownyourinfo.com:14440/ownyourinfo-registry";
	public static final String HOSTING_ENVIRONMENT_CLOUD_BASE_XDI_ENDPOINT = "http://clouds.ownyourinfo.com:14440/ownyourinfo-users/";

	@Override
	public XDI3Segment getCSPCloudNumber() {

		return CSP_CLOUD_NUMBER;
	}

	@Override
	public String getCSPSecretToken() {

		return CSP_SECRET_TOKEN;
	}

	@Override
	public String getGlobalSalt() {

		return CSP_GLOBAL_SALT;
	}

	@Override
	public String getHostingEnvironmentRegistryXdiEndpoint() {

		return HOSTING_ENVIRONMENT_REGISTRY_XDI_ENDPOINT;
	}

	@Override
	public String getHostingEnvironmentCloudBaseXdiEndpoint() {

		return HOSTING_ENVIRONMENT_CLOUD_BASE_XDI_ENDPOINT;
	}
}

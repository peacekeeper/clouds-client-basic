package clouds.client.basic;

import xdi2.core.xri3.XDI3Segment;

public class CSPNeustar implements CSP {

	public static final XDI3Segment CSP_CLOUD_NUMBER = XDI3Segment.create("[@]!:uuid:0baea650-823b-2475-0bae-a650823b2475");
	public static final String CSP_SECRET_TOKEN = "s3cr3t";
	public static final String CSP_GLOBAL_SALT = "c2293773-3240-4524-8c19-c1f5cbe31b86";

	public static final String HOSTING_ENVIRONMENT_REGISTRY_XDI_ENDPOINT = "http://mycloud.neustar.biz:14440/registry";
	public static final String HOSTING_ENVIRONMENT_CLOUD_BASE_XDI_ENDPOINT = "http://mycloud.neustar.biz:14440/users/";

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

package clouds.client.basic;

import xdi2.core.xri3.XDI3Segment;

public interface CSP {

	public XDI3Segment getCSPCloudNumber();
	public String getCSPSecretToken();
	public String getGlobalSalt();

	public String getHostingEnvironmentRegistryXdiEndpoint();
	public String getHostingEnvironmentCloudBaseXdiEndpoint();
}

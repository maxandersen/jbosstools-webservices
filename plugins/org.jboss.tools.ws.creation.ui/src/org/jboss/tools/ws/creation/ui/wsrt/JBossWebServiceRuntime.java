package org.jboss.tools.ws.creation.ui.wsrt;

import org.eclipse.wst.ws.internal.wsrt.AbstractWebServiceRuntime;
import org.eclipse.wst.ws.internal.wsrt.IWebService;
import org.eclipse.wst.ws.internal.wsrt.IWebServiceClient;
import org.eclipse.wst.ws.internal.wsrt.WebServiceClientInfo;
import org.eclipse.wst.ws.internal.wsrt.WebServiceInfo;

@SuppressWarnings("restriction")
public class JBossWebServiceRuntime extends AbstractWebServiceRuntime{

	@Override
	public IWebService getWebService(WebServiceInfo info) {
		return new JBossWebService(info);
	}

	@Override
	public IWebServiceClient getWebServiceClient(WebServiceClientInfo info) {
		return new JBossWebServiceClient(info);
	}

}

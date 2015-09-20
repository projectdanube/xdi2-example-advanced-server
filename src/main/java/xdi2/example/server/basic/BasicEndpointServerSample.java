package xdi2.example.server.basic;

import java.util.Collections;

import xdi2.core.Graph;
import xdi2.core.features.secrettokens.SecretTokens;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.messaging.target.interceptor.impl.BootstrapInterceptor;
import xdi2.messaging.target.interceptor.impl.linkcontract.LinkContractInterceptor;
import xdi2.messaging.target.interceptor.impl.security.secrettoken.SecretTokenInterceptor;
import xdi2.messaging.target.interceptor.impl.security.secrettoken.StaticSecretTokenValidator;
import xdi2.server.impl.standalone.XDIStandaloneServer;

public class BasicEndpointServerSample {

	public static void main(String[] args) throws Exception {

		// create the XDI2 server

		XDIStandaloneServer endpointServer = XDIStandaloneServer.newServer();

		// set up graph messaging target

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		GraphMessagingTarget messagingTarget = new GraphMessagingTarget();
		messagingTarget.setGraph(graph);

		// add interceptors

		BootstrapInterceptor bi = new BootstrapInterceptor();
		bi.setBootstrapOwner(XDIAddress.create("=!:uuid:1111"));
		bi.setBootstrapRootLinkContract(true);

		SecretTokenInterceptor sti = new SecretTokenInterceptor();
		sti.setSecretTokenValidator(
				new StaticSecretTokenValidator(
						"00000000-0000-0000-0000-000000000000", 
						Collections.singletonMap(
								XDIAddress.create("=!:uuid:1111"), 
								SecretTokens.localSaltAndDigestSecretToken(
										"s3cr3t",
										"00000000-0000-0000-0000-000000000000"))));

		LinkContractInterceptor li = new LinkContractInterceptor();

		messagingTarget.getInterceptors().addInterceptor(bi);
		messagingTarget.getInterceptors().addInterceptor(sti);
		messagingTarget.getInterceptors().addInterceptor(li);

		// mount messaging target

		endpointServer.getEndpointServlet().getHttpTransport().getUriMessagingTargetRegistry().mountMessagingTarget("/", messagingTarget);

		// start the server

		endpointServer.startServer();
	}
}

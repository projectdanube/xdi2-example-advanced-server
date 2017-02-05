package xdi2.example.server.basic;

import java.util.Collections;

import xdi2.core.Graph;
import xdi2.core.features.secrettokens.SecretTokens;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.container.impl.graph.GraphMessagingContainer;
import xdi2.messaging.container.interceptor.impl.BootstrapInterceptor;
import xdi2.messaging.container.interceptor.impl.linkcontract.LinkContractInterceptor;
import xdi2.messaging.container.interceptor.impl.security.secrettoken.SecretTokenInterceptor;
import xdi2.messaging.container.interceptor.impl.security.secrettoken.StaticSecretTokenValidator;
import xdi2.server.impl.standalone.XDIStandaloneServer;

public class BasicEndpointServerSample {

	public static void main(String[] args) throws Exception {

		// create the XDI2 server

		XDIStandaloneServer endpointServer = XDIStandaloneServer.newServer();

		// set up graph messaging container

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		GraphMessagingContainer messagingContainer = new GraphMessagingContainer();
		messagingContainer.setGraph(graph);

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

		messagingContainer.getInterceptors().addInterceptor(bi);
		messagingContainer.getInterceptors().addInterceptor(sti);
		messagingContainer.getInterceptors().addInterceptor(li);

		// mount messaging container

		endpointServer.getEndpointServlet().getHttpTransport().getUriMessagingContainerRegistry().mountMessagingContainer("/", messagingContainer);

		// start the server

		endpointServer.startServer();
	}
}

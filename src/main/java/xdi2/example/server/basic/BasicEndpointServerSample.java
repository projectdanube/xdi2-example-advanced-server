package xdi2.example.server.basic;

import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.messaging.target.interceptor.impl.BootstrapInterceptor;
import xdi2.transport.impl.http.embedded.EndpointServerEmbedded;

public class BasicEndpointServerSample {

	public static void main(String[] args) throws Exception {

		// create the XDI2 server

		EndpointServerEmbedded endpointServer = EndpointServerEmbedded.newServer();

		// set up graph messaging target

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		GraphMessagingTarget messagingTarget = new GraphMessagingTarget();
		messagingTarget.setGraph(graph);

		// add interceptor

		BootstrapInterceptor bi = new BootstrapInterceptor();
		bi.setBootstrapOwner(XDI3Segment.create("[=]!:uuid:1111"));
		bi.setBootstrapRootLinkContract(true);

		messagingTarget.getInterceptors().addInterceptor(bi);

		// mount messaging target

		endpointServer.getEndpointServlet().getHttpMessagingTargetRegistry().mountMessagingTarget("/", messagingTarget);

		// start the server

		endpointServer.start();
	}
}

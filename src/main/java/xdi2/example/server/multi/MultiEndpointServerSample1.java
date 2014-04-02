package xdi2.example.server.multi;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import xdi2.core.Graph;
import xdi2.core.impl.json.mongodb.MongoDBJSONGraphFactory;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.transport.impl.http.embedded.EndpointServerEmbedded;

public class MultiEndpointServerSample1 {

	private static void setupAndMountGraphMessagingTarget(EndpointServerEmbedded endpointServer) throws Exception {

		// set up graph messaging target

		Graph graph = new MongoDBJSONGraphFactory().openGraph("mygraph");
		GraphMessagingTarget messagingTarget = new GraphMessagingTarget();
		messagingTarget.setGraph(graph);

		// mount messaging target

		endpointServer.getEndpointServlet().getHttpMessagingTargetRegistry().mountMessagingTarget("/", messagingTarget);
	}

	public static void main(String[] args) throws Exception {

		// read configuration files
		
		Resource jettyApplicationContextResource1 = new UrlResource(MultiEndpointServerSample1.class.getResource("jetty-applicationContext1.xml"));
		Resource jettyApplicationContextResource2 = new UrlResource(MultiEndpointServerSample1.class.getResource("jetty-applicationContext2.xml"));
		Resource jettyApplicationContextResource3 = new UrlResource(MultiEndpointServerSample1.class.getResource("jetty-applicationContext3.xml"));

		// create the XDI2 servers

		EndpointServerEmbedded endpointServer1 = EndpointServerEmbedded.newServer(null, jettyApplicationContextResource1);
		EndpointServerEmbedded endpointServer2 = EndpointServerEmbedded.newServer(null, jettyApplicationContextResource2);
		EndpointServerEmbedded endpointServer3 = EndpointServerEmbedded.newServer(null, jettyApplicationContextResource3);

		// set up and mount graph messaging target

		setupAndMountGraphMessagingTarget(endpointServer1);
		setupAndMountGraphMessagingTarget(endpointServer2);
		setupAndMountGraphMessagingTarget(endpointServer3);

		// start the server

		endpointServer1.start();
		endpointServer2.start();
		endpointServer3.start();
	}
}

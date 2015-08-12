package xdi2.example.server.multi;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import xdi2.core.Graph;
import xdi2.core.impl.json.mongodb.MongoDBJSONGraphFactory;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.server.impl.embedded.XDIEmbeddedServer;

public class MultiEndpointServerSample1 {

	private static void setupAndMountGraphMessagingTarget(XDIEmbeddedServer endpointServer) throws Exception {

		// set up graph messaging target

		Graph graph = new MongoDBJSONGraphFactory().openGraph("mygraph");
		GraphMessagingTarget messagingTarget = new GraphMessagingTarget();
		messagingTarget.setGraph(graph);

		// mount messaging target

		endpointServer.getEndpointServlet().getHttpTransport().getUriMessagingTargetRegistry().mountMessagingTarget("/", messagingTarget);
	}

	public static void main(String[] args) throws Exception {

		// read configuration files
		
		Resource jettyApplicationContextResource1 = new UrlResource(MultiEndpointServerSample1.class.getResource("server-applicationContext1.xml"));
		Resource jettyApplicationContextResource2 = new UrlResource(MultiEndpointServerSample1.class.getResource("server-applicationContext2.xml"));
		Resource jettyApplicationContextResource3 = new UrlResource(MultiEndpointServerSample1.class.getResource("server-applicationContext3.xml"));

		// create the XDI2 servers

		XDIEmbeddedServer endpointServer1 = XDIEmbeddedServer.newServer(null, jettyApplicationContextResource1);
		XDIEmbeddedServer endpointServer2 = XDIEmbeddedServer.newServer(null, jettyApplicationContextResource2);
		XDIEmbeddedServer endpointServer3 = XDIEmbeddedServer.newServer(null, jettyApplicationContextResource3);

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

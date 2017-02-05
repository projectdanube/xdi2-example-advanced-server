package xdi2.example.server.multi;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import xdi2.core.Graph;
import xdi2.core.impl.json.mongodb.MongoDBJSONGraphFactory;
import xdi2.messaging.container.impl.graph.GraphMessagingContainer;
import xdi2.server.impl.standalone.XDIStandaloneServer;

public class MultiEndpointServerSample1 {

	private static void setupAndMountGraphMessagingContainer(XDIStandaloneServer endpointServer) throws Exception {

		// set up graph messaging container

		Graph graph = new MongoDBJSONGraphFactory().openGraph("mygraph");
		GraphMessagingContainer messagingContainer = new GraphMessagingContainer();
		messagingContainer.setGraph(graph);

		// mount messaging container

		endpointServer.getEndpointServlet().getHttpTransport().getUriMessagingContainerRegistry().mountMessagingContainer("/", messagingContainer);
	}

	public static void main(String[] args) throws Exception {

		// read configuration files
		
		Resource serverApplicationContextResource1 = new UrlResource(MultiEndpointServerSample1.class.getResource("server-applicationContext1.xml"));
		Resource serverApplicationContextResource2 = new UrlResource(MultiEndpointServerSample1.class.getResource("server-applicationContext2.xml"));
		Resource serverApplicationContextResource3 = new UrlResource(MultiEndpointServerSample1.class.getResource("server-applicationContext3.xml"));

		// create the XDI2 servers

		XDIStandaloneServer endpointServer1 = XDIStandaloneServer.newServer(null, serverApplicationContextResource1);
		XDIStandaloneServer endpointServer2 = XDIStandaloneServer.newServer(null, serverApplicationContextResource2);
		XDIStandaloneServer endpointServer3 = XDIStandaloneServer.newServer(null, serverApplicationContextResource3);

		// set up and mount graph messaging container

		setupAndMountGraphMessagingContainer(endpointServer1);
		setupAndMountGraphMessagingContainer(endpointServer2);
		setupAndMountGraphMessagingContainer(endpointServer3);

		// start the server

		endpointServer1.startServer();
		endpointServer2.startServer();
		endpointServer3.startServer();
	}
}

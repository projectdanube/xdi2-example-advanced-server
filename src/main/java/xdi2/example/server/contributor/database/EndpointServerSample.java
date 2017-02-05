package xdi2.example.server.contributor.database;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import xdi2.messaging.container.contributor.Contributor;
import xdi2.messaging.container.impl.graph.GraphMessagingContainer;
import xdi2.server.impl.standalone.XDIStandaloneServer;
import xdi2.transport.impl.http.HttpTransport;
import xdi2.transport.registry.impl.uri.UriMessagingContainerRegistry;

public class EndpointServerSample {

	public static void main(String[] args) throws Exception {

		// read configuration files

		Resource applicationContextResource = new UrlResource(EndpointServerSample.class.getResource("applicationContext.xml"));
		Resource serverApplicationContextResource = new UrlResource(EndpointServerSample.class.getResource("server-applicationContext.xml"));

		// create the XDI2 server

		XDIStandaloneServer endpointServer = XDIStandaloneServer.newServer(applicationContextResource, serverApplicationContextResource);

		// add a custom contributor (this can also be done in the applicationContext.xml config file)

		HttpTransport http = endpointServer.getEndpointServlet().getHttpTransport();
		UriMessagingContainerRegistry registry = http.getUriMessagingContainerRegistry();
		GraphMessagingContainer graphMessagingContainer = (GraphMessagingContainer) registry.getMessagingContainer("/");

		Contributor contributor = new BooksConnector();
		contributor.init(graphMessagingContainer);
		graphMessagingContainer.getContributors().addContributor(contributor);

		// start the server

		endpointServer.startServer();
	}
}

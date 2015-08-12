package xdi2.example.server.contributor.database;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import xdi2.messaging.target.contributor.Contributor;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.server.impl.embedded.XDIEmbeddedServer;
import xdi2.transport.impl.http.HttpTransport;
import xdi2.transport.registry.impl.uri.UriMessagingTargetRegistry;

public class EndpointServerSample {

	public static void main(String[] args) throws Exception {

		// read configuration files

		Resource applicationContextResource = new UrlResource(EndpointServerSample.class.getResource("applicationContext.xml"));
		Resource serverApplicationContextResource = new UrlResource(EndpointServerSample.class.getResource("server-applicationContext.xml"));

		// create the XDI2 server

		XDIEmbeddedServer endpointServer = XDIEmbeddedServer.newServer(applicationContextResource, serverApplicationContextResource);

		// add a custom contributor (this can also be done in the applicationContext.xml config file)

		HttpTransport http = endpointServer.getEndpointServlet().getHttpTransport();
		UriMessagingTargetRegistry registry = http.getUriMessagingTargetRegistry();
		GraphMessagingTarget graphMessagingTarget = (GraphMessagingTarget) registry.getMessagingTarget("/");

		Contributor contributor = new BooksConnector();
		contributor.init(graphMessagingTarget);
		graphMessagingTarget.getContributors().addContributor(contributor);

		// start the server

		endpointServer.start();
	}
}

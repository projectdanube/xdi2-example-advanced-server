package xdi2.example.server.contributor;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.transport.impl.http.HttpTransport;
import xdi2.transport.impl.http.embedded.EndpointServerEmbedded;
import xdi2.transport.impl.http.registry.HttpMessagingTargetRegistry;

public class ContributorEndpointServerSample {

	public static void main(String[] args) throws Exception {

		// read configuration files

		Resource applicationContextResource = new UrlResource(ContributorEndpointServerSample.class.getResource("applicationContext.xml"));
		Resource jettyApplicationContextResource = new UrlResource(ContributorEndpointServerSample.class.getResource("jetty-applicationContext.xml"));

		// create the XDI2 server

		EndpointServerEmbedded endpointServer = EndpointServerEmbedded.newServer(applicationContextResource, jettyApplicationContextResource);

		// add a custom contributor

		HttpTransport http = endpointServer.getEndpointServlet().getHttpTransport();
		HttpMessagingTargetRegistry registry = http.getHttpMessagingTargetRegistry();
		GraphMessagingTarget graphMessagingTarget = (GraphMessagingTarget) registry.getMessagingTarget("/");

		graphMessagingTarget.getContributors().addContributor(
				XDIAddress.create("{}[<#inbox>]"),
				new FastCollectionContributor("./xdiinbox"));

		// start the server

		endpointServer.start();
	}
}

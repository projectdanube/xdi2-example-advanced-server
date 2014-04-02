package xdi2.example.server.multi;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import xdi2.transport.impl.http.embedded.EndpointServerEmbedded;

public class MultiEndpointServerSample2 {

	public static void main(String[] args) throws Exception {

		// read configuration files
		
		Resource jettyApplicationContextResource1 = new UrlResource(MultiEndpointServerSample2.class.getResource("jetty-applicationContext1.xml"));
		Resource jettyApplicationContextResource2 = new UrlResource(MultiEndpointServerSample2.class.getResource("jetty-applicationContext2.xml"));
		Resource jettyApplicationContextResource3 = new UrlResource(MultiEndpointServerSample2.class.getResource("jetty-applicationContext3.xml"));

		Resource applicationContextResource = new UrlResource(MultiEndpointServerSample2.class.getResource("applicationContext.xml"));

		// create the XDI2 servers

		EndpointServerEmbedded endpointServer1 = EndpointServerEmbedded.newServer(applicationContextResource, jettyApplicationContextResource1);
		EndpointServerEmbedded endpointServer2 = EndpointServerEmbedded.newServer(applicationContextResource, jettyApplicationContextResource2);
		EndpointServerEmbedded endpointServer3 = EndpointServerEmbedded.newServer(applicationContextResource, jettyApplicationContextResource3);

		// start the server

		endpointServer1.start();
		endpointServer2.start();
		endpointServer3.start();
	}
}

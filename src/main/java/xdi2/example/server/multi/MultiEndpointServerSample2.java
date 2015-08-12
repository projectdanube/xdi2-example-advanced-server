package xdi2.example.server.multi;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import xdi2.server.impl.embedded.XDIEmbeddedServer;

public class MultiEndpointServerSample2 {

	public static void main(String[] args) throws Exception {

		// read configuration files
		
		Resource jettyApplicationContextResource1 = new UrlResource(MultiEndpointServerSample2.class.getResource("server-applicationContext1.xml"));
		Resource jettyApplicationContextResource2 = new UrlResource(MultiEndpointServerSample2.class.getResource("server-applicationContext2.xml"));
		Resource jettyApplicationContextResource3 = new UrlResource(MultiEndpointServerSample2.class.getResource("server-applicationContext3.xml"));

		Resource applicationContextResource = new UrlResource(MultiEndpointServerSample2.class.getResource("applicationContext.xml"));

		// create the XDI2 servers

		XDIEmbeddedServer endpointServer1 = XDIEmbeddedServer.newServer(applicationContextResource, jettyApplicationContextResource1);
		XDIEmbeddedServer endpointServer2 = XDIEmbeddedServer.newServer(applicationContextResource, jettyApplicationContextResource2);
		XDIEmbeddedServer endpointServer3 = XDIEmbeddedServer.newServer(applicationContextResource, jettyApplicationContextResource3);

		// start the server

		endpointServer1.start();
		endpointServer2.start();
		endpointServer3.start();
	}
}

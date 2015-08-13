package xdi2.example.server.multi;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import xdi2.server.impl.standalone.XDIStandaloneServer;

public class MultiEndpointServerSample2 {

	public static void main(String[] args) throws Exception {

		// read configuration files
		
		Resource serverApplicationContextResource1 = new UrlResource(MultiEndpointServerSample2.class.getResource("server-applicationContext1.xml"));
		Resource serverApplicationContextResource2 = new UrlResource(MultiEndpointServerSample2.class.getResource("server-applicationContext2.xml"));
		Resource serverApplicationContextResource3 = new UrlResource(MultiEndpointServerSample2.class.getResource("server-applicationContext3.xml"));

		Resource applicationContextResource = new UrlResource(MultiEndpointServerSample2.class.getResource("applicationContext.xml"));

		// create the XDI2 servers

		XDIStandaloneServer endpointServer1 = XDIStandaloneServer.newServer(applicationContextResource, serverApplicationContextResource1);
		XDIStandaloneServer endpointServer2 = XDIStandaloneServer.newServer(applicationContextResource, serverApplicationContextResource2);
		XDIStandaloneServer endpointServer3 = XDIStandaloneServer.newServer(applicationContextResource, serverApplicationContextResource3);

		// start the server

		endpointServer1.startServer();
		endpointServer2.startServer();
		endpointServer3.startServer();
	}
}

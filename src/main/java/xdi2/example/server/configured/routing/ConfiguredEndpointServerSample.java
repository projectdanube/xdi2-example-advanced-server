package xdi2.example.server.configured.routing;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import xdi2.server.impl.standalone.XDIStandaloneServer;

public class ConfiguredEndpointServerSample {

	public static void main(String[] args) throws Exception {

		// read configuration files

		Resource serverApplicationContextResource1 = new UrlResource(ConfiguredEndpointServerSample.class.getResource("server-applicationContext1.xml"));
		Resource serverApplicationContextResource2 = new UrlResource(ConfiguredEndpointServerSample.class.getResource("server-applicationContext2.xml"));

		Resource applicationContextResource1 = new UrlResource(ConfiguredEndpointServerSample.class.getResource("applicationContext1.xml"));
		Resource applicationContextResource2 = new UrlResource(ConfiguredEndpointServerSample.class.getResource("applicationContext2.xml"));

		// create the XDI2 servers

		XDIStandaloneServer endpointServer1 = XDIStandaloneServer.newServer(applicationContextResource1, serverApplicationContextResource1);
		XDIStandaloneServer endpointServer2 = XDIStandaloneServer.newServer(applicationContextResource2, serverApplicationContextResource2);

		// start the server

		endpointServer1.startServer();
		endpointServer2.startServer();
	}
}

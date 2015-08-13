package xdi2.example.server.configured;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import xdi2.server.impl.standalone.XDIStandaloneServer;

public class ConfiguredEndpointServerSample {

	public static void main(String[] args) throws Exception {

		// read configuration files

		Resource applicationContextResource = new UrlResource(ConfiguredEndpointServerSample.class.getResource("applicationContext.xml"));
		Resource serverApplicationContextResource = new UrlResource(ConfiguredEndpointServerSample.class.getResource("server-applicationContext.xml"));

		// create the XDI2 server

		XDIStandaloneServer endpointServer = XDIStandaloneServer.newServer(applicationContextResource, serverApplicationContextResource);

		// start the server

		endpointServer.startServer();
	}
}

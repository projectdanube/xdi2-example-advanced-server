package xdi2.example.server.configured;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import xdi2.server.impl.embedded.XDIEmbeddedServer;

public class ConfiguredEndpointServerSample {

	public static void main(String[] args) throws Exception {

		// read configuration files

		Resource applicationContextResource = new UrlResource(ConfiguredEndpointServerSample.class.getResource("applicationContext.xml"));
		Resource jettyApplicationContextResource = new UrlResource(ConfiguredEndpointServerSample.class.getResource("server-applicationContext.xml"));

		// create the XDI2 server

		XDIEmbeddedServer endpointServer = XDIEmbeddedServer.newServer(applicationContextResource, jettyApplicationContextResource);

		// start the server

		endpointServer.start();
	}
}

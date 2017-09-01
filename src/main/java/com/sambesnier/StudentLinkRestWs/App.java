package com.sambesnier.StudentLinkRestWs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.tyrus.server.Server;

import com.sambesnier.StudentLinkRestWs.websocket.WebSocketVote;

/**
 * Hello world!
 *
 */
public class App {
	public static final String BASE_URI = "http://localhost:8081/studentlink/";

	public static HttpServer startServer() {
		// create a resource config that scans for JAX-RS resources and providers
		// in com.sambesnier.StudentLinkRest package
		final ResourceConfig rc = new ResourceConfig().packages("com.sambesnier.StudentLinkRestWs.resources");

		// create and start a new instance of grizzly http server
		// exposing the Jersey application at BASE_URI
		return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
	}

	public static void main(String[] args) {
		final HttpServer server = startServer();
		System.out.println(String.format(
				"API started with WADL available at " + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));


		Server serverws = new Server("localhost", 8080, "/studentlink", WebSocketVote.class);

		try {
			serverws.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("Websocket started");
			reader.readLine();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			serverws.stop();
		}
	}
}

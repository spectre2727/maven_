package spc;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            Class.forName("org.h2.Driver");
            DriverManager.getConnection(
                    "jdbc:h2:mem:database01;INIT=runscript from 'classpath:init.sql'",
                    "username",
                    "password"
            );
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }

        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.packages(Main.class.getPackageName());
        resourceConfig.register(new AbstractBinder() {

            @Override
            protected void configure() {
                bindAsContract(JavaObjectRepository.class);
            }

        });
        GrizzlyHttpServerFactory.createHttpServer(URI.create("http://0.0.0.0:8080"), resourceConfig);

        JavaObject java = new JavaObject(1, System.getProperty("java.vendor"), System.getProperty("java.version"));
        JavaObject jersey = new JavaObject(2, "Jersey Framework", "2.39.1");
        JavaObject jpa = new JavaObject(3, "Java Persistence API", "2.2");
        JavaObject hibernate = new JavaObject(4, "Hibernate ORM", "5.6.15");
        JavaObject toDelete = new JavaObject(99, "To Delete", "1.0.0");

        List<JavaObject> javaObjects = List.of(java, jersey, jpa, hibernate, toDelete);

        Client client = ClientBuilder.newClient();

        for (JavaObject javaObject : javaObjects) {
            client.target("http://0.0.0.0:8080/objects")
                    .request()
                    .post(Entity.entity(javaObject, MediaType.APPLICATION_JSON))
                    .close();
        }

        java.setVersion("17.0.7");

        client.target("http://0.0.0.0:8080/objects")
                .path("1")
                .request()
                .put(Entity.entity(java, MediaType.APPLICATION_JSON))
                .close();

        client.target("http://0.0.0.0:8080/objects")
                .path("99")
                .request()
                .delete()
                .close();

        javaObjects = client.target("http://0.0.0.0:8080/objects")
                .request()
                .get(new GenericType<>() {});

        client.close();

        JacksonPrettyPrinter jacksonPrettyPrinter = new JacksonPrettyPrinter();
        try {
            jacksonPrettyPrinter.print(javaObjects);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        System.exit(0);
    }

}

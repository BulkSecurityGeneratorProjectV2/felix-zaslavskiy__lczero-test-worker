package lczero.worker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    Client client;


    @Value("${backend:blas}")
    String backend;

    @Bean
    public CommandLineRunner start() {
        return (args) -> {

            Path currentWorkingDir = Paths.get("").toAbsolutePath();

            client.setCwd(currentWorkingDir.normalize().toString());
            client.setBackend(backend);
            client.test();


        };
    }
}

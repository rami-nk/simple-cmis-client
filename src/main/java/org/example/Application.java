package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

        var client = context.getBean(CmisClient.class);

        client.connect();
        var root = client.getRootFolder();

        client.deleteFolder(root, "TEST_FOLDER");
        var folder = client.createFolder(root, "TEST_FOLDER");
        client.createDocument(folder, "TEST_DOC");
    }
}

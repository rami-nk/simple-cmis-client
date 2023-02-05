package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

        var client = context.getBean(CmisClient.class);

        client.connect();
        var root = client.getRootFolder();

        final String folderName = "TEST_FOLDER";
        final String documentName = "TEST_DOCUMENT.txt";

        client.deleteFolder(root, folderName);
        var folder = client.createFolder(root, folderName);
        client.createDocument(folder, documentName, "Hello World".getBytes());
        var content = client.getContent(folder, documentName);
    }
}

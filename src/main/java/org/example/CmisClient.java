package org.example;

import org.apache.chemistry.opencmis.client.api.Folder;

public interface CmisClient {

    void connect();

    Folder getRootFolder();

    void deleteFolder(Folder folder, String name);

    void listFolder(int depth, Folder folder);

    void deleteDocument(Folder folder, String name);

    void createDocument(Folder folder, String name);

    Folder createFolder(Folder folder, String name);
}

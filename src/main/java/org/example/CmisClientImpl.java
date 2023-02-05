package org.example;

import lombok.extern.slf4j.Slf4j;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class CmisClientImpl implements CmisClient {

    private final CmisServerProperties cmisServerProperties;

    private Session session;


    public CmisClientImpl(final CmisServerProperties cmisServerProperties) {
        this.cmisServerProperties = cmisServerProperties;
    }

    @Override
    public Folder getRootFolder() {
        return this.session.getRootFolder();
    }

    @Override
    public void connect() {
        SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
        Map<String, String> parameter = new HashMap<>();

        parameter.put(SessionParameter.USER, cmisServerProperties.getUser());
        parameter.put(SessionParameter.PASSWORD, cmisServerProperties.getPassword());

        parameter.put(SessionParameter.ATOMPUB_URL, this.cmisServerProperties.getAtompub());
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());

        Repository repository = sessionFactory.getRepositories(parameter).get(0);
        session = repository.createSession();
        log.info("Session created.");
    }

    @Override
    public void deleteFolder(Folder folder, String name) {
        try {
            CmisObject object = session.getObjectByPath(folder.getPath() + name);
            Folder delFolder = (Folder) object;
            delFolder.deleteTree(true, UnfileObject.DELETE, true);
            log.info("Deleted folder: {}.", name);
        } catch (CmisObjectNotFoundException e) {
            log.warn("Folder {} not found.", name);
        }
    }

    @Override
    public void listFolder(int depth, Folder folder) {
        String indent = StringUtils.repeat("\t", depth);
        for (CmisObject o : folder.getChildren()) {
            if (BaseTypeId.CMIS_DOCUMENT.equals(o.getBaseTypeId())) {
                log.info("{} [Document] {}", indent, o.getName());
            } else if (BaseTypeId.CMIS_FOLDER.equals(o.getBaseTypeId())) {
                log.info("{} [Folder] {}", indent, o.getName());
                listFolder(++depth, (Folder) o);
            }
        }
    }

    @Override
    public void deleteDocument(Folder folder, String name) {
        try {
            CmisObject object = session.getObjectByPath(folder.getPath()
                    + name);
            Document delDoc = (Document) object;
            delDoc.delete(true);
        } catch (CmisObjectNotFoundException e) {
            log.warn("Document not found: {}.", name);
        }
    }

    @Override
    public void createDocument(Folder folder, String name) {
        Map<String, String> props = new HashMap<>();
        props.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
        props.put(PropertyIds.NAME, name);
        String content = "aegif Mind Share Leader Generating New Paradigms by aegif corporation.";
        byte[] buf;
        buf = content.getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream input = new ByteArrayInputStream(buf);
        ContentStream contentStream = session.getObjectFactory()
                .createContentStream(name, buf.length,
                        "text/plain; charset=UTF-8", input);
        folder.createDocument(props, contentStream, VersioningState.NONE);
        log.info("Created document: {}.", name);
    }

    @Override
    public Folder createFolder(Folder folder, String name) {
        Map<String, String> props = new HashMap<>();
        props.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
        props.put(PropertyIds.NAME, name);
        Folder newFolder = folder.createFolder(props);
        return newFolder;
    }
}
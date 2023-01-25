package org.example;

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

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Connects to read-only alfresco test cmis server.
 * Code from: https://gist.github.com/mryoshio/1465530
 */
public class SampleAlfrescoCmisClient {

    /**
     * This alfresco cmis server is just read-only.
     * Url from https://www.alfresco.com/whitepaper/cmis
     */
    private static final String CRM_URL = "https://cmis.alfresco.com/api/-default-/public/cmis/versions/1.1/atom";

    private static Session session;

    public static void main(String[] args) {
        Folder root = connect();
    }

    /**
     * Connect to alfresco repository
     *
     * @return root folder object
     */
    private static Folder connect() {
        SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
        Map<String, String> parameter = new HashMap<>();
        parameter.put(SessionParameter.USER, "admin");
        parameter.put(SessionParameter.PASSWORD, "admin");

        parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
        parameter.put(SessionParameter.ATOMPUB_URL, CRM_URL);
        parameter.put(SessionParameter.AUTH_HTTP_BASIC, "true");
        parameter.put(SessionParameter.COOKIES, "true");

        Repository repository = sessionFactory.getRepositories(parameter).get(0);
        session = repository.createSession();

        return session.getRootFolder();
    }

    /**
     * Clean up test folder before executing test
     *
     * @param target
     * @param delFolderName
     */
    private static void cleanup(Folder target, String delFolderName) {
        try {
            CmisObject object = session.getObjectByPath(target.getPath()
                    + delFolderName);
            Folder delFolder = (Folder) object;
            delFolder.deleteTree(true, UnfileObject.DELETE, true);
        } catch (CmisObjectNotFoundException e) {
            System.err.println("No need to clean up.");
        }
    }

    /**
     * @param target
     */
    private static void listFolder(int depth, Folder target) {
        String indent = StringUtils.repeat("\t", depth);
        for (Iterator<CmisObject> it = target.getChildren().iterator(); it
                .hasNext(); ) {
            CmisObject o = it.next();
            if (BaseTypeId.CMIS_DOCUMENT.equals(o.getBaseTypeId())) {
                System.out.println(indent + "[Docment] " + o.getName());
            } else if (BaseTypeId.CMIS_FOLDER.equals(o.getBaseTypeId())) {
                System.out.println(indent + "[Folder] " + o.getName());
                listFolder(++depth, (Folder) o);
            }
        }

    }

    /**
     * Delete test document
     *
     * @param target
     * @param delDocName
     */
    private static void DeleteDocument(Folder target, String delDocName) {
        try {
            CmisObject object = session.getObjectByPath(target.getPath()
                    + delDocName);
            Document delDoc = (Document) object;
            delDoc.delete(true);
        } catch (CmisObjectNotFoundException e) {
            System.err.println("Document is not found: " + delDocName);
        }
    }

    /**
     * Create test document with content
     *
     * @param target
     * @param newDocName
     */
    private static void createDocument(Folder target, String newDocName) {
        Map<String, String> props = new HashMap<String, String>();
        props.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
        props.put(PropertyIds.NAME, newDocName);
        System.out.println("This is a test document: " + newDocName);
        String content = "aegif Mind Share Leader Generating New Paradigms by aegif corporation.";
        byte[] buf = null;
        try {
            buf = content.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ByteArrayInputStream input = new ByteArrayInputStream(buf);
        ContentStream contentStream = session.getObjectFactory()
                .createContentStream(newDocName, buf.length,
                        "text/plain; charset=UTF-8", input);
        target.createDocument(props, contentStream, VersioningState.MAJOR);
    }

    /**
     * Create test folder directly under target folder
     *
     * @param target
     * @param newFolderName
     * @return newly created folder
     */
    private static Folder createFolder(Folder target, String newFolderName) {
        Map<String, String> props = new HashMap<String, String>();
        props.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
        props.put(PropertyIds.NAME, newFolderName);
        Folder newFolder = target.createFolder(props);
        return newFolder;
    }
}

package org.example;

public enum CmisObjectType {
    DOCUMENT("cmis:document"),
    FOLDER("cmis:folder");

    CmisObjectType(String type) {
        this.type = type;
    }

    private final String type;

    public String getType() {
        return type;
    }
}

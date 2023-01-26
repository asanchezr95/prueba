package com.santander.rest.migration.model;

public class DocumentationPagesPayload {

    protected String docbase;
    protected String path;
    protected String alias;

    public String getPath ( ) {
        return path;
    }

    public void setPath ( String path ) {
        this.path = path;
    }

    public String getAlias ( ) {
        return alias;
    }

    public void setAlias ( String alias ) {
        this.alias = alias;
    }

    public String getDocbase ( ) {
        return docbase;
    }
    public void setDocbase(String docbase) {
        this.docbase = docbase;
    }
}

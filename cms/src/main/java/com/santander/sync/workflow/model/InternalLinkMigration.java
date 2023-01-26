package com.santander.sync.workflow.model;

public class InternalLinkMigration {
    protected String docbase;
    protected String path;
    protected String alias;

    public String getAlias ( ) {
        return alias;
    }

    public void setAlias ( String alias ) {
        this.alias = alias;
    }

    public String getPath ( ) {
        return path;
    }

    public void setPath ( String path ) {
        this.path = path;
    }

    public String getDocbase() {
        return docbase;
    }

    public void setDocbase(String docbase) {
        this.docbase = docbase;
    }
}

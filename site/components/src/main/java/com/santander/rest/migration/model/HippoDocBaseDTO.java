package com.santander.rest.migration.model;

public class HippoDocBaseDTO {

    protected String docbase;
    protected String alias;
    protected String path;
    protected String type;

    public
    String getType ( ) {
        return type;
    }

    public
    void setType ( String type ) {
        this.type = type;
    }

    public
    String getPath ( ) {
        return path;
    }

    public
    void setPath ( String path ) {
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

    public void setDocbase ( String docbase ) {
        this.docbase = docbase;
    }

}

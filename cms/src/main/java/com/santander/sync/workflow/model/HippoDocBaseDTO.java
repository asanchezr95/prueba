package com.santander.sync.workflow.model;

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

    public
    void setAlias ( String nameNode ) {
        this.alias = nameNode;
    }

    public void setDocbase(String docbase) {
        this.docbase = docbase;
    }

}

package com.santander.rest.migration.model;

import java.util.List;

public class DescriptionPayload {
    protected String                   content;
    protected List < HippoDocBaseDTO > nodes;

    public
    List < HippoDocBaseDTO > getNodes ( ) {
        return nodes;
    }

    public
    void setNodes ( List < HippoDocBaseDTO > nodes ) {
        this.nodes = nodes;
    }

    public String getContent ( ) {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

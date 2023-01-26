package com.santander.sync.workflow.model;

import java.util.List;

public class SectionLevelFourPayload {

    protected String title;
    protected DescriptionPayload description;
    protected List<DocumentationPagesPayload> documentation;

    public DescriptionPayload getDescription() {
        return description;
    }

    public void setDescription(DescriptionPayload description) {
        this.description = description;
    }

    public List<DocumentationPagesPayload> getDocumentation() {
        return documentation;
    }

    public void setDocumentation(List<DocumentationPagesPayload>  documentation) {
        this.documentation = documentation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

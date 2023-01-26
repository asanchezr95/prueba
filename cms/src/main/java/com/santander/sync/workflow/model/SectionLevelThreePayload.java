package com.santander.sync.workflow.model;

import java.util.List;

public class SectionLevelThreePayload {

    protected String title;
    protected DescriptionPayload description;
    protected List<SectionLevelFourPayload> sections;
    protected List<DocumentationPagesPayload> documentation;

    public DescriptionPayload getDescription() {
        return description;
    }

    public void setDescription(DescriptionPayload description) {
        this.description = description;
    }

    public List<SectionLevelFourPayload> getSections() {
        return sections;
    }

    public void setSections(List<SectionLevelFourPayload> sections) {
        this.sections = sections;
    }

    public List<DocumentationPagesPayload> getDocumentation() {
        return documentation;
    }

    public void setDocumentation(List<DocumentationPagesPayload> documentation) {
        this.documentation = documentation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

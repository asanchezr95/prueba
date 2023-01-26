package com.santander.sync.workflow.model;

public class LinksPayloadMigration {
    protected String externalLink;
    protected String tab;
    protected String title;
    protected InternalLinkMigration internalLink;

    public String getExternalLink() {
        return externalLink;
    }

    public void setExternalLink(String externalLink) {
        this.externalLink = externalLink;
    }

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public InternalLinkMigration getInternalLink() {
        return internalLink;
    }

    public void setInternalLink(InternalLinkMigration internalLink) {
        this.internalLink = internalLink;
    }
}

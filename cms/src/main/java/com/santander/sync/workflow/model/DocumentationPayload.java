package com.santander.sync.workflow.model;

import java.util.List;

public class DocumentationPayload {

   protected String folder;
   protected String namePropertieNode;
   protected String nameDocument;
   protected String          type;
   protected HippoDocBaseDTO icon;
   protected String          title;
   protected String alias;
   protected DescriptionPayload description;
   protected List<SectionLevelOnePayload> pages;
   protected List<LinksPayloadMigration> links;

   public void setFolder ( String path ) {
      this.folder = path;
   }

   public String getNamePropertieNode() {
      return namePropertieNode;
   }

   public void setNamePropertieNode(String namePropertieNode) {
      this.namePropertieNode = namePropertieNode;
   }

   public String getNameDocument() {
      return nameDocument;
   }

   public void setNameDocument(String nameDocument) {
      this.nameDocument = nameDocument;
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public String getAlias() {
      return alias;
   }

   public void setAlias(String alias) {
      this.alias = alias;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public DescriptionPayload getDescription() {
      return description;
   }

   public void setDescription(DescriptionPayload description) {
      this.description = description;
   }

   public
   HippoDocBaseDTO getIcon ( ) {
      return icon;
   }

   public void setIcon ( HippoDocBaseDTO icon ) {
      this.icon = icon;
   }

   public List<LinksPayloadMigration> getLinks() {
      return links;
   }

   public void setLinks(List<LinksPayloadMigration> links) {
      this.links = links;
   }

   public List<SectionLevelOnePayload> getPages() {
      return pages;
   }

   public void setPages(List<SectionLevelOnePayload> pages) {
      this.pages = pages;
   }
}

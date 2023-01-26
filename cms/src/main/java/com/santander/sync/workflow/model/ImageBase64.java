package com.santander.sync.workflow.model;

public class ImageBase64 {

    byte[] base64Original;
    byte[]  base64Thumbnail;

    public  byte[]  getBase64Original ( ) {
        return base64Original;
    }

    public void setBase64Original (  byte[]  base64Original ) {
        this.base64Original = base64Original;
    }

    public  byte[]  getBase64Thumbnail ( ) {
        return base64Thumbnail;
    }

    public void setBase64Thumbnail (  byte[]  base64Yhumbnail ) {
        this.base64Thumbnail = base64Yhumbnail;
    }
}

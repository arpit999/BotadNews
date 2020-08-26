package com.bicubic.botadnews.model;

/**
 * Created by admin on 24-Oct-16.
 */

public class News {

    String title,link,type,thumb_image;


    public News(String title, String link, String type, String thumb_image) {
        this.title = title;
        this.link = link;
        this.type = type;
        this.thumb_image = thumb_image;
    }

    public News(String title, String link, String type) {
        this.title = title;
        this.link = link;
        this.type = type;
    }


    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

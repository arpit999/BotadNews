package com.bicubic.botadnews.model;

/**
 * Created by admin on 20-Oct-16.
 */

public class NewsDemo {

    String content_type, image_link, download_link, news_heading;

    public NewsDemo(String content_type, String image_link, String download_link, String news_heading) {
        this.content_type = content_type;
        this.image_link = image_link;
        this.download_link = download_link;
        this.news_heading = news_heading;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public String getLink() {
        return image_link;
    }

    public void setLink(String image_link) {
        this.image_link = image_link;
    }

    public String getDownload_link() {
        return download_link;
    }

    public void setDownload_link(String download_link) {
        this.download_link = download_link;
    }

    public String getNews_heading() {
        return news_heading;
    }

    public void setNews_heading(String news_heading) {
        this.news_heading = news_heading;
    }

}

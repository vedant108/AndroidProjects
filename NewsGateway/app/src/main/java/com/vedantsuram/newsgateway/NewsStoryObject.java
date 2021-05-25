package com.vedantsuram.newsgateway;

import java.io.Serializable;

public class NewsStoryObject implements Serializable {

    private String author;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String publishedAt;
    private int total;
    private int index;

    public NewsStoryObject(String author, String title, String description, String url, String urlToImage, String publishedAt, int total, int index) {
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
        this.total = total;
        this.index = index+1;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public int getTotal() {
        return total;
    }

    public int getIndex() {
        return index;
    }


}

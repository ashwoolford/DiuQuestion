package com.example.ash.diuquestion;

/**
 * Created by ash on 5/10/2017.
 */

public class Data {
    private String post_title, url;

    public Data(){

    }

    public Data(String post_title, String url){
        this.post_title = post_title;
        this.url = url;

    }

    public String getPost_title() {
        return post_title;
    }

    public void setPost_title(String post_title) {
        this.post_title = post_title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

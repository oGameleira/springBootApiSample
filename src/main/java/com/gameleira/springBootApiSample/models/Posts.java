package com.gameleira.springBootApiSample.models;

import java.util.ArrayList;

public class Posts {
    private ArrayList<Post> posts;

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }
}

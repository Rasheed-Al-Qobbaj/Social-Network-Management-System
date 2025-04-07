package com.example.demo;

class PostNode {
    Post post; // Reference to the actual Post object
    PostNode next;

    PostNode(Post post) {
        this.post = post;
        this.next = null;
    }
}
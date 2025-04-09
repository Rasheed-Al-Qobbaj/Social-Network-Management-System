package com.example.demo;

class PostNode {
    Post post;
    PostNode next;

    PostNode(Post post) {
        this.post = post;
        this.next = null;
    }
}
package com.example.demo;

class SharedUserNode {
    User sharedUser; // Reference to the User this post is shared with
    SharedUserNode next;

    SharedUserNode(User sharedUser) {
        this.sharedUser = sharedUser;
        this.next = null;
    }
}

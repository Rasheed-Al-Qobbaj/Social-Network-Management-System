package com.example.demo;

class SharedUserNode {
    User sharedUser;
    SharedUserNode next;

    SharedUserNode(User sharedUser) {
        this.sharedUser = sharedUser;
        this.next = null;
    }
}

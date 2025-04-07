package com.example.demo;

class FriendNode {
    User friend; // Reference to the actual friend User object
    FriendNode next;

    FriendNode(User friend) {
        this.friend = friend;
        this.next = null;
    }
}
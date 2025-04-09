package com.example.demo;

class FriendNode {
    User friend;
    FriendNode next;

    FriendNode(User friend) {
        this.friend = friend;
        this.next = null;
    }
}
package com.example.demo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

class Post {
    int postId;
    int creatorId; // Store creator ID for easier lookup/reference
    String content;
    String creationDate;
    LocalDate parsedDate;
    SharedUserNode sharedWithListHead;


    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d.M.yyyy");


    public Post(int postId, int creatorId, String content, String creationDate) {
        this.postId = postId;
        this.creatorId = creatorId;
        this.content = content;
        this.creationDate = creationDate;
        this.sharedWithListHead = null;
        try {
            this.parsedDate = LocalDate.parse(creationDate, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            System.err.println("Warning: Could not parse date '" + creationDate + "' for Post ID " + postId + ". Using null date.");
            this.parsedDate = null;
        }
    }

    // --- Getters ---
    public int getPostId() { return postId; }
    public int getCreatorId() { return creatorId; }
    public String getContent() { return content; }
    public String getCreationDate() { return creationDate; }
    public LocalDate getParsedDate() { return parsedDate; }
    public SharedUserNode getSharedWithListHead() { return sharedWithListHead; }

    // --- Methods for managing shared users ---

    public void addSharedUser(User user) {
        if (user == null) return;

        // Check if already shared with this user
        SharedUserNode current = sharedWithListHead;
        while (current != null) {
            if (current.sharedUser.getUserId() == user.getUserId()) {
                return; // Already shared
            }
            current = current.next;
        }

        // Add to head
        SharedUserNode newNode = new SharedUserNode(user);
        newNode.next = sharedWithListHead;
        sharedWithListHead = newNode;
    }


    public boolean removeSharedUser(int userId) {
        if (sharedWithListHead == null) {
            return false;
        }

        // Handle head removal
        if (sharedWithListHead.sharedUser.getUserId() == userId) {
            sharedWithListHead = sharedWithListHead.next;
            return true;
        }

        // Handle removal elsewhere in the list
        SharedUserNode current = sharedWithListHead;
        while (current.next != null) {
            if (current.next.sharedUser.getUserId() == userId) {
                current.next = current.next.next; // Bypass the node to remove
                return true;
            }
            current = current.next;
        }
        return false; // User not found
    }


    @Override
    public String toString() {
        return "Post ID: " + postId + ", Content: \"" + content + "\", Date: " + creationDate + ", CreatorID: " + creatorId;
    }
}

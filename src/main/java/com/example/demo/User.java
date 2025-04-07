package com.example.demo;

class User {
    int userId;
    String name;
    int age;
    FriendNode friendsListHead;       // Head of list of friends (User objects)
    PostNode postsCreatedHead;      // Head of list of posts created by this user (Post objects)
    PostNode postsSharedWithMeHead; // Head of list of posts shared with this user (Post objects)

    public User(int userId, String name, int age) {
        this.userId = userId;
        this.name = name;
        this.age = age;
        this.friendsListHead = null;
        this.postsCreatedHead = null;
        this.postsSharedWithMeHead = null;
    }

    // --- Getters ---
    public int getUserId() { return userId; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public FriendNode getFriendsListHead() { return friendsListHead; }
    public PostNode getPostsCreatedHead() { return postsCreatedHead; }
    public PostNode getPostsSharedWithMeHead() { return postsSharedWithMeHead; }

    // --- Setters (Example for age) ---
    public void setAge(int age) { this.age = age; }
    public void setName(String name) { this.name = name;} // Added setter for name update


    // --- Methods for managing friends ---

    /**
     * Adds a friend to this user's friend list. Avoids duplicates.
     * Note: This adds the friend to *this* user's list. Call it again on the
     * other user object to make the friendship reciprocal if needed.
     * @param friendUser The User object representing the friend to add.
     */
    public void addFriend(User friendUser) {
        if (friendUser == null || friendUser.getUserId() == this.userId) return; // Cannot friend self

        // Check for duplicates
        FriendNode current = friendsListHead;
        while (current != null) {
            if (current.friend.getUserId() == friendUser.getUserId()) {
                return; // Already friends
            }
            current = current.next;
        }

        // Add to head
        FriendNode newFriendNode = new FriendNode(friendUser);
        newFriendNode.next = friendsListHead;
        friendsListHead = newFriendNode;
    }

    /**
     * Removes a friend from this user's friend list by ID.
     * Note: This removes the friend from *this* user's list. Call it again on the
     * other user object to make the removal reciprocal if needed.
     * @param friendId The ID of the friend to remove.
     * @return true if the friend was found and removed, false otherwise.
     */
    public boolean removeFriend(int friendId) {
        if (friendsListHead == null) {
            return false;
        }

        // Handle head removal
        if (friendsListHead.friend.getUserId() == friendId) {
            friendsListHead = friendsListHead.next;
            return true;
        }

        // Handle removal elsewhere
        FriendNode current = friendsListHead;
        while (current.next != null) {
            if (current.next.friend.getUserId() == friendId) {
                current.next = current.next.next; // Bypass the node
                return true;
            }
            current = current.next;
        }
        return false; // Friend not found
    }

    // --- Methods for managing posts ---

    /** Adds a post created by this user to their 'postsCreated' list. */
    public void addCreatedPost(Post post) {
        if (post == null) return;
        PostNode newNode = new PostNode(post);
        newNode.next = postsCreatedHead;
        postsCreatedHead = newNode;
    }

    /** Removes a post from the 'postsCreated' list by Post ID. */
    public boolean removeCreatedPost(int postId) {
        if (postsCreatedHead == null) return false;

        if (postsCreatedHead.post.getPostId() == postId) {
            postsCreatedHead = postsCreatedHead.next;
            return true;
        }

        PostNode current = postsCreatedHead;
        while (current.next != null) {
            if (current.next.post.getPostId() == postId) {
                current.next = current.next.next;
                return true;
            }
            current = current.next;
        }
        return false;
    }


    /** Adds a post shared with this user to their 'postsSharedWithMe' list. Avoids duplicates. */
    public void addSharedPost(Post post) {
        if (post == null) return;

        // Check for duplicates
        PostNode current = postsSharedWithMeHead;
        while(current != null) {
            if (current.post.getPostId() == post.getPostId()) {
                return; // Already in the list
            }
            current = current.next;
        }

        PostNode newNode = new PostNode(post);
        newNode.next = postsSharedWithMeHead;
        postsSharedWithMeHead = newNode;
    }

    /** Removes a post from the 'postsSharedWithMe' list by Post ID. */
    public boolean removeSharedPost(int postId) {
        if (postsSharedWithMeHead == null) return false;

        if (postsSharedWithMeHead.post.getPostId() == postId) {
            postsSharedWithMeHead = postsSharedWithMeHead.next;
            return true;
        }

        PostNode current = postsSharedWithMeHead;
        while (current.next != null) {
            if (current.next.post.getPostId() == postId) {
                current.next = current.next.next;
                return true;
            }
            current = current.next;
        }
        return false;
    }


    @Override
    public String toString() {
        return "User ID: " + userId + ", Name: " + name + ", Age: " + age;
    }

    // --- Helper method for counting posts ---
    public int countCreatedPosts() {
        int count = 0;
        PostNode current = postsCreatedHead;
        while(current != null) {
            count++;
            current = current.next;
        }
        return count;
    }
    public int countSharedPosts() {
        int count = 0;
        PostNode current = postsSharedWithMeHead;
        while(current != null) {
            count++;
            current = current.next;
        }
        return count;
    }
}


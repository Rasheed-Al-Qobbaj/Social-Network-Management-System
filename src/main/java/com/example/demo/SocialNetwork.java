package com.example.demo;

import java.io.*;
import java.util.Scanner;

class SocialNetwork {
    private UserNode userListHead; // Head of the main linked list of all users
    private int nextPostId = 1; // Simple way to generate unique post IDs

    public SocialNetwork() {
        this.userListHead = null;
    }

    // --- Find User Methods ---

    /**
     * Finds a user by their ID.
     */
    public User findUserById(int userId) {
        UserNode current = userListHead;
        while (current != null) {
            if (current.user.getUserId() == userId) {
                return current.user;
            }
            current = current.next;
        }
        return null; // Not found
    }

    /**
     * Finds a user by their name (case-insensitive). Returns the first match.
     */
    public User findUserByName(String name) {
        UserNode current = userListHead;
        while (current != null) {
            if (current.user.getName().equalsIgnoreCase(name)) {
                return current.user;
            }
            current = current.next;
        }
        return null; // Not found
    }

    // --- File Loading Methods ---

    /**
     * Loads user data from the specified file.
     * Handles basic errors and format inconsistencies.
     *
     * @param filename Path to the users.txt file.
     */
    public void loadUsers(String filename) {
        File file = new File(filename);
        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) {
                scanner.nextLine(); // Skip header line
            }
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue; // Skip empty lines

                String[] parts = line.split(",");
                if (parts.length == 3) {
                    try {
                        int id = Integer.parseInt(parts[0].trim());
                        String name = parts[1].trim();
                        int age = Integer.parseInt(parts[2].trim());

                        // Check for duplicate ID before adding
                        if (findUserById(id) == null) {
                            User newUser = new User(id, name, age);
                            // Add user to the main list (add to head for simplicity)
                            UserNode newNode = new UserNode(newUser);
                            newNode.next = userListHead;
                            userListHead = newNode;
                        } else {
                            System.err.println("Warning: Duplicate User ID " + id + " found in " + filename + ". Skipping.");
                        }

                    } catch (NumberFormatException e) {
                        System.err.println("Warning: Invalid number format in users file line: " + line + ". Skipping.");
                    }
                } else {
                    System.err.println("Warning: Invalid format in users file line: " + line + ". Skipping.");
                }
            }
            System.out.println("Users loaded successfully from " + filename);
        } catch (FileNotFoundException e) {
            System.err.println("Error: Users file not found: " + filename);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while loading users: " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Loads friendship data from the specified file.
     * Assumes users are already loaded.
     *
     * @param filename Path to the friendships.txt file.
     */
    public void loadFriendships(String filename) {
        File file = new File(filename);
        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) {
                scanner.nextLine(); // Skip header line (e.g., "User ID,Friends")
            }
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length >= 1) { // Need at least the user ID
                    try {
                        int userId = Integer.parseInt(parts[0].trim());
                        User user = findUserById(userId);

                        if (user != null) {
                            for (int i = 1; i < parts.length; i++) { // Iterate through friend IDs
                                try {
                                    int friendId = Integer.parseInt(parts[i].trim());
                                    User friend = findUserById(friendId);
                                    if (friend != null) {
                                        // Add friendship (make it reciprocal)
                                        addFriendship(userId, friendId);
                                    } else {
                                        System.err.println("Warning: Friend ID " + friendId + " not found for user " + userId + ". Skipping friendship.");
                                    }
                                } catch (NumberFormatException e) {
                                    System.err.println("Warning: Invalid friend ID format for user " + userId + " in line: " + line + ". Skipping friend ID.");
                                }
                            }
                        } else {
                            System.err.println("Warning: User ID " + userId + " not found for friendship in line: " + line + ". Skipping line.");
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Warning: Invalid User ID format in friendships file line: " + line + ". Skipping line.");
                    }
                } else {
                    System.err.println("Warning: Invalid format in friendships file line: " + line + ". Skipping.");
                }
            }
            System.out.println("Friendships loaded successfully from " + filename);
        } catch (FileNotFoundException e) {
            System.err.println("Error: Friendships file not found: " + filename);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while loading friendships: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads post data from the specified file.
     * Assumes users are already loaded.
     * Updates nextPostId to avoid conflicts.
     *
     * @param filename Path to the posts.txt file.
     */
    public void loadPosts(String filename) {
        File file = new File(filename);
        int maxPostId = 0; // Track max loaded ID
        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) {
                scanner.nextLine(); // Skip header line
            }
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                // Expecting: PostID,CreatorID,Content,CreationDate,SharedWithID1,SharedWithID2,...
                if (parts.length >= 4) {
                    try {
                        int postId = Integer.parseInt(parts[0].trim());
                        int creatorId = Integer.parseInt(parts[1].trim());
                        String content = parts[2].trim();
                        String date = parts[3].trim();

                        User creator = findUserById(creatorId);
                        if (creator == null) {
                            System.err.println("Warning: Creator User ID " + creatorId + " not found for Post ID " + postId + ". Skipping post.");
                            continue;
                        }

                        Post newPost = new Post(postId, creatorId, content, date);
                        creator.addCreatedPost(newPost); // Add to creator's list

                        // Keep track of the highest post ID found in the file
                        if (postId >= nextPostId) {
                            nextPostId = postId + 1;
                        }

                        // Process shared users
                        for (int i = 4; i < parts.length; i++) {
                            try {
                                int sharedWithId = Integer.parseInt(parts[i].trim());
                                User sharedUser = findUserById(sharedWithId);
                                if (sharedUser != null) {
                                    newPost.addSharedUser(sharedUser);           // Add user to post's shared list
                                    sharedUser.addSharedPost(newPost);          // Add post to user's shared list
                                } else {
                                    System.err.println("Warning: Shared-with User ID " + sharedWithId + " not found for Post ID " + postId + ". Skipping share.");
                                }
                            } catch (NumberFormatException e) {
                                System.err.println("Warning: Invalid Shared-with User ID format for Post ID " + postId + " in line: " + line + ". Skipping share.");
                            }
                        }

                    } catch (NumberFormatException e) {
                        System.err.println("Warning: Invalid Post/Creator ID format in posts file line: " + line + ". Skipping line.");
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.err.println("Warning: Missing fields in posts file line: " + line + ". Skipping line.");
                    }
                } else {
                    System.err.println("Warning: Invalid format in posts file line (minimum 4 fields required): " + line + ". Skipping.");
                }
            }
            System.out.println("Posts loaded successfully from " + filename);
        } catch (FileNotFoundException e) {
            System.err.println("Error: Posts file not found: " + filename);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while loading posts: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // --- User Management Operations ---

    /**
     * Adds a new user to the system.
     */
    public boolean addUser(int userId, String name, int age) {
        if (findUserById(userId) != null) {
            System.err.println("Error: User with ID " + userId + " already exists.");
            return false;
        }
        User newUser = new User(userId, name, age);
        UserNode newNode = new UserNode(newUser);
        newNode.next = userListHead; // Add to head
        userListHead = newNode;
        System.out.println("User '" + name + "' (ID: " + userId + ") added successfully.");
        return true;
    }

    /**
     * Updates an existing user's information (e.g., age or name).
     */
    public boolean updateUser(int userId, String newName, int newAge) {
        User user = findUserById(userId);
        if (user != null) {
            user.setName(newName); // Assuming setter exists
            user.setAge(newAge);   // Assuming setter exists
            System.out.println("User ID " + userId + " updated successfully.");
            return true;
        } else {
            System.err.println("Error: User ID " + userId + " not found for update.");
            return false;
        }
    }

    /**
     * Deletes a user and handles cascading removals.
     */
    public boolean deleteUser(int userId) {
        User userToDelete = findUserById(userId);
        if (userToDelete == null) {
            System.err.println("Error: User ID " + userId + " not found for deletion.");
            return false;
        }

        // 1. Remove posts created by this user (and from others' shared lists)
        PostNode currentPostNode = userToDelete.getPostsCreatedHead();
        while (currentPostNode != null) {
            deletePostInternal(currentPostNode.post.getPostId(), userId, false); // Delete owned posts
            currentPostNode = currentPostNode.next; // Move to next BEFORE deleting
        }
        userToDelete.postsCreatedHead = null; // Clear the list head just in case


        // 2. Remove this user from other users' friend lists
        // 3. Remove posts *shared with* this user (created by others) from this user's list
        // 4. Remove this user from the 'sharedWith' list of posts created by others
        UserNode current = userListHead;
        while (current != null) {
            if (current.user.getUserId() != userId) { // Don't process the user being deleted
                // Remove from friend lists
                current.user.removeFriend(userId);

                // Remove posts created by others from the deleted user's 'sharedWithMe' list
                // (This user's lists will be garbage collected, but good practice to unlink)
                // Also, remove the deleted user from the Post's sharedWith list
                PostNode sharedPostNode = current.user.getPostsCreatedHead(); // Check posts created by *others*
                while (sharedPostNode != null) {
                    sharedPostNode.post.removeSharedUser(userId); // Remove deleted user from post's share list
                    sharedPostNode = sharedPostNode.next;
                }

                // We also need to iterate the 'sharedWithMe' list of the user being deleted,
                // find the original post, and remove the deleted user from THAT post's shared list.
                // This is slightly redundant if the previous loop catches all posts, but safer.
                PostNode sharedWithDeleted = userToDelete.getPostsSharedWithMeHead();
                while (sharedWithDeleted != null) {
                    sharedWithDeleted.post.removeSharedUser(userId);
                    sharedWithDeleted = sharedWithDeleted.next;
                }

            }
            current = current.next;
        }
        userToDelete.postsSharedWithMeHead = null; // Clear list head


        // 5. Remove the user from the main user list
        if (userListHead.user.getUserId() == userId) {
            userListHead = userListHead.next; // Remove head
        } else {
            current = userListHead;
            while (current.next != null) {
                if (current.next.user.getUserId() == userId) {
                    current.next = current.next.next; // Bypass the node
                    break;
                }
                current = current.next;
            }
        }

        System.out.println("User ID " + userId + " and associated data deleted successfully.");
        return true;
    }


    // --- Friendship Management ---

    /**
     * Adds a reciprocal friendship between two users.
     */
    public boolean addFriendship(int userId1, int userId2) {
        User user1 = findUserById(userId1);
        User user2 = findUserById(userId2);

        if (user1 != null && user2 != null) {
            if (userId1 == userId2) {
                System.err.println("Error: Cannot add self as friend.");
                return false;
            }
            user1.addFriend(user2);
            user2.addFriend(user1);
            System.out.println("Friendship added between " + user1.getName() + " and " + user2.getName());
            return true;
        } else {
            if (user1 == null) System.err.println("Error: User ID " + userId1 + " not found for friendship.");
            if (user2 == null) System.err.println("Error: User ID " + userId2 + " not found for friendship.");
            return false;
        }
    }

    /**
     * Removes a reciprocal friendship between two users.
     */
    public boolean removeFriendship(int userId1, int userId2) {
        User user1 = findUserById(userId1);
        User user2 = findUserById(userId2);
        boolean removed1 = false, removed2 = false;

        if (user1 != null) {
            removed1 = user1.removeFriend(userId2);
        } else {
            System.err.println("Error: User ID " + userId1 + " not found for removing friendship.");
        }
        if (user2 != null) {
            removed2 = user2.removeFriend(userId1);
        } else {
            System.err.println("Error: User ID " + userId2 + " not found for removing friendship.");
        }

        if (removed1 || removed2) { // If removal happened on at least one side
            System.out.println("Friendship removed between users " + userId1 + " and " + userId2);
            return true;
        } else if (user1 != null && user2 != null) {
            System.out.println("Users " + userId1 + " and " + userId2 + " were not friends.");
            return false; // Indicate they weren't friends to begin with
        } else {
            return false; // Indicate failure due to user not found
        }
    }

    // --- Post Management ---

    /**
     * Creates a new post and shares it accordingly.
     */
    public boolean createPost(int creatorId, String content, String creationDate, int[] sharedWithIds) {
        User creator = findUserById(creatorId);
        if (creator == null) {
            System.err.println("Error: Creator User ID " + creatorId + " not found.");
            return false;
        }

        int postId = nextPostId++; // Assign and increment the next available ID
        Post newPost = new Post(postId, creatorId, content, creationDate);
        creator.addCreatedPost(newPost);

        System.out.print("Post ID " + postId + " created by " + creator.getName() + ".");

        if (sharedWithIds != null && sharedWithIds.length > 0) {
            System.out.print(" Sharing with IDs: ");
            for (int sharedId : sharedWithIds) {
                User sharedUser = findUserById(sharedId);
                if (sharedUser != null) {
                    if (sharedUser.getUserId() != creatorId) { // Don't share with self explicitly here
                        newPost.addSharedUser(sharedUser);
                        sharedUser.addSharedPost(newPost);
                        System.out.print(sharedId + " ");
                    }
                } else {
                    System.err.print("\nWarning: User ID " + sharedId + " not found for sharing post " + postId + ". Skipping.");
                }
            }
            System.out.println();
        } else {
            System.out.println(" (Not shared with specific users).");
            // Handle sharing with "all friends" if needed - iterate creator's friend list
            // FriendNode currentFriend = creator.getFriendsListHead();
            // while (currentFriend != null) {
            //     newPost.addSharedUser(currentFriend.friend);
            //     currentFriend.friend.addSharedPost(newPost);
            //     currentFriend = currentFriend.next;
            // }
            // System.out.println(" (Shared with all friends).");
        }


        return true;
    }


    /**
     * Deletes a post. Only the creator can delete their own post entirely.
     * If cascade is true, removes the post from all users' shared lists as well.
     * Internal helper to distinguish between user action and cascade delete.
     */
    private boolean deletePostInternal(int postId, int requestingUserId, boolean cascade) {
        User requester = findUserById(requestingUserId);
        if (requester == null) {
            System.err.println("Error: Requesting user ID " + requestingUserId + " not found.");
            return false; // Should not happen if called correctly
        }

        // Find the post in the creator's list first to get the Post object
        Post postToDelete = null;
        User creator = null;

        UserNode currentUserNode = userListHead;
        while (currentUserNode != null && postToDelete == null) {
            PostNode currentPostNode = currentUserNode.user.getPostsCreatedHead();
            while (currentPostNode != null) {
                if (currentPostNode.post.getPostId() == postId) {
                    postToDelete = currentPostNode.post;
                    creator = currentUserNode.user;
                    break; // Found the post and its creator
                }
                currentPostNode = currentPostNode.next;
            }
            if (postToDelete != null) break; // Exit outer loop once found
            currentUserNode = currentUserNode.next;
        }


        if (postToDelete == null) {
            // Post might only exist in someone's 'sharedWithMe' list if creator was deleted improperly?
            // Or simply doesn't exist.
            if (!cascade) System.err.println("Error: Post ID " + postId + " not found.");
            return false;
        }

        // Check if the requester is the creator
        if (postToDelete.getCreatorId() != requestingUserId) {
            // User is trying to delete a post they didn't create.
            // Allow them to remove it from *their* shared view only.
            boolean removedFromView = requester.removeSharedPost(postId);
            if (removedFromView) {
                System.out.println("Post ID " + postId + " removed from " + requester.getName() + "'s view.");
                return true; // Indicate success in removing from view
            } else {
                System.err.println("Error: Post ID " + postId + " not created by user " + requestingUserId + " and not found in their shared posts.");
                return false;
            }

        }

        // --- If we reach here, the requester IS the creator ---
        // Proceed with full deletion

        // 1. Remove the post from the 'sharedWithMe' list of all users it was shared with
        SharedUserNode sharedNode = postToDelete.getSharedWithListHead();
        while (sharedNode != null) {
            sharedNode.sharedUser.removeSharedPost(postId);
            sharedNode = sharedNode.next;
        }

        // 2. Remove the post from the creator's 'postsCreated' list
        boolean removedFromCreator = creator.removeCreatedPost(postId);

        if (removedFromCreator) {
            System.out.println("Post ID " + postId + " deleted successfully by creator " + creator.getName() + ".");
            // The Post object should now be eligible for garbage collection if no other refs exist
            return true;
        } else {
            // This case should ideally not happen if we found the post earlier
            System.err.println("Internal Error: Failed to remove post " + postId + " from creator's list after finding it.");
            return false;
        }
    }

    /**
     * Public facing method for post deletion
     */
    public boolean deletePost(int postId, int requestingUserId) {
        return deletePostInternal(postId, requestingUserId, true); // User initiated delete is always cascade
    }


    // --- Reporting Methods (Stubs - Implement Logic) ---

    public void displayPostsCreatedByUser(int userId) {
        User user = findUserById(userId);
        if (user == null) {
            System.out.println("User ID " + userId + " not found.");
            return;
        }
        System.out.println("\n--- Posts Created by " + user.getName() + " (ID: " + userId + ") ---");
        PostNode current = user.getPostsCreatedHead();
        if (current == null) {
            System.out.println("No posts created by this user.");
        }
        while (current != null) {
            System.out.println(current.post); // Uses Post's toString()
            // Optionally print who it was shared with
            System.out.print("   Shared With: ");
            SharedUserNode shared = current.post.getSharedWithListHead();
            if (shared == null) System.out.print("None");
            while (shared != null) {
                System.out.print(shared.sharedUser.getName() + " (ID:" + shared.sharedUser.getUserId() + ") ");
                shared = shared.next;
            }
            System.out.println();

            current = current.next;
        }
        System.out.println("------------------------------------------");
    }

    public void displayPostsSharedWithUser(int userId) {
        User user = findUserById(userId);
        if (user == null) {
            System.out.println("User ID " + userId + " not found.");
            return;
        }
        System.out.println("\n--- Posts Shared with " + user.getName() + " (ID: " + userId + ") ---");
        PostNode current = user.getPostsSharedWithMeHead();
        if (current == null) {
            System.out.println("No posts shared with this user.");
        }
        while (current != null) {
            User creator = findUserById(current.post.getCreatorId());
            String creatorName = (creator != null) ? creator.getName() : "Unknown";
            System.out.println(current.post + " (Creator: " + creatorName + ")"); // Uses Post's toString()
            current = current.next;
        }
        System.out.println("------------------------------------------");
    }

    public void displayMostActiveUsers(int n) {
        System.out.println("\n--- Top " + n + " Most Active Users ---");
        // --- Complex Logic Required ---
        // 1. Iterate through all users.
        // 2. For each user, count posts created (potentially within the last 3 weeks).
        //    - Need java.time.LocalDate, ChronoUnit.WEEKS.between(postDate, now) <= 3
        //    - Need to handle null post dates.
        // 3. Store user references/IDs and their counts (e.g., in a temporary array or list).
        // 4. Sort this temporary structure by count (descending).
        // 5. Print the top N users from the sorted structure.
        // Note: Since we cannot use library collections like ArrayList/HashMap for the core logic,
        // implementing the sorting efficiently without them (e.g., using a temporary array and
        // a simple sort like bubble sort or insertion sort) is necessary.

        System.out.println("Most Active Users report not fully implemented yet.");
        // Placeholder: Print total posts per user for now
        UserNode current = userListHead;
        if (current == null) {
            System.out.println("No users in the network.");
            return;
        }
        System.out.println("(Simplified: Showing total created posts per user)");
        while (current != null) {
            System.out.println(" - " + current.user.getName() + ": " + current.user.countCreatedPosts() + " posts created");
            current = current.next;
        }


        System.out.println("---------------------------------");
    }

    public void displayEngagementMetrics(int userId) {
        User user = findUserById(userId);
        if (user == null) {
            System.out.println("User ID " + userId + " not found.");
            return;
        }
        System.out.println("\n--- Engagement Metrics for " + user.getName() + " ---");
        System.out.println("Posts Created: " + user.countCreatedPosts());
        System.out.println("Posts Shared With User: " + user.countSharedPosts());
        // Could add more metrics like number of friends, etc.
        System.out.println("------------------------------------------");
    }


    // --- Data Saving Methods (Stubs - Implement Logic) ---

    /**
     * Saves a report of posts created by each user to a file.
     */
    public void savePostsCreatedReport(String filename /*, boolean sortedByName */) {
        System.out.println("Saving Posts Created Report to " + filename + "...");
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Posts Created Report");
            writer.println("====================");

            // TODO: Add sorting logic if required (e.g., collect users, sort, then iterate)

            UserNode current = userListHead;
            while (current != null) {
                writer.println("\nUser: " + current.user.getName() + " (ID: " + current.user.getUserId() + ")");
                PostNode postNode = current.user.getPostsCreatedHead();
                if (postNode == null) {
                    writer.println("  No posts created.");
                }
                while (postNode != null) {
                    Post post = postNode.post;
                    writer.print("  - Post ID: " + post.getPostId());
                    writer.print(", Content: " + post.getContent());
                    writer.print(", " + post.getCreationDate());
                    // Print shared with list
                    writer.print(", Shared With: ");
                    SharedUserNode shared = post.getSharedWithListHead();
                    if (shared == null) {
                        writer.print("None");
                    } else {
                        boolean first = true;
                        while (shared != null) {
                            if (!first) writer.print(", ");
                            writer.print(shared.sharedUser.getName()); // Example: Just name
                            shared = shared.next;
                            first = false;
                        }
                    }
                    writer.println();
                    postNode = postNode.next;
                }
                current = current.next;
            }
            System.out.println("Report saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving posts created report: " + e.getMessage());
        }
    }

    /**
     * Saves a report of posts shared with each user to a file.
     */
    public void savePostsSharedWithReport(String filename /*, boolean sortedByName */) {
        System.out.println("Saving Posts Shared With Report to " + filename + "...");
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Posts Shared With User Report");
            writer.println("=============================");

            // TODO: Add sorting logic if required

            UserNode current = userListHead;
            while (current != null) {
                writer.println("\nUser: " + current.user.getName() + " (ID: " + current.user.getUserId() + ")");
                PostNode postNode = current.user.getPostsSharedWithMeHead();
                if (postNode == null) {
                    writer.println("  No posts shared with this user.");
                }
                while (postNode != null) {
                    Post post = postNode.post;
                    User creator = findUserById(post.getCreatorId());
                    String creatorName = (creator != null) ? creator.getName() : "Unknown";

                    writer.print("  - Post ID: " + post.getPostId());
                    writer.print(", Content: " + post.getContent());
                    writer.print(", " + post.getCreationDate());
                    writer.println(", Creator: " + creatorName + " (ID: " + post.getCreatorId() + ")");

                    postNode = postNode.next;
                }
                current = current.next;
            }
            System.out.println("Report saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving posts shared with report: " + e.getMessage());
        }
    }
}

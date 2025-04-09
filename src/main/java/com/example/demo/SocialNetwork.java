package com.example.demo;

import java.io.*;
import java.util.Scanner;

class SocialNetwork {
    private UserNode userListHead;
    private int nextPostId = 1;

    public SocialNetwork() {
        this.userListHead = null;
    }

    // --- Find User Methods ---

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
                            // Add user to the main list
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


    public void loadFriendships(String filename) {
        File file = new File(filename);
        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) {
                scanner.nextLine(); // Skip header line
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
                                        // Add friendship
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

    public void loadPosts(String filename) {
        File file = new File(filename);
        int maxPostId = 0;
        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
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
                        creator.addCreatedPost(newPost);

                        if (postId >= nextPostId) {
                            nextPostId = postId + 1;
                        }

                        for (int i = 4; i < parts.length; i++) {
                            try {
                                int sharedWithId = Integer.parseInt(parts[i].trim());
                                User sharedUser = findUserById(sharedWithId);
                                if (sharedUser != null) {
                                    newPost.addSharedUser(sharedUser);
                                    sharedUser.addSharedPost(newPost);
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

    public boolean addUser(int userId, String name, int age) {
        if (findUserById(userId) != null) {
            System.err.println("Error: User with ID " + userId + " already exists.");
            return false;
        }
        User newUser = new User(userId, name, age);
        UserNode newNode = new UserNode(newUser);
        newNode.next = userListHead;
        userListHead = newNode;
        System.out.println("User '" + name + "' (ID: " + userId + ") added successfully.");
        return true;
    }

    public boolean updateUser(int userId, String newName, int newAge) {
        User user = findUserById(userId);
        if (user != null) {
            user.setName(newName);
            user.setAge(newAge);
            System.out.println("User ID " + userId + " updated successfully.");
            return true;
        } else {
            System.err.println("Error: User ID " + userId + " not found for update.");
            return false;
        }
    }

    public boolean deleteUser(int userId) {
        User userToDelete = findUserById(userId);
        if (userToDelete == null) {
            System.err.println("Error: User ID " + userId + " not found for deletion.");
            return false;
        }

        PostNode currentPostNode = userToDelete.getPostsCreatedHead();
        while (currentPostNode != null) {
            deletePostInternal(currentPostNode.post.getPostId(), userId, false);
            currentPostNode = currentPostNode.next;
        }
        userToDelete.postsCreatedHead = null;

        UserNode current = userListHead;
        while (current != null) {
            if (current.user.getUserId() != userId) {
                current.user.removeFriend(userId);

                PostNode sharedPostNode = current.user.getPostsCreatedHead();
                while (sharedPostNode != null) {
                    sharedPostNode.post.removeSharedUser(userId);
                    sharedPostNode = sharedPostNode.next;
                }

                PostNode sharedWithDeleted = userToDelete.getPostsSharedWithMeHead();
                while (sharedWithDeleted != null) {
                    sharedWithDeleted.post.removeSharedUser(userId);
                    sharedWithDeleted = sharedWithDeleted.next;
                }

            }
            current = current.next;
        }
        userToDelete.postsSharedWithMeHead = null;


        if (userListHead.user.getUserId() == userId) {
            userListHead = userListHead.next;
        } else {
            current = userListHead;
            while (current.next != null) {
                if (current.next.user.getUserId() == userId) {
                    current.next = current.next.next;
                    break;
                }
                current = current.next;
            }
        }

        System.out.println("User ID " + userId + " and associated data deleted successfully.");
        return true;
    }


    // --- Friendship Management ---

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

        if (removed1 || removed2) {
            System.out.println("Friendship removed between users " + userId1 + " and " + userId2);
            return true;
        } else if (user1 != null && user2 != null) {
            System.out.println("Users " + userId1 + " and " + userId2 + " were not friends.");
            return false;
        } else {
            return false;
        }
    }

    // --- Post Management ---

    public boolean createPost(int creatorId, String content, String creationDate, int[] sharedWithIds) {
        User creator = findUserById(creatorId);
        if (creator == null) {
            System.err.println("Error: Creator User ID " + creatorId + " not found.");
            return false;
        }

        int postId = nextPostId++;
        Post newPost = new Post(postId, creatorId, content, creationDate);
        creator.addCreatedPost(newPost);

        System.out.print("Post ID " + postId + " created by " + creator.getName() + ".");

        if (sharedWithIds != null && sharedWithIds.length > 0) {
            System.out.print(" Sharing with IDs: ");
            for (int sharedId : sharedWithIds) {
                User sharedUser = findUserById(sharedId);
                if (sharedUser != null) {
                    if (sharedUser.getUserId() != creatorId) {
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
        }


        return true;
    }


    private boolean deletePostInternal(int postId, int requestingUserId, boolean cascade) {
        User requester = findUserById(requestingUserId);
        if (requester == null) {
            System.err.println("Error: Requesting user ID " + requestingUserId + " not found.");
            return false;
        }

        Post postToDelete = null;
        User creator = null;

        UserNode currentUserNode = userListHead;
        while (currentUserNode != null && postToDelete == null) {
            PostNode currentPostNode = currentUserNode.user.getPostsCreatedHead();
            while (currentPostNode != null) {
                if (currentPostNode.post.getPostId() == postId) {
                    postToDelete = currentPostNode.post;
                    creator = currentUserNode.user;
                    break;
                }
                currentPostNode = currentPostNode.next;
            }
            if (postToDelete != null) break;
            currentUserNode = currentUserNode.next;
        }


        if (postToDelete == null) {
            if (!cascade) System.err.println("Error: Post ID " + postId + " not found.");
            return false;
        }


        if (postToDelete.getCreatorId() != requestingUserId) {
            boolean removedFromView = requester.removeSharedPost(postId);
            if (removedFromView) {
                System.out.println("Post ID " + postId + " removed from " + requester.getName() + "'s view.");
                return true;
            } else {
                System.err.println("Error: Post ID " + postId + " not created by user " + requestingUserId + " and not found in their shared posts.");
                return false;
            }

        }


        SharedUserNode sharedNode = postToDelete.getSharedWithListHead();
        while (sharedNode != null) {
            sharedNode.sharedUser.removeSharedPost(postId);
            sharedNode = sharedNode.next;
        }


        boolean removedFromCreator = creator.removeCreatedPost(postId);

        if (removedFromCreator) {
            System.out.println("Post ID " + postId + " deleted successfully by creator " + creator.getName() + ".");

            return true;
        } else {
            System.err.println("Internal Error: Failed to remove post " + postId + " from creator's list after finding it.");
            return false;
        }
    }

    public boolean deletePost(int postId, int requestingUserId) {
        return deletePostInternal(postId, requestingUserId, true);
    }



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
            System.out.println(current.post);
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
            System.out.println(current.post + " (Creator: " + creatorName + ")");
            current = current.next;
        }
        System.out.println("------------------------------------------");
    }

    public void displayMostActiveUsers(int n) {
        System.out.println("\n--- Top " + n + " Most Active Users ---");

        System.out.println("Most Active Users report not fully implemented yet.");
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
        System.out.println("------------------------------------------");
    }


    public void savePostsCreatedReport(String filename /*, boolean sortedByName */) {
        System.out.println("Saving Posts Created Report to " + filename + "...");
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Posts Created Report");
            writer.println("====================");


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
                    writer.print(", Shared With: ");
                    SharedUserNode shared = post.getSharedWithListHead();
                    if (shared == null) {
                        writer.print("None");
                    } else {
                        boolean first = true;
                        while (shared != null) {
                            if (!first) writer.print(", ");
                            writer.print(shared.sharedUser.getName());
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

    public void savePostsSharedWithReport(String filename /*, boolean sortedByName */) {
        System.out.println("Saving Posts Shared With Report to " + filename + "...");
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Posts Shared With User Report");
            writer.println("=============================");

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

    public UserNode getUserListHead() {
        return userListHead;
    }

    public String getPostsCreatedByUserReport(int userId) {
        User user = findUserById(userId);
        if (user == null) {
            return "User ID " + userId + " not found.";
        }

        StringBuilder report = new StringBuilder();
        report.append("--- Posts Created by ").append(user.getName())
                .append(" (ID: ").append(userId).append(") ---\n");

        PostNode current = user.getPostsCreatedHead();
        if (current == null) {
            report.append("No posts created by this user.\n");
        } else {
            while (current != null) {
                Post post = current.post;
                report.append("Post ID: ").append(post.getPostId())
                        .append(", Content: \"").append(post.getContent()).append("\"")
                        .append(", Date: ").append(post.getCreationDate());

                // Append shared with list
                report.append(", Shared With: ");
                SharedUserNode shared = post.getSharedWithListHead();
                if (shared == null) {
                    report.append("None");
                } else {
                    boolean first = true;
                    while (shared != null) {
                        if (!first) report.append(", ");
                        report.append(shared.sharedUser.getName())
                                .append(" (ID:").append(shared.sharedUser.getUserId()).append(")");
                        shared = shared.next;
                        first = false;
                    }
                }
                report.append("\n");
                current = current.next;
            }
        }
        report.append("------------------------------------------\n");
        return report.toString();
    }

    public String getPostsSharedWithUserReport(int userId) {
        User user = findUserById(userId);
        if (user == null) {
            return "User ID " + userId + " not found.";
        }
        StringBuilder report = new StringBuilder();
        report.append("--- Posts Shared with ").append(user.getName())
                .append(" (ID: ").append(userId).append(") ---\n");

        PostNode current = user.getPostsSharedWithMeHead();
        if (current == null) {
            report.append("No posts shared with this user.\n");
        } else {
            while (current != null) {
                Post post = current.post;
                User creator = findUserById(post.getCreatorId());
                String creatorName = (creator != null) ? creator.getName() : "Unknown";
                report.append("Post ID: ").append(post.getPostId())
                        .append(", Content: \"").append(post.getContent()).append("\"")
                        .append(", Date: ").append(post.getCreationDate())
                        .append(" (Creator: ").append(creatorName)
                        .append(" ID:").append(post.getCreatorId()).append(")\n");
                current = current.next;
            }
        }
        report.append("------------------------------------------\n");
        return report.toString();
    }

    public String getEngagementMetricsReport(int userId) {
        User user = findUserById(userId);
        if (user == null) {
            return "User ID " + userId + " not found.";
        }
        StringBuilder report = new StringBuilder();
        report.append("--- Engagement Metrics for ").append(user.getName()).append(" ---\n");
        report.append("Posts Created: ").append(user.countCreatedPosts()).append("\n");
        report.append("Posts Shared With User: ").append(user.countSharedPosts()).append("\n");
        report.append("------------------------------------------\n");
        return report.toString();
    }

    public String getMostActiveUsersReport(int n) {
        StringBuilder report = new StringBuilder();
        report.append("--- Top ").append(n).append(" Most Active Users Report ---\n");
        report.append("(Simplified: Showing total created posts per user)\n");
        UserNode current = userListHead;
        if (current == null) {
            report.append("No users in the network.\n");
        } else {
            int count = 0;
            while(current != null && count < n) {
                report.append(" - ").append(current.user.getName())
                        .append(": ").append(current.user.countCreatedPosts()).append(" posts created\n");
                current = current.next;
                count++;
            }
            if (count == 0) {
                report.append("No users found to report on.\n");
            }
        }
        report.append("------------------------------------------\n");
        return report.toString();
    }
}

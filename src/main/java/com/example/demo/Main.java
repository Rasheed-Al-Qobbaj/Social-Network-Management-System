package com.example.demo;

public class Main {

    public static void main(String[] args) {
        SocialNetwork network = new SocialNetwork();

        // --- 1. File Loading ---
        // **IMPORTANT**: Replace with actual paths to YOUR generated files.
        // You need to create these files with at least 30 users & 7 posts/user.
        String usersFilePath = "src/main/java/com/example/demo/users.txt";
        String friendshipsFilePath = "src/main/java/com/example/demo/friendships.txt";
        String postsFilePath = "src/main/java/com/example/demo/posts.txt";

        // Use a simple file chooser mechanism (text-based for now)
        // Or just hardcode paths for testing if GUI FileChooser is not allowed yet.
        System.out.println("Attempting to load data...");
        network.loadUsers(usersFilePath);          // Load users first
        network.loadFriendships(friendshipsFilePath); // Then friendships
        network.loadPosts(postsFilePath);            // Then posts

        System.out.println("\n--- Data Loading Complete ---");


        // --- 2. Example Operations (Demonstration) ---

        // Search for users
        System.out.println("\nSearching for User ID 2:");
        User user2 = network.findUserById(2);
        if (user2 != null) {
            System.out.println("Found: " + user2);
            network.displayEngagementMetrics(2);
        } else {
            System.out.println("User ID 2 not found.");
        }

        System.out.println("\nSearching for User Name 'Khaled':");
        User khaled = network.findUserByName("Khaled");
        if (khaled != null) {
            System.out.println("Found: " + khaled);
        } else {
            System.out.println("User 'Khaled' not found.");
        }


        // Display posts for a user
        System.out.println("\nDisplaying posts related to User ID 1:");
        network.displayPostsCreatedByUser(1);
        network.displayPostsSharedWithUser(1);

        // Add a new user
        System.out.println("\nAdding a new user:");
        network.addUser(5, "Sara", 24);
        User sara = network.findUserById(5);

        // Add friendship
        System.out.println("\nAdding friendship:");
        if (sara != null) {
            network.addFriendship(1, 5); // Ahmed friends Sara
        }

        // Create a post
        System.out.println("\nCreating a new post:");
        User ahmed = network.findUserById(1);
        if (ahmed != null) {
            int[] shareWith = {2, 3, 5}; // Share with Fatima, Khaled, Sara
            network.createPost(1, "Enjoying COMP242 course :)", "25.3.2025", shareWith);
            network.displayPostsCreatedByUser(1); // Show Ahmed's posts again
            network.displayPostsSharedWithUser(5); // Show Sara's shared posts
        }


        // Delete a post (Ahmed deletes his own post ID 1)
        System.out.println("\nAhmed (ID 1) deleting Post ID 1:");
        network.deletePost(1, 1);
        network.displayPostsCreatedByUser(1); // Show Ahmed's posts again
        network.displayPostsSharedWithUser(2); // Show Fatima's shared posts (Post 1 should be gone)


        // Fatima (ID 2) tries to delete Khaled's post (ID 3) - should remove from view only
        System.out.println("\nFatima (ID 2) trying to delete Post ID 3 (created by Khaled):");
        network.deletePost(3, 2);
        network.displayPostsSharedWithUser(2); // Post 3 should be gone from Fatima's view
        network.displayPostsCreatedByUser(3);   // Post 3 should still exist for Khaled


        // Delete a user (Layla ID 4)
        System.out.println("\nDeleting User Layla (ID 4):");
        network.deleteUser(4);
        System.out.println("Searching for User ID 4 after deletion: " + network.findUserById(4));
        System.out.println("Checking Ahmed's (ID 1) friends after Layla's deletion:");
        if(ahmed != null) {
            FriendNode fn = ahmed.getFriendsListHead();
            while(fn != null) {
                System.out.println(" - " + fn.friend.getName());
                fn = fn.next;
            }
        }

        // --- 3. Reporting ---
        System.out.println("\n--- Generating Reports ---");
        network.displayMostActiveUsers(5); // Display top 5

        // Save reports to files
        network.savePostsCreatedReport("posts_created_report.txt");
        network.savePostsSharedWithReport("posts_shared_report.txt");


        System.out.println("\n--- Program Demo End ---");
    }
}

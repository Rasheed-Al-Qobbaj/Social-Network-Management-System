package com.example.demo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Arrays;



public class SocialNetworkApp extends Application {

    private SocialNetwork network = new SocialNetwork();
    private UserNode currentUserNode = null;

    // --- UI Components ---
    private Label lblCurrentUserStatus;
    private TextField tfUserId, tfUserName, tfUserAge;

    private TableView<User> friendsTable;
    private TableView<Post> postsCreatedTable;
    private TableView<Post> postsSharedTable;

    private Button btnLoad, btnPrev, btnNext;
    private Button btnAddUser, btnUpdateUser, btnDeleteUser;
    private Button btnSearchId, btnSearchName;
    private TextField tfSearchId, tfSearchName;

    private TextField tfFriendId;
    private Button btnAddFriend, btnRemoveFriend;

    private TextField tfPostContent, tfPostShareWith;
    private DatePicker dpPostDate;
    private Button btnCreatePost, btnDeletePost;

    private Button btnReportCreated, btnReportShared, btnReportActive, btnReportEngagement;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Advanced Social Network Management System");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // --- Top: Load Button ---
        HBox topBox = new HBox(10);
        topBox.setPadding(new Insets(5));
        btnLoad = new Button("Load Data Files");
        btnLoad.setOnAction(e -> loadDataFiles(primaryStage));
        topBox.getChildren().add(btnLoad);
        root.setTop(topBox);


        VBox centerBox = createCenterDisplay();
        root.setCenter(centerBox);


        HBox bottomBox = createNavigationPanel();
        root.setBottom(bottomBox);


        VBox rightBox = createOperationsPanel();
        root.setRight(rightBox);


        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.show();


        disableControls(true);
    }


    private VBox createCenterDisplay() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        // Current User Info
        GridPane userInfoPane = new GridPane();
        userInfoPane.setHgap(10);
        userInfoPane.setVgap(5);
        userInfoPane.add(new Label("Current User ID:"), 0, 0);
        tfUserId = new TextField();
        tfUserId.setEditable(false);
        userInfoPane.add(tfUserId, 1, 0);

        userInfoPane.add(new Label("Name:"), 0, 1);
        tfUserName = new TextField();
        userInfoPane.add(tfUserName, 1, 1);

        userInfoPane.add(new Label("Age:"), 0, 2);
        tfUserAge = new TextField();
        userInfoPane.add(tfUserAge, 1, 2);

        lblCurrentUserStatus = new Label("No user loaded.");
        userInfoPane.add(lblCurrentUserStatus, 0, 3, 2, 1);

        TitledPane userTitledPane = new TitledPane("Current User Information", userInfoPane);
        userTitledPane.setCollapsible(false);

        // Friends Table
        friendsTable = new TableView<>();
        TableColumn<User, Integer> friendIdCol = new TableColumn<>("Friend ID");
        friendIdCol.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getUserId()));
        TableColumn<User, String> friendNameCol = new TableColumn<>("Friend Name");
        friendNameCol.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getName()));
        friendsTable.getColumns().addAll(friendIdCol, friendNameCol);
        friendsTable.setPlaceholder(new Label("No friends to display"));
        TitledPane friendsTitledPane = new TitledPane("Friends", friendsTable);
        friendsTitledPane.setCollapsible(false);
        friendsTitledPane.setPrefHeight(150);

        // Posts Created Table
        postsCreatedTable = new TableView<>();
        TableColumn<Post, Integer> createdPostIdCol = new TableColumn<>("Post ID");
        createdPostIdCol.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getPostId()));
        TableColumn<Post, String> createdPostContentCol = new TableColumn<>("Content");
        createdPostContentCol.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getContent()));
        createdPostContentCol.setPrefWidth(200);
        TableColumn<Post, String> createdPostDateCol = new TableColumn<>("Date");
        createdPostDateCol.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getCreationDate()));
        postsCreatedTable.getColumns().addAll(createdPostIdCol, createdPostContentCol, createdPostDateCol);
        postsCreatedTable.setPlaceholder(new Label("No posts created by this user"));
        TitledPane postsCreatedTitledPane = new TitledPane("Posts Created By User", postsCreatedTable);
        postsCreatedTitledPane.setCollapsible(false);
        postsCreatedTitledPane.setPrefHeight(150);

        // Posts Shared Table
        postsSharedTable = new TableView<>();
        TableColumn<Post, Integer> sharedPostIdCol = new TableColumn<>("Post ID");
        sharedPostIdCol.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getPostId()));
        TableColumn<Post, String> sharedPostContentCol = new TableColumn<>("Content");
        sharedPostContentCol.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getContent()));
        sharedPostContentCol.setPrefWidth(200);
        TableColumn<Post, String> sharedPostDateCol = new TableColumn<>("Date");
        sharedPostDateCol.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getCreationDate()));
        TableColumn<Post, Integer> sharedPostCreatorIdCol = new TableColumn<>("Creator ID");
        sharedPostCreatorIdCol.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getCreatorId()));
        postsSharedTable.getColumns().addAll(sharedPostIdCol, sharedPostContentCol, sharedPostDateCol, sharedPostCreatorIdCol);
        postsSharedTable.setPlaceholder(new Label("No posts shared with this user"));
        TitledPane postsSharedTitledPane = new TitledPane("Posts Shared With User", postsSharedTable);
        postsSharedTitledPane.setCollapsible(false);
        postsSharedTitledPane.setPrefHeight(150);

        box.getChildren().addAll(userTitledPane, friendsTitledPane, postsCreatedTitledPane, postsSharedTitledPane);
        return box;
    }

    private HBox createNavigationPanel() {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));
        btnPrev = new Button("<< Next User");
        btnPrev.setOnAction(e -> navigateUser(false));
        btnNext = new Button("Previous User >>");
        btnNext.setOnAction(e -> navigateUser(true));
        box.getChildren().addAll(btnPrev, btnNext);
        return box;
    }

    private VBox createOperationsPanel() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));
        box.setAlignment(Pos.TOP_LEFT);

        // --- User Management ---
        VBox userOpsBox = new VBox(5);
        userOpsBox.setPadding(new Insets(5));
        userOpsBox.setBorder(new Border(new BorderStroke(javafx.scene.paint.Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        userOpsBox.getChildren().add(new Label("User Management:"));

        // Search
        HBox searchBox = new HBox(5);
        tfSearchId = new TextField();
        tfSearchId.setPromptText("User ID");
        btnSearchId = new Button("Search ID");
        btnSearchId.setOnAction(e -> searchUserById());
        tfSearchName = new TextField();
        tfSearchName.setPromptText("User Name");
        btnSearchName = new Button("Search Name");
        btnSearchName.setOnAction(e -> searchUserByName());
        searchBox.getChildren().addAll(tfSearchId, btnSearchId, tfSearchName, btnSearchName);
        userOpsBox.getChildren().add(searchBox);

        // Add User
        btnAddUser = new Button("Add New User...");
        btnAddUser.setOnAction(e -> showAddUserDialog());
        userOpsBox.getChildren().add(btnAddUser);

        // Update/Delete Current User
        HBox updateDeleteBox = new HBox(5);
        btnUpdateUser = new Button("Update Current User");
        btnUpdateUser.setOnAction(e -> updateUser());
        btnDeleteUser = new Button("Delete Current User");
        btnDeleteUser.setOnAction(e -> deleteUser());
        updateDeleteBox.getChildren().addAll(btnUpdateUser, btnDeleteUser);
        userOpsBox.getChildren().add(updateDeleteBox);


        // --- Friendship Management ---
        VBox friendOpsBox = new VBox(5);
        friendOpsBox.setPadding(new Insets(5));
        friendOpsBox.setBorder(new Border(new BorderStroke(javafx.scene.paint.Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        friendOpsBox.getChildren().add(new Label("Friendship Management (Current User):"));
        HBox friendIdBox = new HBox(5);
        tfFriendId = new TextField();
        tfFriendId.setPromptText("Friend's User ID");
        btnAddFriend = new Button("Add Friend");
        btnAddFriend.setOnAction(e -> addFriend());
        btnRemoveFriend = new Button("Remove Friend");
        btnRemoveFriend.setOnAction(e -> removeFriend());
        friendIdBox.getChildren().addAll(tfFriendId, btnAddFriend, btnRemoveFriend);
        friendOpsBox.getChildren().add(friendIdBox);


        // --- Post Management ---
        VBox postOpsBox = new VBox(5);
        postOpsBox.setPadding(new Insets(5));
        postOpsBox.setBorder(new Border(new BorderStroke(javafx.scene.paint.Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        postOpsBox.getChildren().add(new Label("Post Management (Current User):"));

        // Create Post
        tfPostContent = new TextField();
        tfPostContent.setPromptText("Post Content");
        dpPostDate = new DatePicker(LocalDate.now());
        tfPostShareWith = new TextField();
        tfPostShareWith.setPromptText("Share with User IDs (comma-sep)");
        btnCreatePost = new Button("Create Post");
        btnCreatePost.setOnAction(e -> createPost());
        postOpsBox.getChildren().addAll(tfPostContent, dpPostDate, tfPostShareWith, btnCreatePost);

        // Delete Post
        btnDeletePost = new Button("Delete Selected Post");
        btnDeletePost.setOnAction(e -> deleteSelectedPost());
        postOpsBox.getChildren().add(btnDeletePost);


        // --- Reporting ---
        VBox reportOpsBox = new VBox(5);
        reportOpsBox.setPadding(new Insets(5));
        reportOpsBox.setBorder(new Border(new BorderStroke(javafx.scene.paint.Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        reportOpsBox.getChildren().add(new Label("Reporting:"));
        btnReportCreated = new Button("Show Posts Created by Current");
        btnReportCreated.setOnAction(e -> showPostsCreatedReport());
        btnReportShared = new Button("Show Posts Shared with Current");
        btnReportShared.setOnAction(e -> showPostsSharedReport());
        btnReportActive = new Button("Show Top 5 Active Users");
        btnReportActive.setOnAction(e -> {
            String reportContent = network.getMostActiveUsersReport(5);
            showReportDialog("Most Active Users Report", reportContent);
        });
        btnReportEngagement = new Button("Show Current User Engagement");
        btnReportEngagement.setOnAction(e -> showEngagementReport());
        reportOpsBox.getChildren().addAll(btnReportCreated, btnReportShared, btnReportActive, btnReportEngagement);


        box.getChildren().addAll(userOpsBox, friendOpsBox, postOpsBox, reportOpsBox);
        return box;
    }



    private void loadDataFiles(Stage ownerStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select users.txt");
        File usersFile = fileChooser.showOpenDialog(ownerStage);

        if (usersFile != null) {
            fileChooser.setTitle("Select friendships.txt");
            File friendshipsFile = fileChooser.showOpenDialog(ownerStage);

            if (friendshipsFile != null) {
                fileChooser.setTitle("Select posts.txt");
                File postsFile = fileChooser.showOpenDialog(ownerStage);

                if (postsFile != null) {
                    try {

                        network = new SocialNetwork();
                        currentUserNode = null;

                        network.loadUsers(usersFile.getAbsolutePath());
                        network.loadFriendships(friendshipsFile.getAbsolutePath());
                        network.loadPosts(postsFile.getAbsolutePath());

                        currentUserNode = network.getUserListHead();
                        updateDisplay();
                        disableControls(currentUserNode == null);
                        showAlert(Alert.AlertType.INFORMATION, "Load Success", "Data loaded successfully.");

                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "Load Error", "Failed to load data files.\n" + e.getMessage());
                        disableControls(true);
                    }
                }
            }
        }
    }

    private void navigateUser(boolean forward) {
        if (network.getUserListHead() == null) return;

        if (currentUserNode == null) {
            currentUserNode = network.getUserListHead();
        } else {
            if (forward) {
                currentUserNode = (currentUserNode.next != null) ? currentUserNode.next : network.getUserListHead();
            } else {

                UserNode prev = null;
                UserNode temp = network.getUserListHead();
                if (temp == currentUserNode) {
                    while(temp.next != null) {
                        temp = temp.next;
                    }
                    currentUserNode = temp;
                } else {
                    while (temp != null && temp.next != currentUserNode) {
                        temp = temp.next;
                    }
                    currentUserNode = temp;
                    if(currentUserNode == null) currentUserNode = network.getUserListHead();
                }
            }
        }
        updateDisplay();
    }

    private void searchUserById() {
        try {
            int id = Integer.parseInt(tfSearchId.getText().trim());
            User foundUser = network.findUserById(id);
            if (foundUser != null) {

                UserNode node = network.getUserListHead();
                while (node != null && node.user != foundUser) {
                    node = node.next;
                }
                if (node != null) {
                    currentUserNode = node;
                    updateDisplay();
                    tfSearchId.clear();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Search Error", "Internal error: User found but Node not located.");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Search Result", "User ID " + id + " not found.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid numeric User ID.");
        }
    }

    private void searchUserByName() {
        String name = tfSearchName.getText().trim();
        if (name.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a name to search.");
            return;
        }
        User foundUser = network.findUserByName(name);
        if (foundUser != null) {
            UserNode node = network.getUserListHead();
            while (node != null && node.user != foundUser) {
                node = node.next;
            }
            if (node != null) {
                currentUserNode = node;
                updateDisplay();
                tfSearchName.clear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Search Error", "Internal error: User found but Node not located.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Search Result", "User Name '" + name + "' not found.");
        }
    }

    private void showAddUserDialog() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Add New User");
        dialog.setHeaderText("Enter new user details:");

        ButtonType addButtonType = new ButtonType("Add User", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField idField = new TextField();
        idField.setPromptText("User ID");
        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField ageField = new TextField();
        ageField.setPromptText("Age");

        grid.add(new Label("User ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Age:"), 0, 2);
        grid.add(ageField, 1, 2);

        dialog.getDialogPane().setContent(grid);


        Platform.runLater(idField::requestFocus);


        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    int id = Integer.parseInt(idField.getText());
                    String name = nameField.getText();
                    int age = Integer.parseInt(ageField.getText());
                    if(name.trim().isEmpty()) throw new IllegalArgumentException("Name cannot be empty.");

                    return new User(id, name, age);
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "ID and Age must be numbers.");
                } catch (IllegalArgumentException e) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", e.getMessage());
                }
                return null;
            }
            return null;
        });

        Optional<User> result = dialog.showAndWait();

        result.ifPresent(newUser -> {
            if (network.addUser(newUser.getUserId(), newUser.getName(), newUser.getAge())) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "User added successfully.");

                searchUserById(newUser.getUserId());
            } else {

            }
        });
    }

    private void updateUser() {
        if (currentUserNode == null) return;
        try {
            String newName = tfUserName.getText().trim();
            int newAge = Integer.parseInt(tfUserAge.getText().trim());
            int userId = currentUserNode.user.getUserId();

            if(newName.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Name cannot be empty.");
                return;
            }

            if (network.updateUser(userId, newName, newAge)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "User updated successfully.");
                updateDisplay();
            } else {
                showAlert(Alert.AlertType.ERROR, "Update Failed", "Could not update user (check console for details).");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Age must be a valid number.");
        }
    }

    private void deleteUser() {
        if (currentUserNode == null) return;

        User userToDelete = currentUserNode.user;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete User: " + userToDelete.getName() + " (ID: " + userToDelete.getUserId() + ")");
        confirm.setContentText("Are you sure you want to permanently delete this user and all their created posts? This cannot be undone.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            UserNode nodeToDisplayNext = (currentUserNode.next != null) ? currentUserNode.next : network.getUserListHead();
            if(nodeToDisplayNext == currentUserNode) nodeToDisplayNext = null;

            if (network.deleteUser(userToDelete.getUserId())) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "User deleted successfully.");
                currentUserNode = nodeToDisplayNext;
                updateDisplay();
                disableControls(currentUserNode == null);
            } else {
                showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Could not delete user (check console for details).");
            }
        }
    }

    private void addFriend() {
        if (currentUserNode == null) return;
        try {
            int friendId = Integer.parseInt(tfFriendId.getText().trim());
            int currentUserId = currentUserNode.user.getUserId();
            if (friendId == currentUserId) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Cannot add yourself as a friend.");
                return;
            }

            if (network.addFriendship(currentUserId, friendId)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Friendship added.");
                updateDisplay();
                tfFriendId.clear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed", "Could not add friendship (User ID might not exist?).");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid numeric Friend ID.");
        }
    }

    private void removeFriend() {
        if (currentUserNode == null) return;
        try {
            int friendId = Integer.parseInt(tfFriendId.getText().trim());
            int currentUserId = currentUserNode.user.getUserId();


            boolean isFriend = false;
            FriendNode fn = currentUserNode.user.getFriendsListHead();
            while(fn != null){
                if(fn.friend.getUserId() == friendId) {
                    isFriend = true;
                    break;
                }
                fn = fn.next;
            }
            if(!isFriend){
                showAlert(Alert.AlertType.WARNING, "Not Found", "User ID " + friendId + " is not in the current user's friend list.");
                return;
            }


            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Removal");
            confirm.setHeaderText("Remove Friend?");
            confirm.setContentText("Are you sure you want to remove the friendship with User ID " + friendId + "?");

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (network.removeFriendship(currentUserId, friendId)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Friendship removed.");
                    updateDisplay();
                    tfFriendId.clear();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed", "Could not remove friendship (check console).");
                }
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid numeric Friend ID.");
        }
    }


    private void createPost() {
        if (currentUserNode == null) return;

        String content = tfPostContent.getText().trim();
        LocalDate date = dpPostDate.getValue();
        String shareWithString = tfPostShareWith.getText().trim();

        if (content.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Post content cannot be empty.");
            return;
        }
        if (date == null) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please select a creation date.");
            return;
        }

        String formattedDate = date.format(DateTimeFormatter.ofPattern("d.M.yyyy"));
        int currentUserId = currentUserNode.user.getUserId();
        int[] sharedWithIds = null;

        if (!shareWithString.isEmpty()) {
            try {
                sharedWithIds = Arrays.stream(shareWithString.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .mapToInt(Integer::parseInt)
                        .toArray();
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid User ID format in 'Share With' field. Use comma-separated numbers.");
                return;
            }
        }

        if (network.createPost(currentUserId, content, formattedDate, sharedWithIds)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Post created successfully.");
            updateDisplay();
            tfPostContent.clear();
            tfPostShareWith.clear();
            dpPostDate.setValue(LocalDate.now());
        } else {
            showAlert(Alert.AlertType.ERROR, "Failed", "Could not create post (check console).");
        }
    }

    private void deleteSelectedPost() {
        if (currentUserNode == null) return;


        Post selectedPost = postsCreatedTable.getSelectionModel().getSelectedItem();
        String listType = "created";
        if (selectedPost == null) {
            selectedPost = postsSharedTable.getSelectionModel().getSelectedItem();
            listType = "shared";
        }

        if (selectedPost == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a post from either the 'Created' or 'Shared' table to delete.");
            return;
        }

        int postId = selectedPost.getPostId();
        int currentUserId = currentUserNode.user.getUserId();

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete Post ID: " + postId);
        confirm.setContentText("Are you sure? \n(Deleting your own post removes it everywhere. Deleting a shared post only removes it from your view).");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (network.deletePost(postId, currentUserId)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Post action completed (deleted or removed from view).");
                updateDisplay();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed", "Could not complete post deletion action (check console). Post might have already been removed.");
            }
        }
    }


    private void showReportDialog(String title, String content) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(null);

        TextArea textArea = new TextArea(content);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefSize(600, 400);

        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        dialog.showAndWait();
    }

    private void showPostsCreatedReport() {
        if (currentUserNode == null) {
            showAlert(Alert.AlertType.WARNING, "No User", "No user is currently selected.");
            return;
        }
        String reportContent = network.getPostsCreatedByUserReport(currentUserNode.user.getUserId());
        showReportDialog("Posts Created by " + currentUserNode.user.getName(), reportContent);
    }

    private void showPostsSharedReport() {
        if (currentUserNode == null) {
            showAlert(Alert.AlertType.WARNING, "No User", "No user is currently selected.");
            return;
        }
        String reportContent = network.getPostsSharedWithUserReport(currentUserNode.user.getUserId());
        showReportDialog("Posts Shared With " + currentUserNode.user.getName(), reportContent);
    }

    private void showEngagementReport() {
        if (currentUserNode == null) {
            showAlert(Alert.AlertType.WARNING, "No User", "No user is currently selected.");
            return;
        }
        String reportContent = network.getEngagementMetricsReport(currentUserNode.user.getUserId());
        showReportDialog("Engagement Metrics for " + currentUserNode.user.getName(), reportContent);
    }




    private void disableControls(boolean disable) {
        btnPrev.setDisable(disable);
        btnNext.setDisable(disable);
        tfUserName.setDisable(disable);
        tfUserAge.setDisable(disable);
        btnUpdateUser.setDisable(disable);
        btnDeleteUser.setDisable(disable);
        friendsTable.setDisable(disable);
        postsCreatedTable.setDisable(disable);
        postsSharedTable.setDisable(disable);
        btnAddUser.setDisable(disable);
        btnSearchId.setDisable(disable);
        btnSearchName.setDisable(disable);
        tfSearchId.setDisable(disable);
        tfSearchName.setDisable(disable);
        tfFriendId.setDisable(disable);
        btnAddFriend.setDisable(disable);
        btnRemoveFriend.setDisable(disable);
        tfPostContent.setDisable(disable);
        tfPostShareWith.setDisable(disable);
        dpPostDate.setDisable(disable);
        btnCreatePost.setDisable(disable);
        btnDeletePost.setDisable(disable);
        btnReportCreated.setDisable(disable);
        btnReportShared.setDisable(disable);
        btnReportActive.setDisable(disable);
        btnReportEngagement.setDisable(disable);

    }


    private void updateDisplay() {
        if (currentUserNode != null && currentUserNode.user != null) {
            User user = currentUserNode.user;
            tfUserId.setText(String.valueOf(user.getUserId()));
            tfUserName.setText(user.getName());
            tfUserAge.setText(String.valueOf(user.getAge()));
            lblCurrentUserStatus.setText("Displaying: " + user.getName());


            ObservableList<User> friendsData = FXCollections.observableArrayList();
            FriendNode fn = user.getFriendsListHead();
            while (fn != null) {
                friendsData.add(fn.friend);
                fn = fn.next;
            }
            friendsTable.setItems(friendsData);


            ObservableList<Post> createdPostsData = FXCollections.observableArrayList();
            PostNode pcn = user.getPostsCreatedHead();
            while (pcn != null) {
                createdPostsData.add(pcn.post);
                pcn = pcn.next;
            }
            postsCreatedTable.setItems(createdPostsData);


            ObservableList<Post> sharedPostsData = FXCollections.observableArrayList();
            PostNode psn = user.getPostsSharedWithMeHead();
            while (psn != null) {
                sharedPostsData.add(psn.post);
                psn = psn.next;
            }
            postsSharedTable.setItems(sharedPostsData);

        } else {

            tfUserId.clear();
            tfUserName.clear();
            tfUserAge.clear();
            lblCurrentUserStatus.setText("No user selected or list empty.");
            friendsTable.getItems().clear();
            postsCreatedTable.getItems().clear();
            postsSharedTable.getItems().clear();
        }

        friendsTable.refresh();
        postsCreatedTable.refresh();
        postsSharedTable.refresh();
    }


    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    private void searchUserById(int id) {
        tfSearchId.setText(String.valueOf(id));
        searchUserById();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
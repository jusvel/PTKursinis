package com.kursinis.ptkursinis.fxControllers;

import com.kursinis.ptkursinis.helpers.JavaFxCustomUtils;
import com.kursinis.ptkursinis.hibernateControllers.CustomHib;
import com.kursinis.ptkursinis.model.Post;
import com.kursinis.ptkursinis.model.PostComment;
import com.kursinis.ptkursinis.model.User;
import com.kursinis.ptkursinis.model.Warehouse;
import jakarta.persistence.EntityManagerFactory;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class DiscussionPageController implements PageController, Initializable {
    public ListView postList;
    public Label authorLabel;
    public ListView commentsList;
    public TextArea commentTextArea;
    public TextArea postBodyTextArea;
    public TextField postTitleTextField;
    public Button savePostButton;
    public Button deletePostButton;
    public Button updateCommentButton;
    public Button deleteCommentButton;
    private EntityManagerFactory entityManagerFactory;
    private User currentUser;
    private CustomHib customHib;

    private final ObservableList<Post> postData = FXCollections.observableArrayList();
    private final ObservableList<PostComment> postCommentData = FXCollections.observableArrayList();
    private Post selectedPost;
    private PostComment selectedComment;


    @Override
    public void setData(EntityManagerFactory entityManagerFactory, User currentUser) {
        this.entityManagerFactory = entityManagerFactory;
        this.currentUser = currentUser;
        customHib = new CustomHib(entityManagerFactory);
        loadPosts();
    }

    private void loadPosts(){
        postData.clear();
        postData.addAll(customHib.getAllRecords(Post.class));
    }
    private void loadComments(int id){
        postCommentData.clear();
        Post post = (Post) customHib.getEntityById(Post.class,id);
        postCommentData.addAll(post.getComments());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        postList.setItems(postData);
        commentsList.setItems(postCommentData);
        addPostListSelectionListener();
        addPostCommentListSelectionListener();
    }

    private void addPostCommentListSelectionListener() {
        commentsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                selectedComment = (PostComment) commentsList.getSelectionModel().getSelectedItem();
                if(selectedComment == null) return;
                if(selectedComment.getUser().getId() == currentUser.getId()) {
                    updateCommentButton.setDisable(false);
                    deleteCommentButton.setDisable(false);
                } else {
                    updateCommentButton.setDisable(true);
                    deleteCommentButton.setDisable(true);
                }
            }
        });
    }

    public void leaveAComment() {
        if(selectedPost != null) {
            String commentBody = JavaFxCustomUtils.showTextInputDialog("Leave a comment", "Enter your comment");
            if(commentBody != null) {
                PostComment postComment = new PostComment();
                postComment.setBody(commentBody);
                postComment.setUser(currentUser);
                postComment.setPost(selectedPost);
                customHib.create(postComment);
                postCommentData.add(postComment);
            } else{
                JavaFxCustomUtils.generateAlert(Alert.AlertType.ERROR,"Error","Error","Comment body cannot be empty");
            }
        } else {
            JavaFxCustomUtils.showError("Please select a post");
        }
    }

    private void addPostListSelectionListener() {
        postList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                if(postList.getSelectionModel().getSelectedItem() != null) {
                    selectedPost = (Post) postList.getSelectionModel().getSelectedItem();
                    postTitleTextField.setText(selectedPost.getTitle());
                    authorLabel.setText(selectedPost.getUser().getUsername());
                    postBodyTextArea.setText(selectedPost.getBody());
                    loadComments(selectedPost.getId());
                    if(currentUser != null && currentUser.getId() == selectedPost.getUser().getId()) {
                        postTitleTextField.setEditable(true);
                        postBodyTextArea.setEditable(true);
                        deletePostButton.setDisable(false);
                        savePostButton.setDisable(false);
                    } else {
                        postTitleTextField.setEditable(false);
                        postBodyTextArea.setEditable(false);
                        deletePostButton.setDisable(true);
                        savePostButton.setDisable(true);
                    }
                }
            }
        });
    }

    public void newPost() {
        Post post = new Post();
        postTitleTextField.setEditable(true);
        postBodyTextArea.setEditable(true);
        savePostButton.setDisable(false);
        postTitleTextField.setText("");
        postBodyTextArea.setText("");
        post.setUser(currentUser);
        authorLabel.setText(currentUser.getUsername());
        selectedPost = post;
    }

    public void deletePost() {
        if(selectedPost.getUser().getId() == currentUser.getId()) {
            customHib.delete(Post.class,selectedPost.getId());
            postData.remove(selectedPost);
        }
    }

    public void updateComment() {
        if(selectedComment == null) {
            JavaFxCustomUtils.showError("Please select a comment");
            return;
        }
        if(currentUser.getId() == selectedComment.getUser().getId()){
            selectedComment.setBody(JavaFxCustomUtils.showTextInputDialog("Update comment","Enter new comment body"));
            customHib.update(selectedComment);
            loadComments(selectedPost.getId());
        }
    }

    public void deleteComent() {
        if(selectedComment == null){
            JavaFxCustomUtils.showError("Please select a comment");
            return;
        }
        if(currentUser.getId() == selectedComment.getUser().getId()){
            customHib.delete(PostComment.class,selectedComment.getId());
            postCommentData.remove(selectedComment);
        }
    }

    public void savePost() {
        if(selectedPost!=null){
            if(customHib.getEntityById(Post.class,selectedPost.getId())!=null){
                selectedPost.setTitle(postTitleTextField.getText());
                selectedPost.setBody(postBodyTextArea.getText());
                customHib.update(selectedPost);
                loadPosts();
            } else{
                String title = postTitleTextField.getText();
                String body = postBodyTextArea.getText();
                User user = currentUser;
                customHib.create(new Post(title,body,user));
                loadPosts();
            }
        } else{
            JavaFxCustomUtils.showError("Please select a post");
        }
    }
}

package com.application.bean;

import com.application.service.dao.documents.PostDocument;

import java.util.List;

import com.application.service.dao.documents.CommentDocument;
import com.application.service.dao.documents.UserDocument;

public class PostResponse {
	private PostDocument post;
	private UserDocument user;
	private Boolean likedByUser;
	private long likesCount;
	private String daysAgo;
	private List<CommentDocument> comments;

	public List<CommentDocument> getComments() {
		return comments;
	}
	public void setComments(List<CommentDocument> comments) {
		this.comments = comments;
	}
	public PostDocument getPost() {
		return post;
	}
	public void setPost(PostDocument post) {
		this.post = post;
	}
	public UserDocument getUser() {
		return user;
	}
	public void setUser(UserDocument user) {
		this.user = user;
	}
	public Boolean getLikedByUser() {
		return likedByUser;
	}
	public void setLikedByUser(Boolean likedByUser) {
		this.likedByUser = likedByUser;
	}
	public long getLikesCount() {
		return likesCount;
	}
	public void setLikesCount(long likesCount) {
		this.likesCount = likesCount;
	}
	public String getDaysAgo() {
		return daysAgo;
	}
	public void setDaysAgo(String daysAgo) {
		this.daysAgo = daysAgo;
	}
}

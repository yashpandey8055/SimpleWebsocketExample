package com.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.service.dao.PostDao;
import com.application.service.dao.UsersDao;
import com.application.service.dao.documents.PostDocument;
import com.application.service.dao.documents.UserDocument;

@RestController
@RequestMapping("/posts")
public class PostController {
	
	@Autowired
	PostDao postDao;
	
	@Autowired
	UsersDao userDao;

	@PostMapping("/insert")
	public ResponseEntity<PostDocument> insertPost(@AuthenticationPrincipal UserDocument currentUser, @RequestBody PostDocument post){
		
		UserDocument user = userDao.find(currentUser.getUsername());
		user.getPosts().add(postDao.insert(post));
		user.setPosts(user.getPosts());
		userDao.save(user);
		return new ResponseEntity<>(post,HttpStatus.OK);
		
	}
}

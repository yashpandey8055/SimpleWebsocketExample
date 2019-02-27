package com.application.data.dao;

import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.application.data.dao.documents.CommentDocument;
import com.application.data.dao.documents.MongoDocument;
import com.mongodb.client.result.DeleteResult;

public class CommentCollectionDAOImpl implements MongoCollectionFactory {

	MongoTemplate template;
	
	
	@Override
	public MongoDocument findOne(String key, Object value) {
		return template.findOne(Query.query(Criteria.where(key).is(value)), CommentDocument.class);
	}

	@Override
	public List<? extends MongoDocument> findList(String key, Object value) {
		return template.find(Query.query(Criteria.where(key).is(value)), CommentDocument.class);
	}

	@Override
	public MongoDocument save(MongoDocument document) {
		template.save(document);
		return document;
	}

	@Override
	public DeleteResult delete(String key, Object value) {
		return template.remove(Query.query(Criteria.where(key).is(value)), CommentDocument.class);
	}

	@Override
	public List<? extends MongoDocument> executeQuery(Query query) {
		return template.find(query, CommentDocument.class);
	}


}


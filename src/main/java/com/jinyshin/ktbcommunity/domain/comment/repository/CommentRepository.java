package com.jinyshin.ktbcommunity.domain.comment.repository;

import com.jinyshin.ktbcommunity.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, CustomizedCommentRepository {

}
package me.whitebear.jpa.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryQuery,
    QuerydslPredicateExecutor<Comment> {

}

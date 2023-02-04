package me.whitebear.jpa.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.RepositoryDefinition;

@RepositoryDefinition(domainClass = Comment.class, idClass = Long.class)
public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryQuery {

}

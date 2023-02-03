package me.whitebear.jpa.comment;

import java.util.List;

public interface CommentRepositoryQuery {

  List<Comment> search(Long userId);
}

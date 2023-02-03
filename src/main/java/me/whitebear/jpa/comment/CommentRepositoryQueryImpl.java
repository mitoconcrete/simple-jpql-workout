package me.whitebear.jpa.comment;

import static me.whitebear.jpa.comment.QComment.comment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommentRepositoryQueryImpl implements CommentRepositoryQuery {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public List<Comment> search(Long userId) {
    return jpaQueryFactory.select(comment)
        .leftJoin(comment.emotions).fetchJoin()
        .from(comment)
        .where(
            comment.user.id.eq(userId)
        ).orderBy(comment.emotions.any().createdAt.desc())
        .fetch();
  }
}

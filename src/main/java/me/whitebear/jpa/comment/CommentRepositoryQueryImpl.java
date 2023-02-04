package me.whitebear.jpa.comment;

import static me.whitebear.jpa.comment.QComment.comment;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommentRepositoryQueryImpl implements CommentRepositoryQuery {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public List<Comment> search(Long userId) {
    return jpaQueryFactory
        .select(comment)
        .from(comment)
        .where(
            userEq(userId),
            comment.emotions.any().isNotNull()
        ).
        orderBy(comment.emotions.any().createdAt.desc())
        .fetch();
  }

  private BooleanExpression userEq(Long userId) {
    return Objects.nonNull(userId) ? comment.user.id.eq(userId) : null;
  }
}

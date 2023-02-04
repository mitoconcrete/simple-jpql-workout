package me.whitebear.jpa.mentionEmotion;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import me.whitebear.jpa.comment.CommentRepository;
import me.whitebear.jpa.common.MentionEmotionBasicDto;
import me.whitebear.jpa.common.MentionEmotionThreadDto;
import me.whitebear.jpa.thread.ThreadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MentionEmotionService {

  private final CommentRepository commentRepository;
  private final ThreadRepository threadRepository;

  @Transactional
  public List<MentionEmotionBasicDto> search(Long userId) {
    var commentList = commentRepository.search(userId).stream().map(MentionEmotionBasicDto::new)
        .toList();

    var threadList = threadRepository.search(userId).stream().map(MentionEmotionThreadDto::new)
        .toList();

    // 두개를 합쳐서 반환
    return Stream.concat(threadList.stream(), commentList.stream()).collect(Collectors.toList());
  }
}

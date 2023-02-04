package me.whitebear.jpa.common;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import me.whitebear.jpa.comment.Comment;
import me.whitebear.jpa.emotion.Emotion;
import me.whitebear.jpa.thread.Thread;


@Getter
public class MentionEmotionBasicDto {

  private final String username;
  private final String message;
  private final List<String> emotions;
  private final Integer emotionCounts;


  public MentionEmotionBasicDto(Thread thread) {
    username = thread.getUser().getUsername();
    message = thread.getMessage();
    emotions = thread.getEmotions().stream().map(Emotion::getBody).collect(
        Collectors.toList());
    emotionCounts = emotions.size();
  }

  public MentionEmotionBasicDto(Comment comment) {
    username = comment.getUser().getUsername();
    message = comment.getMessage();
    emotions = comment.getEmotions().stream().map(Emotion::getBody).collect(Collectors.toList());
    emotionCounts = emotions.size();
  }

}

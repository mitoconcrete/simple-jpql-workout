package me.whitebear.jpa.common;

import lombok.Getter;
import me.whitebear.jpa.thread.Thread;

@Getter
public class MentionEmotionThreadDto extends MentionEmotionBasicDto {

  private final String channelName;

  public MentionEmotionThreadDto(Thread thread) {
    super(thread);
    this.channelName = thread.getChannel().getName();
  }
}

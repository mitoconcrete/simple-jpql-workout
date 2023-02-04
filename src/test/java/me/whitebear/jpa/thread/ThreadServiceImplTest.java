package me.whitebear.jpa.thread;

import java.util.List;
import me.whitebear.jpa.channel.Channel;
import me.whitebear.jpa.channel.Channel.Type;
import me.whitebear.jpa.channel.ChannelRepository;
import me.whitebear.jpa.comment.Comment;
import me.whitebear.jpa.comment.CommentRepository;
import me.whitebear.jpa.common.PageDTO;
import me.whitebear.jpa.mention.ThreadMention;
import me.whitebear.jpa.mentionEmotion.MentionEmotionService;
import me.whitebear.jpa.user.User;
import me.whitebear.jpa.user.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ThreadServiceImplTest {

  @Autowired
  UserRepository userRepository;


  @Autowired
  ChannelRepository channelRepository;

  @Autowired
  ThreadService threadService;

  @Autowired
  CommentRepository commentRepository;

  @Autowired
  private ThreadRepository threadRepository;

  @Autowired
  MentionEmotionService mentionEmotionService;

  @Test
  void getMentionedThreadList() {
    // given
    User savedUser = getTestUser("1", "2");
    var newThread = Thread.builder().message("message").build();
    newThread.addMention(savedUser);
    threadService.insert(newThread);

    var newThread2 = Thread.builder().message("message2").build();
    newThread2.addMention(savedUser);
    threadService.insert(newThread2);

    // when
    // 모든 채널에서 내가 멘션된 쓰레드 목록 조회 기능
    var mentionedThreads = savedUser.getThreadMentions().stream().map(ThreadMention::getThread)
        .toList();

    // then
    assert mentionedThreads.containsAll(List.of(newThread, newThread2));
  }

  @Test
  void getNotEmptyThreadList() {
    // given
    var newChannel = Channel.builder().name("c1").type(Type.PUBLIC).build();
    var savedChannel = channelRepository.save(newChannel);
    getTestThread("message", savedChannel);

    Thread newThread2 = getTestThread("", savedChannel);

    // when
    var notEmptyThreads = threadService.selectNotEmptyThreadList(savedChannel);

    // then
    assert !notEmptyThreads.contains(newThread2);
  }

  @Test
  @DisplayName("전체 채널에서 내가 멘션된 쓰레드 상세정보 목록 테스트")
  void selectMentionedThreadListTest() {
    // given
    var user = getTestUser("1", "1");
    var user2 = getTestUser("2", "2");
    var user3 = getTestUser("3", "3");
    var user4 = getTestUser("3", "4");
    var newChannel = Channel.builder().name("c1").type(Type.PUBLIC).build();
    var savedChannel = channelRepository.save(newChannel);
    var thread2 = getTestThreadWithComment("", savedChannel, user
        , user2, "e2", user3, "c2", user4, "ce2");
    var thread1 = getTestThreadWithComment("message", savedChannel, user
        , user2, "e1", user3, "c1", user4, "ce1");

    // when
    var pageDTO = PageDTO.builder().currentPage(1).size(100).build();
    var mentionedThreadList = threadService.selectMentionedThreadList(user.getId(), pageDTO);

    // then
    assert mentionedThreadList.getTotalElements() == 2;
  }

  @Test
  @DisplayName("전체 채널에 내가 작성한 쓰레드 그리고 댓글 중 이모지가 달려있는 쓰레드/댓글 상세정보 목록1 : 스레드 1(이모지o), 댓글 1(이모지o)이 존재할 때 스레드와 댓글의 데이터를 가져옵니다.")
  void getEmojiAttachedThreadAndCommentsTest1() {
    // given
    var user = getTestUser("1", "1");
    var user2 = getTestUser("2", "2");

    var publicChannel = Channel.builder().name("c1").type(Type.PUBLIC).build();
    var savedPublicChannel = channelRepository.save(publicChannel);

    var publicThread = Thread.builder().message("message").user(user).build();
    publicThread.setChannel(savedPublicChannel);
    publicThread.addEmotion(user2, "emoji");
    publicThread.addEmotion(user2, "emoji1");

    var comment = Comment.builder().message("thread comment").build();
    comment.setUser(user);
    comment.addEmotion(user2, "emoji");
    comment.addEmotion(user2, "emoji2");
    var savedComment = commentRepository.save(comment);
    publicThread.addComment(savedComment);
    channelRepository.save(savedPublicChannel);

    // when
    var list = mentionEmotionService.search(user.getId());
    // then
    Assertions.assertEquals(list.size(), 2);
    Assertions.assertEquals(list.get(0).getMessage(), publicThread.getMessage());
    Assertions.assertEquals(list.get(1).getMessage(), savedComment.getMessage());
  }

  @Test
  @DisplayName("전체 채널에 내가 작성한 쓰레드 그리고 댓글 중 이모지가 달려있는 쓰레드/댓글 상세정보 목록2 : 스레드 1(이모지o), 댓글 1(이모지x)이 존재할 때 스레드만 가져옵니다.")
  void getEmojiAttachedThreadAndCommentsTest2() {
    // given
    var user = getTestUser("1", "1");
    var user2 = getTestUser("2", "2");

    var publicChannel = Channel.builder().name("c1").type(Type.PUBLIC).build();
    var savedPublicChannel = channelRepository.save(publicChannel);

    var publicThread = Thread.builder().message("message").user(user).build();
    publicThread.setChannel(savedPublicChannel);
    publicThread.addEmotion(user2, "emoji");

    var comment = Comment.builder().message("thread comment").build();
    comment.setUser(user);
    var savedComment = commentRepository.save(comment);
    publicThread.addComment(savedComment);
    channelRepository.save(savedPublicChannel);

    var list = mentionEmotionService.search(user.getId());

    Assertions.assertEquals(list.size(), 1);
    Assertions.assertEquals(list.get(0).getMessage(), publicThread.getMessage());
  }

  @Test
  @DisplayName("전체 채널에 내가 작성한 쓰레드 그리고 댓글 중 이모지가 달려있는 쓰레드/댓글 상세정보 목록2 : 스레드 1(이모지x), 댓글 1(이모지o)이 존재할 때 댓글만 가져옵니다.")
  void getEmojiAttachedThreadAndCommentsTest3() {
    // given
    var user = getTestUser("1", "1");
    var user2 = getTestUser("2", "2");

    var publicChannel = Channel.builder().name("c1").type(Type.PUBLIC).build();
    var savedPublicChannel = channelRepository.save(publicChannel);

    var publicThread = Thread.builder().message("message").user(user).build();
    publicThread.setChannel(savedPublicChannel);

    var comment = Comment.builder().message("thread comment").build();
    comment.setUser(user);
    comment.addEmotion(user2, "emoji");
    var savedComment = commentRepository.save(comment);
    publicThread.addComment(savedComment);
    channelRepository.save(savedPublicChannel);

    var list = mentionEmotionService.search(user.getId());

    Assertions.assertEquals(list.size(), 1);
    Assertions.assertEquals(list.get(0).getMessage(), savedComment.getMessage());
  }

  @Test
  @DisplayName("전체 채널에 내가 작성한 쓰레드 그리고 댓글 중 이모지가 달려있는 쓰레드/댓글 상세정보 목록2 : 스레드 1(이모지x), 댓글 1(이모지x)이 존재할 때 아무것도 가져오지 않습니다.")
  void getEmojiAttachedThreadAndCommentsTest4() {
    // given
    var user = getTestUser("1", "1");
    var user2 = getTestUser("2", "2");

    var publicChannel = Channel.builder().name("c1").type(Type.PUBLIC).build();
    var savedPublicChannel = channelRepository.save(publicChannel);

    var publicThread = Thread.builder().message("message").user(user).build();
    publicThread.setChannel(savedPublicChannel);

    var comment = Comment.builder().message("thread comment").build();
    comment.setUser(user);
    var savedComment = commentRepository.save(comment);
    publicThread.addComment(savedComment);
    channelRepository.save(savedPublicChannel);

    var list = mentionEmotionService.search(user.getId());

    Assertions.assertEquals(list.size(), 0);
  }

  private User getTestUser(String username, String password) {
    var newUser = User.builder().username(username).password(password).build();
    return userRepository.save(newUser);
  }

  private Comment getTestComment(User user, String message) {
    var newComment = Comment.builder().message(message).build();
    newComment.setUser(user);
    return commentRepository.save(newComment);
  }

  private Thread getTestThread(String message, Channel savedChannel) {
    var newThread = Thread.builder().message(message).build();
    newThread.setChannel(savedChannel);
    return threadService.insert(newThread);
  }

  private Thread getTestThread(String message, Channel channel, User mentionedUser) {
    var newThread = getTestThread(message, channel);
    newThread.addMention(mentionedUser);
    return threadService.insert(newThread);
  }

  private Thread getTestThread(String message, Channel channel, User mentionedUser,
      User emotionUser, String emotionValue) {
    var newThread = getTestThread(message, channel, mentionedUser);
    newThread.addEmotion(emotionUser, emotionValue);
    return threadService.insert(newThread);
  }

  private Thread getTestThread(String message, Channel channel, User mentionedUser,
      User emotionUser, String emotionValue, User commentUser, String commentMessage) {
    var newThread = getTestThread(message, channel, mentionedUser, emotionUser, emotionValue);
    newThread.addComment(getTestComment(commentUser, commentMessage));
    return threadService.insert(newThread);
  }

  private Thread getTestThreadWithComment(String message, Channel channel, User mentionedUser,
      User emotionUser, String emotionValue, User commentUser, String commentMessage,
      User commentEmotionUser, String commentEmotionValue) {
    var newThread = getTestThread(message, channel, mentionedUser, emotionUser, emotionValue,
        commentUser, commentMessage);
    newThread.getComments()
        .forEach(comment -> comment.addEmotion(commentEmotionUser, commentEmotionValue));
    return threadService.insert(newThread);
  }
}
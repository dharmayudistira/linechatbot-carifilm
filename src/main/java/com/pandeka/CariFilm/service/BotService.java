package com.pandeka.CariFilm.service;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.Multicast;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.profile.UserProfileResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class BotService {

    @Autowired
    private LineMessagingClient lineMessagingClient;

    // push message
    public void push(PushMessage pushMessage) {
        try {
            lineMessagingClient.pushMessage(pushMessage).get();
        }catch (InterruptedException | ExecutionException e ) {
            throw new RuntimeException(e);
        }
    }

    // send a multicast message
    public void multicast(Set<String> to, Message message) {
        try {
            Multicast multicast = new Multicast(to, message);
            lineMessagingClient.multicast(multicast).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    // base reply message
    public void reply(ReplyMessage replyMessage) {
        try {
            lineMessagingClient.replyMessage(replyMessage).get();
        }catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    // reply with a template message like Button, Greeting, Carousel, and Flex without additional info
    public void reply(String replyToken, Message message) {
        ReplyMessage replyMessage = new ReplyMessage(replyToken, message);
        reply(replyMessage);
    }

    // reply with a template message (like button, greeting, carousel, and flex) with additional info
    public void reply(String replyToken, List<Message> message) {
        ReplyMessage replyMessage = new ReplyMessage(replyToken, message);
        reply(replyMessage);
    }

    // reply with a string message without additional info
    public void replyText(String replyToken, String message) {
        TextMessage textMessage = new TextMessage(message);
        reply(replyToken, textMessage);
    }

    // reply with a string message with additional info
    public void replyText(String replyToken, String[] messages) {
        List<Message> textMessages = Arrays
                .stream(messages)
                .map(TextMessage::new)
                .collect(Collectors.toList());
        reply(replyToken, textMessages);
    }

    // get user profile
    public UserProfileResponse getProfile(String userId) {
        try {
            return lineMessagingClient.getProfile(userId).get();
        }catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    // leave group
    public void leaveGroup(String groupId) {
        try {
            lineMessagingClient.leaveGroup(groupId).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    // leave room
    public void leaveRoom(String groupId) {
        try {
            lineMessagingClient.leaveGroup(groupId).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}

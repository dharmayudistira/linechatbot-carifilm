package com.pandeka.CariFilm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.bot.client.LineSignatureValidator;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.JoinEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.ReplyEvent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.objectmapper.ModelObjectMapper;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.pandeka.CariFilm.model.LineEvents;
import com.pandeka.CariFilm.model.Movies;
import com.pandeka.CariFilm.service.BotService;
import com.pandeka.CariFilm.service.BotTemplate;
import com.pandeka.CariFilm.service.DBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class BotController {

    @Autowired
    @Qualifier("lineSignatureValidator")
    private LineSignatureValidator lineSignatureValidator;

    @Autowired
    private BotService botService;

    @Autowired
    private BotTemplate botTemplate;

    @Autowired
    private DBService dbService;

    private UserProfileResponse sender = null;
    private Movies movies = null;

    @RequestMapping(value="/webhook", method= RequestMethod.POST)
    public ResponseEntity<String> callback(
            @RequestHeader("X-Line-Signature") String xLineSignature,
            @RequestBody String eventsPayload
    ) {

        try {
            // validator
            if (!lineSignatureValidator.validateSignature(eventsPayload.getBytes(), xLineSignature)) {
                throw new RuntimeException("Invalid Signature Validation");
            }

            // parsing event
            ObjectMapper objectMapper = ModelObjectMapper.createNewObjectMapper();
            LineEvents lineEventsModel = objectMapper.readValue(eventsPayload, LineEvents.class);

            lineEventsModel.getEvents().forEach((event) -> {
                //code for reply

                if (event instanceof JoinEvent || event instanceof FollowEvent) { // check if it's from join or follow
                    String replyToken = ((ReplyEvent) event).getReplyToken();
                    handleJointOrFollowEvent(replyToken, event.getSource());
                } else if (event instanceof MessageEvent) { // check if it's from message
                    handleMessageEvent((MessageEvent) event);
                }
            });

            return new ResponseEntity<>(HttpStatus.OK);

        }catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private void greetingMessage(String replyToken, Source source, String additionalMessage) {
        if (sender == null) {
            String senderId = source.getSenderId();
            sender = botService.getProfile(senderId);
        }

        TemplateMessage greetingMessage = botTemplate.greetingMessage(source, sender);

        if (additionalMessage != null) {
            List<Message> messages = new ArrayList<>();
            messages.add(new TextMessage(additionalMessage));
            messages.add(greetingMessage);
            botService.reply(replyToken, messages);
        }else {
            botService.reply(replyToken, greetingMessage);
        }
    }

    private void handleJointOrFollowEvent(String replyToken, Source source) {
        greetingMessage(replyToken, source, null);
    }

    private void handleMessageEvent(MessageEvent event) {
        String replyToken = event.getReplyToken();
        MessageContent content = event.getMessage();
        Source source = event.getSource();
        String senderId = source.getSenderId();
        sender = botService.getProfile(senderId);

        if (content instanceof TextMessageContent) {

        }else {
            greetingMessage(replyToken, source, null);
        }
    }


}

package com.pandeka.CariFilm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.bot.client.LineSignatureValidator;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.JoinEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.ReplyEvent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.source.GroupSource;
import com.linecorp.bot.model.event.source.RoomSource;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.event.source.UserSource;
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
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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

        if (content instanceof TextMessageContent) { // check if it's from text message, then process the message
            handleTextMessage(replyToken, (TextMessageContent) content, source);
        }else { // if it's not from text message, so it's must be from "add friend" .. then create a greeting message
            greetingMessage(replyToken, source, null);
        }
    }

    private void handleTextMessage(String replyToken, TextMessageContent content, Source source) {
        if (source instanceof GroupSource) { // check if it's from a group chat

        }else if (source instanceof RoomSource) { // check if it's from a room chat

        }else if (source instanceof UserSource) { // check if it's from a personal chat
            handlePersonalChat(replyToken, content.getText());
        }else {
            botService.replyText(replyToken, "Mohon maaf terjadi kesalahan!");
        }
    }

    private void handlePersonalChat(String replyToken, String textMessage) {
        String action = textMessage.toLowerCase();

        if (action.contains("lihat daftar film")) { //check if it's from "Lihat Daftar Film" feature
            showCarouselMovies(replyToken, null);
        }else if(action.contains("lihat favorite")) { //check if it's from "Lihat Favorite" feature

        }else { // bot can't understand user message, so reply it with guide information

        }
    }

    private void showCarouselMovies(String replyToken, String additionalInfo) {
        if ((movies == null) || (movies.getResults().size() < 1)) {
            getMovies();
        }

        TemplateMessage carouselMovies = botTemplate.carouselMovies(movies);

        if (additionalInfo == null) {
            botService.reply(replyToken, carouselMovies);
            return;
        }

        List<Message> additionalMessage = new ArrayList<>();
        additionalMessage.add(new TextMessage(additionalInfo));
        additionalMessage.add(carouselMovies);
        botService.reply(replyToken, additionalMessage);
    }

    private void getMovies()  {
        String URL = "https://api.themoviedb.org/3/movie/now_playing?api_key=f8477f51763d4d05f65a2d9bb1ac93fe";

        try (CloseableHttpAsyncClient client = HttpAsyncClients.createDefault()){
            client.start();

            // Use HTTPGet to retrieve data
            HttpGet get = new HttpGet(URL);

            Future<HttpResponse> future = client.execute(get, null);
            HttpResponse response = future.get();

            //Get the response from the GET request
            InputStream inputStream = response.getEntity().getContent();
            String encoding = StandardCharsets.UTF_8.name();
            String jsonResponse = IOUtils.toString(inputStream, encoding);

            ObjectMapper objectMapper = new ObjectMapper();
            movies = objectMapper.readValue(jsonResponse, Movies.class);
        }catch (InterruptedException | ExecutionException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}

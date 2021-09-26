package com.se21.calbot.listeners;

import ch.qos.logback.core.net.SMTPAppenderBase;
import com.se21.calbot.ClientManager.ClientManager;
import com.se21.calbot.controllers.Controller;
import com.se21.calbot.factories.clientFactory;
import com.se21.calbot.model.User;
import discord4j.core.object.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.se21.calbot.enums.Enums.operationType.Add;
import static com.se21.calbot.enums.Enums.operationType.Retrieve;

@Service
public abstract class MessageListener {
    ClientManager clientObj;

    @Autowired
    Controller controller;
    @Autowired
    clientFactory clientfactory;
    @Autowired
    User user;

    public Mono<Void> processCommand(Message eventMessage) {

        Mono<String> cmd = Mono.just(eventMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .map(Message::getContent);

        discord4j.core.object.entity.User author = eventMessage.getAuthor().orElse(null);
        if(author == null)
            return Mono.empty().then();
        if(eventMessage.getAuthor().map(user -> user.isBot()).orElse(false))
            return Mono.empty().then();

        //No need to make db entry just yet
        user.setDiscordId(author.getUsername()+author.getDiscriminator());
        clientObj = clientfactory.getClient("Discord");
        String response = clientObj.processInput(eventMessage.getContent());
        return Mono.just(eventMessage)
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage("" + response))
                .then();
    }

}

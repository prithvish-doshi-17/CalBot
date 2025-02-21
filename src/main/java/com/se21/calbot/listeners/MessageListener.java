package com.se21.calbot.listeners;

import org.springframework.beans.factory.annotation.Autowired;

import com.se21.calbot.controllers.Controller;
import com.se21.calbot.factories.clientFactory;
import com.se21.calbot.interfaces.ClientManager;

import discord4j.core.object.entity.Message;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;

/**
 * MessageListener manages the listener facility for any event from discord bot.
 */
@Getter
@Setter
public abstract class MessageListener {
    ClientManager clientObj;

    @Autowired
    Controller controller;
    @Autowired
    clientFactory clientfactory;

    /**
     * If any event happens in chatBot, it will call processCommand. Further developer can filter
     * the message to be processed or ignored.
     * @param eventMessage activity in chatbot
     * @return response from bot to user
     */
    public Mono<Void> processCommand(Message eventMessage) {

        Mono<String> cmd = Mono.just(eventMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .map(Message::getContent);

        discord4j.core.object.entity.User author = eventMessage.getAuthor().orElse(null);
        if(author == null)
            return Mono.empty().then();
        if(eventMessage.getAuthor().map(user -> user.isBot()).orElse(false))
            return Mono.empty().then();


        clientObj = clientfactory.getClient("Discord");
        String clientId = author.getUsername()+author.getDiscriminator();
        String response = clientObj.processInput(clientId, eventMessage.getContent());
        return Mono.just(eventMessage)
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage("" + response))
                .then();
    }

}

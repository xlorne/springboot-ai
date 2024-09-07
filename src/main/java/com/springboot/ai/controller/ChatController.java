package com.springboot.ai.controller;

import com.springboot.ai.pojo.ChatMessage;
import com.springboot.ai.service.ChatService;
import lombok.AllArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/chat")
@AllArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final EmbeddingModel embeddingModel;

    @PostMapping("stream")
    public Flux<String> stream(@RequestBody ChatMessage message) {
        return chatService.streamChat(message.getChatId(), message.getMessage());
    }

    @PostMapping("embedding")
    public float[] embedding(@RequestBody String request) {
        return embeddingModel.embed(request);
    }

    @PostMapping("hi")
    public String hi(@RequestBody ChatMessage message) {
        return chatService.syncChat(message.getChatId(), message.getMessage());
    }

}

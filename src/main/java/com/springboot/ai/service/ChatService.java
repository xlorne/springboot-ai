package com.springboot.ai.service;

import com.springboot.ai.AIConfiguration;
import com.springboot.ai.advisors.LoggingAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Service
public class ChatService {

    private final ChatClient chatClient;

    public ChatService(ChatClient.Builder modelBuilder,
                       ChatMemory chatMemory,
                       VectorStore vectorStore) {
        SearchRequest request = SearchRequest.defaults()
                .withTopK(AIConfiguration.CHAT_MEMORY_SIMILARITY_TOP_K)
                .withSimilarityThreshold(AIConfiguration.CHAT_MEMORY_SIMILARITY_THRESHOLD);

        this.chatClient =
                modelBuilder
                        .defaultSystem(AIConfiguration.DEFAULT_SYSTEM_TEMPLATE_TEXT)
                        .defaultFunctions("currentTime")
                        .defaultAdvisors(
                                //chat session manager
                                new PromptChatMemoryAdvisor(chatMemory, AIConfiguration.DEFAULT_PROMPT_MEMORY_TEMPLATE_TEXT),
                                //question answer advisor
                                new QuestionAnswerAdvisor(vectorStore, request, AIConfiguration.DEFAULT_QUESTION_ANSWER_TEMPLATE_TEXT),
                                //request response logging
                                new LoggingAdvisor()
                        )
                        .build();
    }

    public String syncChat(String chatId, String userMessage) {
        ChatClient.CallResponseSpec responseSpec = chatClient.prompt()
                .user(userMessage)
                .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, AIConfiguration.CHAT_MEMORY_RETRIEVE_SIZE_VALUE))
                .call();
        ChatResponse chatResponse = responseSpec.chatResponse();
        return chatResponse.getResult().getOutput().getContent();
    }

    public Flux<String> streamChat(String chatId, String userMessage) {
        ChatClient.StreamResponseSpec responseSpec = chatClient.prompt()
                .user(userMessage)
                .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, AIConfiguration.CHAT_MEMORY_RETRIEVE_SIZE_VALUE))
                .stream();
        Flux<ChatResponse> chatResponse = responseSpec.chatResponse();
        return chatResponse.map(r -> r.getResult().getOutput().getContent());
    }
}

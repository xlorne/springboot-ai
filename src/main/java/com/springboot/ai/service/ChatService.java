package com.springboot.ai.service;

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

    private static final int CHAT_MEMORY_RETRIEVE_SIZE_VALUE = 1000;

    private static final String defaultSystemText = """
            我是一个聊天机器人，我的名字是小明,我可以帮你查询时间.
            """;

    private static final String defaultPromptMemoryText = """
            这是历史的对话数据：
            ---------------------
            MEMORY:
            {memory}
            ---------------------
            """;

    private static final String defaultQuestionAnswerText = """
            以下的内容是知识库内容：
            ---------------------
            {question_answer_context}
            ---------------------
            """;

    public ChatService(ChatClient.Builder modelBuilder, ChatMemory chatMemory, VectorStore vectorStore) {
        SearchRequest request = SearchRequest.defaults()
                .withTopK(1)
                .withSimilarityThreshold(0.6);

        this.chatClient =
                modelBuilder
                        .defaultSystem(defaultSystemText)
                        .defaultAdvisors(
                                //chat session manager
                                new PromptChatMemoryAdvisor(chatMemory, defaultPromptMemoryText),
                                //question answer advisor
                                new QuestionAnswerAdvisor(vectorStore, request, defaultQuestionAnswerText),
                                //request response logging
                                new LoggingAdvisor()
                        )
                        .defaultFunctions("currentTime")
                        .build();
    }

    public String syncChat(String chatId, String userMessage) {
        ChatClient.CallResponseSpec responseSpec = chatClient.prompt()
                .user(userMessage)
                .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, CHAT_MEMORY_RETRIEVE_SIZE_VALUE))
                .call();
        ChatResponse chatResponse = responseSpec.chatResponse();
        return chatResponse.getResult().getOutput().getContent();
    }

    public Flux<String> streamChat(String chatId, String userMessage) {
        ChatClient.StreamResponseSpec responseSpec = chatClient.prompt()
                .user(userMessage)
                .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, CHAT_MEMORY_RETRIEVE_SIZE_VALUE))
                .stream();
        Flux<ChatResponse> chatResponse = responseSpec.chatResponse();
        return chatResponse.map(r -> r.getResult().getOutput().getContent());
    }
}

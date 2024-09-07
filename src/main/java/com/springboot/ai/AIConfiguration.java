package com.springboot.ai;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfiguration {

    // Chat memory retrieve size
    public static final int CHAT_MEMORY_RETRIEVE_SIZE_VALUE = 1000;
    // Similarity threshold for chat memory
    public static final float CHAT_MEMORY_SIMILARITY_THRESHOLD = 0.6f;
    // Top K for chat memory similarity search
    public static final int CHAT_MEMORY_SIMILARITY_TOP_K = 1;


    public static final String DEFAULT_SYSTEM_TEMPLATE_TEXT = """
            我是一个聊天机器人，我的名字是小明,我可以帮你查询时间.
            """;

    public static final String DEFAULT_PROMPT_MEMORY_TEMPLATE_TEXT = """
            这是历史的对话数据：
            ---------------------
            MEMORY:
            {memory}
            ---------------------
            """;

    public static final String DEFAULT_QUESTION_ANSWER_TEMPLATE_TEXT = """
            以下的内容是知识库内容：
            ---------------------
            {question_answer_context}
            ---------------------
            """;

    @Bean
    public ChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        return new SimpleVectorStore(embeddingModel);
    }

    @Bean
    public TokenTextSplitter tokenTextSplitter() {
        return new TokenTextSplitter();
    }


}

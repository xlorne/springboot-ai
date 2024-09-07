package com.springboot.ai;

import org.springframework.ai.autoconfigure.retry.SpringAiRetryProperties;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class SpringBootAiAutoConfiguration {


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

    @Bean
    public ResponseErrorHandler responseErrorHandler(SpringAiRetryProperties properties) {

        return new ResponseErrorHandler() {

            @Override
            public boolean hasError(@NonNull ClientHttpResponse response) throws IOException {
                return response.getStatusCode().isError();
            }

            @Override
            public void handleError(@NonNull ClientHttpResponse response) throws IOException {
                if (response.getStatusCode().isError()) {
                    String error = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
                    String message = String.format("%s - %s", response.getStatusCode().value(), error);

                    // Explicitly configured transient codes
                    if (properties.getOnHttpCodes().contains(response.getStatusCode().value())) {
                        throw new TransientAiException(message);
                    }

                    // onClientErrors - If true, do not throw a NonTransientAiException,
                    // and do not attempt retry for 4xx client error codes, false by
                    // default.
                    if (!properties.isOnClientErrors() && response.getStatusCode().is4xxClientError()) {
                        throw new NonTransientAiException(message);
                    }

                    // Explicitly configured non-transient codes
                    if (!CollectionUtils.isEmpty(properties.getExcludeOnHttpCodes())
                            && properties.getExcludeOnHttpCodes().contains(response.getStatusCode().value())) {
                        throw new NonTransientAiException(message);
                    }
                    throw new TransientAiException(message);
                }
            }
        };
    }

}

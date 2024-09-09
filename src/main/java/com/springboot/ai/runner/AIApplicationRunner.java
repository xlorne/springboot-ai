package com.springboot.ai.runner;

import com.springboot.ai.AIConfiguration;
import com.springboot.ai.reader.QATextDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class AIApplicationRunner implements ApplicationRunner {

    @Value("classpath:texts/qa.txt")
    private Resource qaDocs;

    @jakarta.annotation.Resource
    private VectorStore vectorStore;

    @jakarta.annotation.Resource
    private TokenTextSplitter tokenTextSplitter;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        vectorStore.write(tokenTextSplitter.transform(new QATextDocumentReader(qaDocs).read()));

        SearchRequest request = SearchRequest.query("你的兴趣爱好是什么？")
                .withTopK(AIConfiguration.CHAT_MEMORY_SIMILARITY_TOP_K)
                .withSimilarityThreshold(AIConfiguration.CHAT_MEMORY_SIMILARITY_THRESHOLD);

        vectorStore.similaritySearch(request).forEach(doc -> {
            System.out.println("question: \n" + request.getQuery());
            System.out.println("answer: \n" + doc.getContent());
        });
    }
}

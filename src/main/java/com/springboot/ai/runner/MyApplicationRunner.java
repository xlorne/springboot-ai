package com.springboot.ai.runner;

import com.springboot.ai.reader.QATextDocumentReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyApplicationRunner implements ApplicationRunner {

    @Value("classpath:texts/qa.txt")
    private Resource qaDocs;

    @jakarta.annotation.Resource
    private VectorStore vectorStore;

    @jakarta.annotation.Resource
    private TokenTextSplitter tokenTextSplitter;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        vectorStore.write(tokenTextSplitter.transform(new QATextDocumentReader(qaDocs).read()));

        SearchRequest request = SearchRequest.query("你的兴趣是什么")
                .withTopK(1)
                .withSimilarityThreshold(0.6);

        vectorStore.similaritySearch(request).forEach(doc -> {
            log.info("answer: \n {}", doc.getContent());
        });
    }
}

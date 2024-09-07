package com.springboot.ai.reader;

import org.apache.commons.io.IOUtils;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class QATextDocumentReader implements DocumentReader {

    private final String content;

    public QATextDocumentReader(Resource resource) throws IOException {
        this.content = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
    }

    @Override
    public List<Document> get() {
        return Arrays.stream(content.split("---"))
                .map(String::trim)
                .map(Document::new)
                .toList();

    }
}

package com.sdlc.pro.txboard.handler;

import com.sdlc.pro.txboard.model.TransactionSummary;
import com.sdlc.pro.txboard.repository.TransactionLogRepository;
import com.sdlc.pro.txboard.util.ObjectMapperUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.HttpRequestHandler;

import java.io.IOException;
import java.io.PrintWriter;

public class TransactionSummaryHttpHandler implements HttpRequestHandler {
    private final Object objectMapper;
    private final TransactionLogRepository transactionLogRepository;

    public TransactionSummaryHttpHandler(Object objectMapper, TransactionLogRepository transactionLogRepository) {
        this.objectMapper = objectMapper;
        this.transactionLogRepository = transactionLogRepository;
    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter writer = response.getWriter();
        TransactionSummary transactionSummary = transactionLogRepository.getTransactionSummary();
        String json = ObjectMapperUtils.mapAsJsonString(objectMapper, transactionSummary);
        writer.write(json);
        writer.flush();
    }
}

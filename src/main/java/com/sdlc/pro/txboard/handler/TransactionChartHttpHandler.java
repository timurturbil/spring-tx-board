package com.sdlc.pro.txboard.handler;

import com.sdlc.pro.txboard.dto.TransactionChart;
import com.sdlc.pro.txboard.model.DurationDistribution;
import com.sdlc.pro.txboard.repository.TransactionLogRepository;
import com.sdlc.pro.txboard.util.ObjectMapperUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.HttpRequestHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class TransactionChartHttpHandler implements HttpRequestHandler {
    private final Object objectMapper;
    private final TransactionLogRepository transactionLogRepository;

    public TransactionChartHttpHandler(Object objectMapper, TransactionLogRepository transactionLogRepository) {
        this.objectMapper = objectMapper;
        this.transactionLogRepository = transactionLogRepository;
    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter writer = response.getWriter();

        List<DurationDistribution> durationDistributions = transactionLogRepository.getDurationDistributions();
        TransactionChart chartData = new TransactionChart(durationDistributions);

        String json = ObjectMapperUtils.mapAsJsonString(objectMapper, chartData);
        writer.write(json);
        writer.flush();
    }
}

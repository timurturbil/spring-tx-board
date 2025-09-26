package com.sdlc.pro.txboard.handler;

import com.sdlc.pro.txboard.config.TxBoardProperties;
import com.sdlc.pro.txboard.util.ObjectMapperUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.HttpRequestHandler;

import java.io.IOException;
import java.io.PrintWriter;

public class AlarmingThresholdHttpHandler implements HttpRequestHandler {
    private final Object objectMapper;
    private final TxBoardProperties.AlarmingThreshold alarmingThreshold;

    public AlarmingThresholdHttpHandler(Object objectMapper, TxBoardProperties.AlarmingThreshold alarmingThreshold) {
        this.objectMapper = objectMapper;
        this.alarmingThreshold = alarmingThreshold;
    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter writer = response.getWriter();
        String json = ObjectMapperUtils.mapAsJsonString(objectMapper, this.alarmingThreshold);
        writer.write(json);
        writer.flush();
    }
}

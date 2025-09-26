package com.sdlc.pro.txboard.config;

import com.sdlc.pro.txboard.handler.AlarmingThresholdHttpHandler;
import com.sdlc.pro.txboard.handler.TransactionChartHttpHandler;
import com.sdlc.pro.txboard.handler.TransactionLogsHttpHandler;
import com.sdlc.pro.txboard.handler.TransactionSummaryHttpHandler;
import com.sdlc.pro.txboard.listener.TransactionLogListener;
import com.sdlc.pro.txboard.listener.TransactionLogPersistenceListener;
import com.sdlc.pro.txboard.repository.InMemoryTransactionLogRepository;
import com.sdlc.pro.txboard.repository.RedisTransactionLogRepository;
import com.sdlc.pro.txboard.repository.TransactionLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import java.util.Map;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(value = {WebMvcConfigurer.class, HttpRequestHandler.class})
public class SpringTxBoardWebConfiguration implements WebMvcConfigurer, ApplicationContextAware {
    private static final int ORDER = 0;
    private static final Logger log = LoggerFactory.getLogger(SpringTxBoardWebConfiguration.class);

    private ApplicationContext applicationContext;

    @Bean("sdlcProSpringTxLogRepository")
    @ConditionalOnMissingBean(TransactionLogRepository.class)
    public TransactionLogRepository transactionLogRepository(TxBoardProperties txBoardProperties) {
        TxBoardProperties.StorageType storageType = txBoardProperties.getStorage();
        log.info("Spring Tx Board is configured to use {} storage for transaction logs.", storageType);

        return switch (storageType) {
            case IN_MEMORY -> new InMemoryTransactionLogRepository(txBoardProperties);
            case REDIS -> new RedisTransactionLogRepository();
        };
    }

    @Bean("sdlcProTransactionLogPersistenceListener")
    public TransactionLogListener transactionLogPersistenceListener(TransactionLogRepository transactionLogRepository) {
        return new TransactionLogPersistenceListener(transactionLogRepository);
    }

    @ConditionalOnClass(name = "com.fasterxml.jackson.databind.ObjectMapper")
    @Bean("sdlcProTxBoardRestHandlerMapping")
    public HandlerMapping txBoardRestHandlerMapping(TxBoardProperties txBoardProperties,
                                                    TransactionLogRepository transactionLogRepository) throws ClassNotFoundException {

        Class<?> mapperClass = Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
        Object objectMapper = applicationContext.getBean(mapperClass);
        return buildHandlerMapping(objectMapper, txBoardProperties, transactionLogRepository);
    }

    @ConditionalOnClass(name = "tools.jackson.databind.ObjectMapper")
    @Bean("sdlcProTxBoardRestHandlerMapping")
    public HandlerMapping txBoardRestHandlerMappingFor(TxBoardProperties txBoardProperties,
                                                       TransactionLogRepository transactionLogRepository) throws ClassNotFoundException {

        Class<?> mapperClass = Class.forName("tools.jackson.databind.ObjectMapper");
        Object objectMapper = applicationContext.getBean(mapperClass);
        return buildHandlerMapping(objectMapper, txBoardProperties, transactionLogRepository);
    }

    private HandlerMapping buildHandlerMapping(Object objectMapper, TxBoardProperties txBoardProperties,
                                               TransactionLogRepository transactionLogRepository) {
        return new SimpleUrlHandlerMapping(Map.of(
                "/api/spring-tx-board/config/alarming-threshold", new AlarmingThresholdHttpHandler(objectMapper, txBoardProperties.getAlarmingThreshold()),
                "/api/spring-tx-board/tx-summary", new TransactionSummaryHttpHandler(objectMapper, transactionLogRepository),
                "/api/spring-tx-board/tx-logs", new TransactionLogsHttpHandler(objectMapper, transactionLogRepository),
                "/api/spring-tx-board/tx-charts", new TransactionChartHttpHandler(objectMapper, transactionLogRepository)
        ), ORDER);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/tx-board/ui/**")
                .addResourceLocations("classpath:/META-INF/tx-board/ui/")
                .setCachePeriod(0);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/tx-board/ui", "/tx-board/ui/index.html");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

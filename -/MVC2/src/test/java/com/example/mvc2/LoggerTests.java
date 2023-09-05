package com.example.mvc2;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@ContextConfiguration(classes = Mvc2Application.class)
@RunWith(SpringRunner.class)
public class LoggerTests {
    private static final Logger logger = LoggerFactory.getLogger(LoggerTests.class);

    @Test
    public void LoggerTest() {
        System.out.println(logger.getName());
        logger.debug("debug log");
        logger.info("info log");
        logger.error("error log");
        logger.warn("warn log");
    }
}

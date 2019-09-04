package com.dc.thread;

import com.dc.thread.pipeline.tools.MyThreadFactory;
import com.dc.thread.pipeline.tools.SpringBootContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SpringBootApplication(scanBasePackages = "com.dc.thread")
public class ThreadApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThreadApplication.class, args);
    }

    @Value("${thread.maxPoolSize}")
    private Integer maxPoolSize;

    @Bean
    public ExecutorService executorService() {
        return new ThreadPoolExecutor(1,
                maxPoolSize,
                60, TimeUnit.MINUTES,
                new SynchronousQueue<>(), new MyThreadFactory("dc-step"), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Bean
    public SpringBootContext springBootContext() {
        return new SpringBootContext();
    }
}

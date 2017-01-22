package com.leeo.hystrix;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SemaphoreEchoCommand extends HystrixCommand<String> {
    private static Logger logger = LoggerFactory.getLogger(SemaphoreEchoCommand.class);

    private String input;

    public SemaphoreEchoCommand(String input) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Semaphore Echo"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("Echo"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                    /*配置信号量隔离方式,默认采用线程池隔离 */    
                    .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                    /*设置的信号量最大值为2*/
                    .withExecutionIsolationSemaphoreMaxConcurrentRequests(2)));
        this.input = input;
    }

    @Override
    protected String run() throws Exception {
        logger.info("Run command with input: {}", this.input);
        Thread.sleep(100);
        
        return "Echo: " + this.input;
    }
}
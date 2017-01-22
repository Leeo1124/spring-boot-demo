package com.leeo.hystrix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixRequestCache;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategyDefault;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

public class ThreadEchoCommand extends HystrixCommand<String> {
    
    public static final HystrixCommandKey COMMAND_KEY = HystrixCommandKey.Factory.asKey("Echo");
    private static Logger logger = LoggerFactory.getLogger(ThreadEchoCommand.class);

    private String input;

    public ThreadEchoCommand(String input) {
        super(Setter
                /*该命令属于哪一个组，可以帮助我们更好的组织命令*/
                .withGroupKey(
                    HystrixCommandGroupKey.Factory.asKey("EchoGroup")
                )
                /*该命令的名称*/
                .andCommandKey(COMMAND_KEY)
                /*该命令所属线程池的名称，同样配置的命令会共享同一线程池，若不配置，会默认使用GroupKey作为线程池名称*/
                .andThreadPoolKey(
                    HystrixThreadPoolKey.Factory.asKey("EchoThreadPool")
                )
                /*该命令的一些设置，包括断路器的配置，隔离策略，降级设置，以及一些监控指标等*/
                .andCommandPropertiesDefaults(
                    HystrixCommandProperties.Setter()
                    /*配置隔离策略,默认采用线程池隔离 */
                    .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
                    /*配置依赖超时时间,500毫秒*/ 
                    .withExecutionTimeoutInMilliseconds(500)
                )
                /*关于线程池的配置，包括线程池大小，排队队列的大小等*/
                .andThreadPoolPropertiesDefaults(
                    HystrixThreadPoolProperties.Setter().withCoreSize(10)
                )
        );
        this.input = input;
    }

    @Override
    protected String run() {
        logger.info("Run command with input: {}", this.input);
        //sleep 1 秒,调用会超时  
//        TimeUnit.MILLISECONDS.sleep(1000);  
//        Thread.sleep(1000);
        return "Echo: " + this.input;
    }
    
    /**
     * 使用Fallback() 提供降级策略
     */
    @Override  
    protected String getFallback() {  
        return "exeucute Falled";  
    }  

    /**
     * 重写getCacheKey方法,实现区分不同请求的逻辑 
     * (需调用HystrixRequestContext.initializeContext()初始化HystrixRequestContext，否则会报错
     * Servlet容器中使用HystrixRequestContextServletFilter)
     */
//    @Override
//    protected String getCacheKey() {
//        return this.input;
//    }
//
//    public static void flushCache(String cacheKey) {
//        HystrixRequestCache.getInstance(COMMAND_KEY,
//                HystrixConcurrencyStrategyDefault.getInstance()).clear(cacheKey);
//    }
}
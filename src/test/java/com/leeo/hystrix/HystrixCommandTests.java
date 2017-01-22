package com.leeo.hystrix;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import rx.Observable;
import rx.Observer;
import rx.functions.Action1;

import com.leeo.hystrix.CollapseEchoHystrixCommand;
import com.leeo.hystrix.CommandWithFallbackViaNetwork;
import com.leeo.hystrix.SemaphoreEchoCommand;
import com.leeo.hystrix.ThreadEchoCommand;
import com.netflix.hystrix.HystrixRequestLog;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HystrixCommandTests {
    private static Logger logger = LoggerFactory.getLogger(HystrixCommandTests.class);

    /**
     * 同步调用(实现getCacheKey方法时调用报错)
     */
    @Test
    public void synchronousExecute() {
        ThreadEchoCommand command = new ThreadEchoCommand("hello world");
        String result = command.execute();
        assertThat(result, equalTo("Echo: hello world"));
    }

    /**
     * 异步调用,可自由控制获取结果时机
     * @throws Exception
     */
    @Test
    public void asynchronousExecute() throws Exception {
        ThreadEchoCommand command = new ThreadEchoCommand("hello world");
        Future<String> future = command.queue();
        while (!future.isDone()) {
            logger.info("Do other things ...");
        }
        //get操作不能超过command定义的超时时间,默认:1秒  
        String result = future.get(1000, TimeUnit.MILLISECONDS);
        assertThat(result, equalTo("Echo: hello world"));
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void reactiveExecute1() throws Exception {
        ThreadEchoCommand command1 = new ThreadEchoCommand("hello world");
        //注册观察者事件拦截 
        Observable<ThreadEchoCommand> result = Observable.just(command1);
        //注册结果回调事件
        result.subscribe(new Action1<ThreadEchoCommand>() {
            @Override
            public void call(ThreadEchoCommand s) {
                //执行结果处理,result 为HelloWorldCommand返回的结果  
                //用户对结果做二次处理. 
                logger.info("Command called. Result is:{}", s.execute());
            }
        });

        Thread.sleep(1000);
    }

    /**
     * 注册异步事件回调执行(注册完整执行生命周期事件 )
     * @throws Exception
     */
    @Test
    public void reactiveExecute2() throws Exception {
        ThreadEchoCommand command1 = new ThreadEchoCommand("hello world-1");
        ThreadEchoCommand command2 = new ThreadEchoCommand("hello world-2");

        Observable<ThreadEchoCommand> observable = Observable.from(new ThreadEchoCommand[]{command1, command2});
        Observer<ThreadEchoCommand> observer = new Observer<ThreadEchoCommand>() {
            @Override
            public void onCompleted() {//onNext/onError完成之后最后回调
                logger.info("Command Completed");
            }

            @Override
            public void onError(Throwable e) {//当产生异常时回调  
                logger.error("Command failled", e);
            }

            @Override
            public void onNext(ThreadEchoCommand command) {//获取结果后回调
                logger.info("Command finished,result is {}", command.execute());
            }
        };
        observable.subscribe(observer);

        Thread.sleep(1000);
    }

    /**
     * 信号量隔离(HystrixCommand在主线程中执行)
     */
    @Test
    public void semaphoresCommandExecute() {
        SemaphoreEchoCommand command = new SemaphoreEchoCommand("hello world");
        assertThat(command.execute(), equalTo("Echo: hello world"));
    }

    /**
     * 信号量隔离(设置的信号量最大值为2， 因此可以看到有2个线程可以成功运行命令，
     * 第三个则会得到一个无法获取信号量的HystrixRuntimeException)
     * @throws Exception
     */
    @Test
    public void semaphoresCommandMultiExecute() throws Exception {
        for (int i = 0; i < 5; i++) {
            final SemaphoreEchoCommand command = new SemaphoreEchoCommand("hello world-" + i);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    command.queue();
                }
            });
            thread.start();
        }
        Thread.sleep(1000);
    }

    /**
     * 请求缓存可以让(CommandKey/CommandGroup)相同的情况下,直接共享结果，
     * 降低依赖调用次数，在高并发和CacheKey碰撞率高场景下可以提升性能.
     */
    @Test
    public void requestCache() {
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        try {
            ThreadEchoCommand command1 = new ThreadEchoCommand("hello world");
            ThreadEchoCommand command2 = new ThreadEchoCommand("hello world");

            assertThat(command1.execute(), equalTo("Echo: hello world"));
            assertThat(command1.isResponseFromCache(), equalTo(false));
            assertThat(command2.execute(), equalTo("Echo: hello world"));
            assertThat(command2.isResponseFromCache(), equalTo(true));
        } finally {
            context.shutdown();
        }

        context = HystrixRequestContext.initializeContext();
        try {
            ThreadEchoCommand command3 = new ThreadEchoCommand("hello world");
            assertThat(command3.execute(), equalTo("Echo: hello world"));
            assertThat(command3.isResponseFromCache(), equalTo(false));
        } finally {
            context.shutdown();
        }
    }

    /**
     * 除了重新初始化RequestContext，Hystrix还提供了另外一种方式来刷新Cache，
     * 该方式需要使用HystrixRequestCache的clear()方法
     */
    @Test
    public void flushCacheTest() {
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        try {
            ThreadEchoCommand command1 = new ThreadEchoCommand("hello world");
            ThreadEchoCommand command2 = new ThreadEchoCommand("hello world");

            assertThat(command1.execute(), equalTo("Echo: hello world"));
            assertThat(command1.isResponseFromCache(), equalTo(false));
            assertThat(command2.execute(), equalTo("Echo: hello world"));
            assertThat(command2.isResponseFromCache(), equalTo(true));

//            ThreadEchoCommand.flushCache("hello world");
            ThreadEchoCommand command3 = new ThreadEchoCommand("hello world");
            assertThat(command3.execute(), equalTo("Echo: hello world"));
            assertThat(command3.isResponseFromCache(), equalTo(false));
        } finally {
            context.shutdown();
        }
    }

    /**
     * 命令调用合并(批量执行请求),允许多个请求合并到一个线程/信号下批量执行
     * (HystrixCollapser用于对多个相同业务的请求合并到一个线程甚至可以合并到一个连接中执行，降低线程交互次和IO数,但必须保证他们属于同一依赖.)
     * @throws Exception
     */
    @Test
    public void collapseCommandTest() throws Exception {
        HystrixRequestContext context = HystrixRequestContext.initializeContext();

        try {
            Future<String> result1 = new CollapseEchoHystrixCommand("hello world-1").queue();
            Future<String> result2 = new CollapseEchoHystrixCommand("hello world-2").queue();
            Future<String> result3 = new CollapseEchoHystrixCommand("hello world-3").queue();

            assertThat(result1.get(),equalTo("Echo: hello world-1"));
            assertThat(result2.get(),equalTo("Echo: hello world-2"));
            assertThat(result3.get(),equalTo("Echo: hello world-3"));

            assertEquals(1, HystrixRequestLog.getCurrentRequest().getExecutedCommands().size());
        } finally {
            context.shutdown();
        }
    }

    /**
     * 依赖调用和降级调用使用不同的线程池做隔离，防止上层线程池跑满，影响二级降级逻辑调用
     */
    @Test
    public void fallbackCommandTest() {
        CommandWithFallbackViaNetwork command = new CommandWithFallbackViaNetwork(18);
        String result = command.execute();
        assertThat(result, equalTo("Fallback exeucute"));
    }
}
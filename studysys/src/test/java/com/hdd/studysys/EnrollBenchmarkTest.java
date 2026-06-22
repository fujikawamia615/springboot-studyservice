package com.hdd.studysys;

import com.hdd.studysys.service.EnrollmentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootTest
public class EnrollBenchmarkTest {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @AfterEach
    public void cleanup() {
        Set<String> keys = redisTemplate.keys("schedule:enrolled:*");
        if (keys != null)
            redisTemplate.delete(keys);
        keys = redisTemplate.keys("student:*");
        if (keys != null)
            redisTemplate.delete(keys);
    }

    @Test
    public void benchmark() throws InterruptedException {
        int totalRequests = 100000;
        int concurrency = 100;
        int students = 10;
        int schedules = 10;

        ExecutorService executor = Executors.newFixedThreadPool(concurrency);
        CountDownLatch latch = new CountDownLatch(totalRequests);
        AtomicInteger success = new AtomicInteger();
        AtomicInteger fail = new AtomicInteger();
        AtomicLong totalTime = new AtomicLong();

        long start = System.currentTimeMillis();

        for (int i = 0; i < totalRequests; i++) {
            final int studentId = (i % students) + 1;
            final int scheduleId = (i % schedules) + 1;
            executor.submit(() -> {
                long t1 = System.nanoTime();
                try {
                    var result = enrollmentService.enroll(studentId, scheduleId);
                    if (Boolean.TRUE.equals(result.get("success")))
                        success.incrementAndGet();
                    else
                        fail.incrementAndGet();
                } catch (Exception e) {
                    fail.incrementAndGet();
                }
                totalTime.addAndGet(System.nanoTime() - t1);
                latch.countDown();
            });
        }

        latch.await();
        long elapsed = System.currentTimeMillis() - start;
        double avgMs = totalTime.get() / 1_000_000.0 / totalRequests;

        System.out.println("========== Service 压测结果 ==========");
        System.out.println("总请求: " + totalRequests);
        System.out.println("并发数: " + concurrency);
        System.out.println("学生数: " + students + ", 课程数: " + schedules);
        System.out.println("成功: " + success.get());
        System.out.println("失败: " + fail.get());
        System.out.println("总耗时: " + elapsed + " ms");
        System.out.println("QPS: " + (totalRequests * 1000L / elapsed));
        System.out.println("平均延迟: " + String.format("%.2f", avgMs) + " ms");
        System.out.println("===================================");

        executor.shutdown();
    }
}
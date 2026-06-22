package com.hdd.studysys.config;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class SentinelConfig {

    @Bean
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }

    @PostConstruct
    public void init() {
        FlowRule rule = new FlowRule();
        rule.setResource("enroll");
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setCount(30000); // 30000 QPS
        FlowRuleManager.loadRules(List.of(rule));
    }
}
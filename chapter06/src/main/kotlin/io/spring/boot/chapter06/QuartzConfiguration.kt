package io.spring.boot.chapter06

import org.quartz.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class QuartzConfiguration {
    @Bean
    fun quartzJobDetail(): JobDetail? {
        return JobBuilder.newJob(BatchScheduledJob::class.java)
            .withIdentity("quartzJob")
            .storeDurably()
            .build()
    }

    @Bean
    fun jobTrigger(): SimpleTrigger? {
        val scheduleBuilder = SimpleScheduleBuilder
            .simpleSchedule()
            .withIntervalInSeconds(5)
            .withRepeatCount(4)

        return TriggerBuilder.newTrigger()
            .forJob(quartzJobDetail())
            .withIdentity("quartzTrigger")
            .withSchedule(scheduleBuilder)
            .build()
    }
}
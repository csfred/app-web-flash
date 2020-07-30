package cn.enilu.flash.api.runner;

import cn.enilu.flash.bean.vo.QuartzJob;
import cn.enilu.flash.service.task.JobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 启动定时任务
 *
 * @author enilu
 * @Date 2019-08-13
 */
@Slf4j
@Component
public class StartJob implements ApplicationRunner {

    @Resource
    private JobService jobService;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        log.info("start Job >>>>>>>>>>>>>>>>>>>>>>>");
        List<QuartzJob> list = jobService.getTaskList();
        for (QuartzJob quartzJob : list) {
            jobService.addJob(quartzJob);
        }
    }
}

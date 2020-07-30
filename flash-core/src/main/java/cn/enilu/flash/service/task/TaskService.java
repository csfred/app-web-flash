package cn.enilu.flash.service.task;


import cn.enilu.flash.bean.entity.system.Task;
import cn.enilu.flash.bean.exception.ApplicationException;
import cn.enilu.flash.bean.exception.ApplicationExceptionEnum;
import cn.enilu.flash.bean.vo.QuartzJob;
import cn.enilu.flash.dao.system.TaskRepository;
import cn.enilu.flash.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 定时任务服务类
 *
 * @author enilu
 * @date 2019-06-13
 */
@Slf4j
@Service
public class TaskService extends BaseService<Task, Long, TaskRepository> {
    @Resource
    private TaskRepository taskRepository;
    @Resource
    private JobService jobService;


    public Task save(Task task) {
        log.info("新增定时任务%s", task.getName());
        task = taskRepository.save(task);
        try {
            jobService.addJob(jobService.getJob(task));
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }
        return task;
    }

    @Override
    public Task update(Task record) {
        log.info("更新定时任务{}", record.getName());
        taskRepository.save(record);
        try {
            QuartzJob job = jobService.getJob(record.getId().toString(), record.getJobGroup());
            if (job != null) {
                jobService.deleteJob(job);
            }
            jobService.addJob(jobService.getJob(record));
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }
        return record;
    }


    public Task disable(Long id) {
        Task task = get(id);
        task.setDisabled(true);
        taskRepository.save(task);
        log.info("禁用定时任务{}", id.toString());
        try {
            QuartzJob job = jobService.getJob(task.getId().toString(), task.getJobGroup());
            if (job != null) {
                jobService.deleteJob(job);
            }
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }
        return task;
    }


    public Task enable(Long id) {
        Task task = get(id);
        task.setDisabled(false);
        taskRepository.save(task);
        log.info("启用定时任务{}", id.toString());
        try {
            QuartzJob job = jobService.getJob(task.getId().toString(), task.getJobGroup());
            if (job != null) {
                jobService.deleteJob(job);
            }
            if (!task.isDisabled()) {
                jobService.addJob(jobService.getJob(task));
            }
        } catch (SchedulerException e) {
            throw new ApplicationException(ApplicationExceptionEnum.TASK_CONFIG_ERROR);
        }
        return task;
    }

    @Override
    public void delete(Long id) {
        Task task = get(id);
        task.setDisabled(true);
        taskRepository.delete(task);
        try {
            log.info("删除定时任务{}", id.toString());
            QuartzJob job = jobService.getJob(task);
            if (job != null) {
                jobService.deleteJob(job);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    public List<Task> queryAllByNameLike(String name) {
        return taskRepository.findByNameLike("%" + name + "%");
    }
}

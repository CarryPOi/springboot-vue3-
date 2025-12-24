package com.example.fitness.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.fitness.entity.WorkoutLog;
import java.util.List;
import java.util.Map;

public interface IWorkoutLogService extends IService<WorkoutLog> {
    List<WorkoutLog> getByUsername(String username);
    Map<String, Object> getWorkoutStats(String username);
}

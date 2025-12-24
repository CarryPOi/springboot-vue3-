package com.example.fitness.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.fitness.entity.WorkoutLog;
import com.example.fitness.mapper.WorkoutLogMapper;
import com.example.fitness.service.IWorkoutLogService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WorkoutLogServiceImpl extends ServiceImpl<WorkoutLogMapper, WorkoutLog> implements IWorkoutLogService {

    @Override
    public List<WorkoutLog> getByUsername(String username) {
        return lambdaQuery()
                .eq(WorkoutLog::getUsername, username)
                .orderByDesc(WorkoutLog::getRecordDate)
                .list();
    }

    @Override
    public Map<String, Object> getWorkoutStats(String username) {
        List<WorkoutLog> logs = getByUsername(username);
        
        int totalWorkouts = logs.size();
        int totalDuration = logs.stream().mapToInt(WorkoutLog::getDuration).sum();
        int totalCalories = logs.stream().mapToInt(WorkoutLog::getCalories).sum();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalWorkouts", totalWorkouts);
        stats.put("totalDuration", totalDuration);
        stats.put("totalCalories", totalCalories);
        
        return stats;
    }
}

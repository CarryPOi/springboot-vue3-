package com.example.fitness.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.fitness.entity.DailyDiet;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IDailyDietService extends IService<DailyDiet> {
    List<DailyDiet> getListByUsernameAndDate(String username, LocalDate date);
    Map<String, Object> getDailyStats(String username, LocalDate date);
    List<Map<String, Object>> getHistoryStats(String username, int days);
}

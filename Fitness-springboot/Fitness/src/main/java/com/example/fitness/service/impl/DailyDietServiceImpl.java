package com.example.fitness.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.fitness.entity.DailyDiet;
import com.example.fitness.mapper.DailyDietMapper;
import com.example.fitness.service.IDailyDietService;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DailyDietServiceImpl extends ServiceImpl<DailyDietMapper, DailyDiet> implements IDailyDietService {

    @Override
    public List<DailyDiet> getListByUsernameAndDate(String username, LocalDate date) {
        LocalDate localDate = (date != null) ? date : LocalDate.now();
        return this.list(new LambdaQueryWrapper<DailyDiet>()
                .eq(DailyDiet::getUsername, username)
                .eq(DailyDiet::getRecordDate, localDate)
                .orderByDesc(DailyDiet::getCreateTime));
    }

    @Override
    public Map<String, Object> getDailyStats(String username, LocalDate date) {
        List<DailyDiet> diets = getListByUsernameAndDate(username, date);
        double totalCalories = 0;
        double totalProtein = 0;
        double totalFat = 0;
        double totalCarbs = 0;

        for (DailyDiet diet : diets) {
            totalCalories += diet.getCalories() != null ? diet.getCalories() : 0;
            totalProtein += diet.getProtein() != null ? diet.getProtein() : 0;
            totalFat += diet.getFat() != null ? diet.getFat() : 0;
            totalCarbs += diet.getCarbs() != null ? diet.getCarbs() : 0;
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCalories", Math.round(totalCalories * 100.0) / 100.0);
        stats.put("totalProtein", Math.round(totalProtein * 100.0) / 100.0);
        stats.put("totalFat", Math.round(totalFat * 100.0) / 100.0);
        stats.put("totalCarbs", Math.round(totalCarbs * 100.0) / 100.0);
        return stats;
    }

    @Override
    public List<Map<String, Object>> getHistoryStats(String username, int days) {
        LocalDate startDate = LocalDate.now().minusDays(days - 1);
        List<DailyDiet> diets = this.list(new LambdaQueryWrapper<DailyDiet>()
                .eq(DailyDiet::getUsername, username)
                .ge(DailyDiet::getRecordDate, startDate)
                .orderByDesc(DailyDiet::getRecordDate));

        Map<LocalDate, List<DailyDiet>> dailyGroups = diets.stream()
                .collect(Collectors.groupingBy(DailyDiet::getRecordDate));

        List<Map<String, Object>> history = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            LocalDate date = LocalDate.now().minusDays(i);
            List<DailyDiet> dayDiets = dailyGroups.getOrDefault(date, new ArrayList<>());
            
            double cal = 0, pro = 0, fat = 0, carb = 0;
            for (DailyDiet d : dayDiets) {
                cal += d.getCalories() != null ? d.getCalories() : 0;
                pro += d.getProtein() != null ? d.getProtein() : 0;
                fat += d.getFat() != null ? d.getFat() : 0;
                carb += d.getCarbs() != null ? d.getCarbs() : 0;
            }

            Map<String, Object> item = new HashMap<>();
            item.put("date", date.toString());
            item.put("calories", Math.round(cal * 10.0) / 10.0);
            item.put("protein", Math.round(pro * 10.0) / 10.0);
            item.put("fat", Math.round(fat * 10.0) / 10.0);
            item.put("carbs", Math.round(carb * 10.0) / 10.0);
            history.add(item);
        }
        return history;
    }
}

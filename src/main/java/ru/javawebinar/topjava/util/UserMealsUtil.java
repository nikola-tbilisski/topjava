package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        System.out.println("Unfiltered list");
        meals.forEach((System.out)::println);

        System.out.println("\nFiltered by Cycle");
        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(0, 0), LocalTime.of(23, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println("\nFiltered by Streams");
        filteredByStreams(meals, LocalTime.of(0, 0), LocalTime.of(23, 0), 2000)
                .forEach(System.out::println);
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesSumByDates = new HashMap<>();
        for (UserMeal um : meals) {
            caloriesSumByDates.merge(um.getDate(), um.getCalories(), Integer::sum);
        }

        List<UserMealWithExcess> umWithExcessList = new ArrayList<>();
        for (UserMeal um : meals) {
            if (TimeUtil.isBetweenHalfOpen(um.getDateTime().toLocalTime(), startTime, endTime)) {
                umWithExcessList.add(getUserMealWithExcess(um, caloriesSumByDates, caloriesPerDay));
            }
        }
        return umWithExcessList;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesSumByDates = meals.stream()
                .collect(Collectors.groupingBy(UserMeal::getDate, Collectors.summingInt(UserMeal::getCalories)));

        return meals.stream()
                .filter(um -> TimeUtil.isBetweenHalfOpen(um.getDateTime().toLocalTime(), startTime, endTime))
                .map(um -> getUserMealWithExcess(um, caloriesSumByDates, caloriesPerDay))
                .collect(Collectors.toList());
    }

    private static UserMealWithExcess getUserMealWithExcess(UserMeal um, Map<LocalDate, Integer> caloriesSumByDates, int caloriesPerDay) {
        return new UserMealWithExcess(um.getDateTime(),
                um.getDescription(), um.getCalories(),
                caloriesSumByDates.get(um.getDate()) > caloriesPerDay);
    }
}

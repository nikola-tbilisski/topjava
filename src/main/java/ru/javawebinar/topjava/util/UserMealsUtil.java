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
        List<UserMealWithExcess> excessList = new ArrayList<>();
        meals.sort(Comparator.comparing(UserMeal::getDateTime).reversed());
        Map<LocalDate, Integer> usMap = getLocalDateIntegerMap(meals);

        for (UserMeal mls : meals) {
            if (TimeUtil.isBetweenHalfOpen(mls.getDateTime().toLocalTime(), startTime, endTime)) {
                excessList.add(new UserMealWithExcess(mls.getDateTime(), mls.getDescription(), mls.getCalories(), usMap.get(mls.getDate()) > caloriesPerDay));
            }
        }

        return excessList;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> umMap = getLocalDateIntegerMap(meals);

        return meals.stream()
                .sorted(Comparator.comparing(UserMeal::getDateTime).reversed())
                .filter(el -> TimeUtil.isBetweenHalfOpen(el.getDateTime().toLocalTime(), startTime, endTime))
                .map(um -> new UserMealWithExcess(um.getDateTime(), um.getDescription(), um.getCalories(), umMap.get(um.getDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    private static Map<LocalDate, Integer> getLocalDateIntegerMap(List<UserMeal> meals) {
        return meals
                .stream()
                .collect(
                        Collectors.groupingBy(UserMeal::getDate, Collectors.summingInt(UserMeal::getCalories))
                );
    }
}

package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class MealsUtil {
    public static final List<Meal> MEAL_LIST = Arrays.asList(
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410));

    public static void main(String[] args) {
        filteredByCycles(MEAL_LIST, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000)
                .forEach(System.out::println);

        System.out.println();

        filteredByStreams(MEAL_LIST, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000)
                .forEach(System.out::println);
    }

    public static List<MealTo> getWithExceeded(Collection<Meal> mealList, int caloriesPerDay) {
        return filteredByStreams(mealList, LocalTime.MIN, LocalTime.MAX, caloriesPerDay);
    }

    public static List<MealTo> filteredByCycles(Collection<Meal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesSumByDates = new HashMap<>();
        for (Meal um : meals) {
            caloriesSumByDates.merge(um.getDate(), um.getCalories(), Integer::sum);
        }

        List<MealTo> umWithExcessList = new ArrayList<>();
        for (Meal um : meals) {
            if (TimeUtil.isBetweenHalfOpen(um.getDateTime().toLocalTime(), startTime, endTime)) {
                umWithExcessList.add(getUserMealWithExcess(um, caloriesSumByDates, caloriesPerDay));
            }
        }
        return umWithExcessList;
    }

    public static List<MealTo> filteredByStreams(Collection<Meal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesSumByDates = meals.stream()
                .collect(Collectors.groupingBy(Meal::getDate, Collectors.summingInt(Meal::getCalories)));

        return meals.stream()
                .filter(um -> TimeUtil.isBetweenHalfOpen(um.getDateTime().toLocalTime(), startTime, endTime))
                .map(um -> getUserMealWithExcess(um, caloriesSumByDates, caloriesPerDay))
                .collect(Collectors.toList());
    }

    private static MealTo getUserMealWithExcess(Meal um, Map<LocalDate, Integer> caloriesSumByDates, int caloriesPerDay) {
        return new MealTo(um.getDateTime(),
                um.getDescription(), um.getCalories(),
                caloriesSumByDates.get(um.getDate()) > caloriesPerDay);
    }
}

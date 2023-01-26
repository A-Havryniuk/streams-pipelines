package com.efimchick.ifmo;

import com.efimchick.ifmo.util.CourseResult;
import com.efimchick.ifmo.util.Person;

import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.*;

public class Collecting {
    protected static Map<Double, String> scoreAndMarks;
    public int sum(IntStream i)
    {
        return i.sum();
    }

    public int production(IntStream limit) {
        int[] allNumber = limit.toArray();
        int product = 1;
        for (int j : allNumber) {
            product *= j;
        }
        return product;
    }

    public int oddSum(IntStream limit) {
         return limit.filter(i -> i%2!=0).sum();
    }

    public  Map<Integer, Integer> sumByRemainder(int i, IntStream limit) {
        return limit.boxed()
                .collect(Collectors.groupingBy(s -> s%i, Collectors.summingInt(s -> s)));
    }

    public  Map<Person,Double> totalScores(Stream<CourseResult> programmingResults) {
        List<CourseResult> result = programmingResults.collect(Collectors.toList());
        return result.stream().collect(Collectors.toMap(CourseResult::getPerson, r -> r.getTaskResults().
                values().stream().mapToInt(v->v).sum()/(double)getCountTasks(result)));
    }


    private long getCountTasks (List<CourseResult> courseResults) {
        return courseResults.stream()
                .flatMap(r -> r.getTaskResults()
                        .keySet().stream())
                .distinct().count();
    }
    private long getCountPerson(List<CourseResult> result)
    {
        return result.stream().map(CourseResult::getPerson).distinct().count();
    }

    public double averageTotalScore(Stream<CourseResult> programmingResults) {
        List<CourseResult> result = programmingResults.collect(Collectors.toList());
        return totalScores(result.stream()).values().stream().reduce(0.0, Double::sum)/getCountPerson(result);
    }

    public Map<String, Double> averageScoresPerTask(Stream<CourseResult> programmingResults) {
        List<CourseResult> result = programmingResults.collect(Collectors.toList());
        return result.stream().flatMap(r->r.getTaskResults().
                entrySet().stream()).collect(Collectors.
                groupingBy(Map.Entry::getKey, Collectors.
                        summingDouble(value -> value.getValue()/(double)getCountPerson(result))));
    }

    public  Map<Person,String> defineMarks(Stream<CourseResult> programmingResults) {
        List<CourseResult> result = programmingResults.collect(Collectors.toList());
        return result.stream().collect(Collectors.toMap(CourseResult::getPerson, x -> getMark(getAvarageScore(x, result))));
    }
    private Double getAvarageScore(CourseResult course, List<CourseResult> list)
    {
        return course.getTaskResults().values().stream().
                mapToDouble(value -> value).sum()/getCountTasks(list);
    }
    private String getMark(Double mark)
    {
        if(mark>=90)
            return "A";
        if(mark>=83)
            return "B";
        if(mark>=75)
            return "C";
        if(mark>=68)
            return "D";
        if(mark>=60)
            return "E";
        else
            return "F";
    }

    public String easiestTask(Stream<CourseResult> programmingResults) {
        List<CourseResult> result = programmingResults.collect(Collectors.toList());
        Map<String, Double> map = averageScoresPerTask(result.stream());
        List<String> maxKeys = new ArrayList<>();
        double max = Double.MIN_VALUE;
        for(Map.Entry<String, Double> entry : map.entrySet())
        {
            if(entry.getValue() > max)
            {
                max = entry.getValue();
                maxKeys.clear();
            }
            if(entry.getValue() == max)
            {
                maxKeys.add(entry.getKey());
            }
        }
        return maxKeys.get(0);
    }

    public Collector printableStringCollector() {
        return new Collector() {
            @Override
            public Supplier supplier() {
                return FuckingPrint::new;
            }

            @Override
            public BiConsumer<FuckingPrint, CourseResult> accumulator() {
                return FuckingPrint::addCourseResult;
            }

            @Override
            public BinaryOperator combiner() {
                return null;
            }

            @Override
            public Function<FuckingPrint, String> finisher() {
                return specialPrint -> {
                    StringBuilder builder = new StringBuilder();
                    specialPrint.buildResult(builder);
                    return builder.toString();
                };
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Collections.emptySet();
            }
        };
    }

}

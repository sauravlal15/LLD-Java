package com.saurav.lld.dsapractice;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Entry point for dsa-practice. Add domain types in this package (or
 * subpackages), not in the default package.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("dsa-practice ready.");
        // Merge two unsorted arrays into a single sorted array without duplicates
        List<Integer> input1 = List.of(1, 4, 3, 4);
        List<Integer> input2 = List.of(3, 2, 4, 1);

        List<Integer> res = Stream.concat(input1.stream(), input2.stream()).sorted().distinct().collect(Collectors.toList());

        System.out.println("res: " + res);
    }
}

// Find the maximum and minimum of a list of integers
/*
List<Integer> input = List.of(1, 2, 3, 4);
int[] res = {Integer.MIN_VALUE, Integer.MAX_VALUE};
input.stream().forEach(no -> {
    res[0] = Math.max(res[0], no);
    res[1] = Math.min(res[1], no);
});

System.out.println("max, min: " + res[0] + ", " + res[1]);
 */
// Print the numbers from a given list of integers that are multiples of 5
/*
List<Integer> input = new ArrayList<>(List.of(10, 15, 12, 20));
List<Integer> res = input.stream().filter(no -> no % 5 == 0).collect(Collectors.toList());

System.out.println("res: " + res);
 */
// Sort a given list of decimals in reverse order
/*
List<Double> input = new ArrayList<>(List.of(1.0, 2.3, 4.2, 3.12));
// input.sort(Comparator.reverseOrder());

List<Double> res = input.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());

System.out.println("res: " + res);
 */
// Find the frequency of each element in an array or a list
/*
List<Integer> input = new ArrayList(List.of(1, 2, 3, 4, 3, 2, 1));
Map<Integer, Integer> count = new HashMap<>();

input.stream().forEach(no -> count.put(no, count.getOrDefault(no, 0) + 1));

System.out.println("res: " + count);
 */
// Find the frequency of each character in a string using Java 8 streams
/*
String res = "abcdabc";
Map<String, Integer> count = new HashMap<>();
Arrays.stream(res.split("")).forEach(ch
        -> count.put(ch, count.getOrDefault(ch, 0) + 1));

System.out.println("count: " + count);
 */
// Remove duplicate elements from a list using Java 8 streams
/*
List<Integer> input = new ArrayList<>(List.of(1, 2, 4, 5, 3, 4));
Map<Integer, Integer> count = new HashMap<>();

List<Integer> res = input.stream().filter(no -> {
    count.put(no, count.getOrDefault(no, 0) + 1);
    return count.get(no) == 1;
}).collect(Collectors.toList());

System.out.println("res: " + res);
 */
// Separate odd and even numbers in a list of integers
/*
List<Integer> input = new ArrayList<>(List.of(1, 2, 3, 4));
List<Integer> even = new ArrayList<>(), odd = new ArrayList<>();
for (int i = 0; i < input.size(); i++) {
    if (input.get(i) % 2 == 0) {
        even.add(input.get(i));
    } else {
        odd.add(input.get(i));
    }
}
System.out.println("even: " + even);
System.out.println("odd: " + odd);

 */

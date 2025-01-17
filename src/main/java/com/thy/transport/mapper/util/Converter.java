package com.thy.transport.mapper.util;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Converter {
    public static String mapListToString(SortedSet<Integer> integers) {
        if (integers == null) {
            return "";
        }
        return integers.stream()
                .map(String::valueOf)  // Convert each Integer to String
                .collect(Collectors.joining(","));  // Join them with a comma
    }

    public static SortedSet<Integer> mapStringToList(String str) {
        if (str == null || str.isEmpty()) {
            return new TreeSet<>();  // Return an empty list if the string is null or empty
        }
        return new TreeSet<>(List.of(str.split(",")).stream()  // Split the string by commas
                .map(Integer::parseInt)  // Convert each string to an Integer
                .collect(Collectors.toList()));
    }
}

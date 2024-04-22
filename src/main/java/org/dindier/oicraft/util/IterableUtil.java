package org.dindier.oicraft.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class IterableUtil {
    public static <S, T> Iterable<T> map(Iterable<S> iterable, Function<S, T> function) {

        if (iterable instanceof List) {
            // Optimization for List
            return ((List<S>) iterable).stream().map(function).toList();
        }

        List<T> list = new ArrayList<>();
        for (S s : iterable) {
            list.add(function.apply(s));
        }
        return list;
    }
}

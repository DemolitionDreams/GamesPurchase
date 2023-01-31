package com.gamespurchase.utilities;

import java.util.Comparator;
import java.util.function.Function;

public class CompareUtility {

    public  enum Nulls {FIRST, LAST}

    public  enum Order {ASCENDING, DESCENDING}

    /**
     * Return a Comparator of T on a function f(T) -> R
     */
    public static <T, R extends Comparable<? super R>> Comparator<T> comparatorOf(
            Function<T, R> function,
            Order order,
            Nulls nulls) {

        Comparator<R> rComparator = Comparator.naturalOrder();

        if (order == Order.DESCENDING) {
            rComparator = rComparator.reversed();
        }
        rComparator = (nulls == Nulls.FIRST) ?
                Comparator.nullsFirst(rComparator) :
                Comparator.nullsLast(rComparator);

        Comparator<T> tComparator =
                Comparator.comparing(
                        function, rComparator);

        tComparator = (nulls == Nulls.FIRST) ?
                Comparator.nullsFirst(tComparator) :
                Comparator.nullsLast(tComparator);

        return tComparator;
    }
}
package jmt.common.functional;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * Created by raffaele on 12/18/16.
 */
public class Functional {
    public static <S, K, T extends Iterable<K>> S reduce(ReduceCallable<K, S> function, S accumulator, T iterable) {
        int i = 0;
        for (K value : iterable) {
            accumulator = function.callable(value, accumulator, i++);
        }
        return accumulator;
    }

    public static <K, T extends Collection<K>> T map(final MapCallable<K> function, T collection) {
        try {
            return reduce(new ReduceCallable<K, T>() {
                @Override
                public T callable(K value, T accumulator, int pos) {
                    accumulator.add(function.callable(value, pos));
                    return accumulator;
                }
            }, (T) collection.getClass().getConstructor().newInstance(), collection);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <K, T extends Collection<K>> T filter(final FilterCallable<K> function, T collection) {
        try {
            return reduce(new ReduceCallable<K, T>() {
                @Override
                public T callable(K value, T accumulator, int pos) {
                    if (function.callable(value, pos)) {
                        accumulator.add(value);
                    }
                    return accumulator;
                }
            }, (T) collection.getClass().getConstructor().newInstance(), collection);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}

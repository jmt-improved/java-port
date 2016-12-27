package jmt.common.functional;

/**
 * Created by raffaele on 12/18/16.
 */
public interface ReduceCallable<E, T> {
    T callable(E value, T accumulator, int pos);
}
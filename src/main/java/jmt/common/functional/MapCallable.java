package jmt.common.functional;

/**
 * Created by raffaele on 12/18/16.
 */
public interface MapCallable<E> {
    E callable(E value, int pos);
}
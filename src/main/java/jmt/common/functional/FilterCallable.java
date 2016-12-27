package jmt.common.functional;

/**
 * Created by raffaele on 12/18/16.
 */
public interface FilterCallable<E> {
    boolean callable(E value, int pos);
}
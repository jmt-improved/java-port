package jmt.common.functional;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by raffaele on 12/27/16.
 */
public class DynamicHash {
    class DynamicField<T> {
        final T value;

        DynamicField(T v) {
            this.value = v;
        }
    }

    private Map<String, DynamicField<?>> fields;

    public DynamicHash() {
        fields = new HashMap<>();
    }

    public <T> void put(String key, T value) {
        fields.put(key, new DynamicField<>(value));
    }


    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        DynamicField<T> f = (DynamicField<T>) fields.get(key);

        return f.value;
    }

}
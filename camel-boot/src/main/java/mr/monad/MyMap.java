package mr.monad;

import java.util.HashMap;

/**
 * Created by anatol on 11/12/14.
 */
public class MyMap<T, U> extends HashMap<T, U> {
    public Try<U> find(T key) {
        U value = super.get(key);
        if (value == null) {
            return Try.failure("Key " + key + " not found in map");
        }
        else {
            return Try.success(value);
        }
    }
}
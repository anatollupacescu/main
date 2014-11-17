package mr.monad;

import java.util.function.Consumer;

/**
 * Created by anatol on 11/12/14.
 */
public abstract class Try<V> {

    private Try() {
    }

    public abstract Boolean isSuccess();

    public abstract Boolean isFailure();

    public abstract void throwException();

    public abstract V successValue();

    public abstract RuntimeException failureValue();

    public static <V> Try<V> failure(String message) {
        return new Failure<V>(message);
    }

    public static <V> Try<V> failure(String message, Exception e) {
        return new Failure<V>(message, e);
    }

    public static <V> Try<V> failure(Exception e) {
        return new Failure<V>(e);
    }

    public static <V> Try<V> success(V value) {
        return new Success<V>(value);
    }

    public void ifPresent(Consumer<V> c) {
        if (isSuccess()) {
            c.accept(successValue());
        }
    }

    public void ifPresentOrThrow(Consumer<V> c) {
        if (isSuccess()) {
            c.accept(successValue());
        } else {
            throw ((Failure<V>) this).exception;
        }
    }

    public Try<RuntimeException> ifPresentOrFail(Consumer<V> c) {
        if (isSuccess()) {
            c.accept(successValue());
            return failure("Failed to fail!");
        } else {
            return success(failureValue());
        }
    }

    private static class Failure<V> extends Try<V> {

        private RuntimeException exception;

        public Failure(String message) {
            super();
            this.exception = new IllegalStateException(message);
        }

        public Failure(String message, Exception e) {
            super();
            this.exception = new IllegalStateException(message, e);
        }

        public Failure(Exception e) {
            super();
            this.exception = new IllegalStateException(e);
        }

        @Override
        public Boolean isSuccess() {
            return false;
        }

        @Override
        public Boolean isFailure() {
            return true;
        }

        @Override
        public void throwException() {
            throw this.exception;
        }

        @Override
        public V successValue() {
            throw new IllegalStateException("Should not call successValue on Failure object");
        }

        @Override
        public RuntimeException failureValue() {
            return exception;
        }

    }

    private static class Success<V> extends Try<V> {

        private V value;

        public Success(V value) {
            super();
            this.value = value;
        }

        @Override
        public Boolean isSuccess() {
            return true;
        }

        @Override
        public Boolean isFailure() {
            return false;
        }

        @Override
        public void throwException() {
            //log.error("Method throwException() called on a Success instance");
        }

        @Override
        public V successValue() {
            return value;
        }

        @Override
        public RuntimeException failureValue() {
            throw new IllegalStateException("Should not be called on a Success class");
        }
    }


    // TODO various method such as map an flatMap
}
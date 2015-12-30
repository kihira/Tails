package kihira.tails.client.animation;

import java.util.Objects;

public class Parameter<T extends Comparable<T>> {
    protected T value;

    public Parameter(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public boolean checkValid(T input) {
        return input == value;
    }

    public enum Comparison {
        EQUALS,
        LESSTHAN,
        GREATERTHEN
    }

    public static class ComparsionParameter<T extends Comparable<T>> extends Parameter<T> {
        private final Comparison comparison;

        public ComparsionParameter(T value, Comparison comparison) {
            super(value);
            this.comparison = comparison;
        }

        @Override
        public boolean checkValid(T input) {
            switch (comparison) {
                case EQUALS:
                    return Objects.equals(input, value);
                case LESSTHAN:
                    return value.compareTo(input) < 0;
                case GREATERTHEN:
                    return value.compareTo(input) > 0;
            }
            return false;
        }
    }
}

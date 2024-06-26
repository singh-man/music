package com.music;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

/**
 * Example: how to extend a Function<T, R> and use it.
 */
public class FunctionTest {

    private interface MyAdder<T, R> extends Function<T, R> {

        @Override
        default R apply(T t) {
            return operate(t);
        }

        R operate(T t);
    }

    @Test
    public void myTest() {
        MyAdder<Integer, Integer> add10 = integer -> integer + 10;
        MyAdder<Integer, Integer> multiply10 = t -> t*10;

        // direct use of function
        Assertions.assertEquals(20, add10.operate(10));

        // Function -> chaining and applying <- still usees base function!!
        Assertions.assertEquals(200, add10
                .andThen(multiply10)
                .apply(10));
    }
}

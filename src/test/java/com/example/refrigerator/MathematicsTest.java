package com.example.refrigerator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

public class MathematicsTest {

    @Test
    public void ok() {
        assertThat(Mathematics.sqrt(9), is(3d));
    }

    @Test
    public void zero() {
        assertThat(Mathematics.sqrt(0), is(0d));
    }
}

package com.example;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class ResponseTimeInterceptorTest 
{
    private ResponseTimeInterceptor interceptor;

    @Before
    public void setup() {
        interceptor = new ResponseTimeInterceptor();
    }

    @Test
    public void testAverageResponseTime() {
        interceptor.clear();
        interceptor.responseTimes.addAll(Arrays.asList(100L, 200L, 300L));

        double avg = interceptor.getAverageResponseTime();
        assertEquals(200.0, avg, 0.01);
    }

    @Test
    public void testEmptyResponseTimes() {
        interceptor.clear();
        assertEquals(0.0, interceptor.getAverageResponseTime(), 0.0);
    }
}

package org.jboss.as.quickstarts.helloworld;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class HelloServiceTest {

    private HelloService service;

    @Before
    public void setup() {

        service = new HelloService();
    }

    @Test
    public void createHelloMessage() {

        String result = service.createHelloMessage("World");
        String expected = "Hello World!";
        assertEquals(expected, result);
    }
}
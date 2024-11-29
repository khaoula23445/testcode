package org.example;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Example of a valid test.
     * 
     * This test case could be meaningful by checking actual functionality.
     */
    public void testApp()
    {
        // Example assertion for a meaningful test
        int expected = 5;
        int actual = 2 + 3;
        assertEquals("Sum should be 5", expected, actual);
    }
}

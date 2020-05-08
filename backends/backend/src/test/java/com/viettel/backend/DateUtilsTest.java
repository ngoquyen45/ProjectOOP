package com.viettel.backend;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class DateUtilsTest extends TestCase {
    
    /**
     * Create the test case
     *
     * @param testName
     *            name of the test case
     */
    public DateUtilsTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(DateUtilsTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testDateUtil() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        Date date = new Date(1426071133964l);
        System.out.println(sdf.format(date));
        System.out.println(date.getTime());
    }

}

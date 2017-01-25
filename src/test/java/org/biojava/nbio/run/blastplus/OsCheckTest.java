/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.biojava.nbio.run.blastplus;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author pavanpa
 */
public class OsCheckTest {
    
    public OsCheckTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getOperatingSystemType method, of class OsCheck.
     */
    @Test
    public void testGetOperatingSystemType() {
        System.out.println("getOperatingSystemType");
        OsCheck.OSType expResult = null;
        OsCheck.OSType result = OsCheck.getOperatingSystemType();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getArchDataModel method, of class OsCheck.
     */
    @Test
    public void testGetArchDataModel() {
        System.out.println("getArchDataModel");
        String expResult = "";
        String result = OsCheck.getArchDataModel();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}

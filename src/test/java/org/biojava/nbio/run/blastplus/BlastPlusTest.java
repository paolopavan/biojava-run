/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.biojava.nbio.run.blastplus;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import org.biojava.nbio.core.search.io.Result;
import org.biojava.nbio.core.sequence.template.Sequence;
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
public class BlastPlusTest {
    
    public BlastPlusTest() {
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
     * Test of run method, of class BlastPlus.
     */
    @Test
    public void testRun() throws Exception {
        System.out.println("run");
        
        // test on a big database
        String databaseResource = "/org/biojava/nbio/run/blastplus/16SMicrobial.fasta";
        String queriesResource = "/org/biojava/nbio/run/blastplus/queries.fasta";
        
        BlastPlus searchEngine = new BlastPlus();
        searchEngine.setProgram(BlastPlus.Program.BLASTN);
        
        File queryFile,databaseFile;
        databaseFile = new File(this.getClass().getResource(databaseResource).getFile());
        queryFile = new File(this.getClass().getResource(queriesResource).getFile());
        
        searchEngine.setDatabase(databaseFile);
        searchEngine.setQuery(queryFile);
        
        searchEngine.run();
        
        // cannot verify all the results but at least 
        // they must be exactly as the query sent:
        int counter=0;
        Iterator<Result> iterator = searchEngine.iterator();
        while (iterator.hasNext()) {
            counter++;
            iterator.next();
        }
        assertEquals(counter, 71);
        
    }

    /**
     * Test of setDatabase method, of class BlastPlus.
     */
    @Test
    public void testSetDatabase_String_String() {
        System.out.println("setDatabase");
        String path = "";
        String baseName = "";
        BlastPlus instance = new BlastPlus();
        instance.setDatabase(path, baseName);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setDatabase method, of class BlastPlus.
     */
    @Test
    public void testSetDatabase_File() {
        System.out.println("setDatabase");
        File f = null;
        BlastPlus instance = new BlastPlus();
        instance.setDatabase(f);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setDatabase method, of class BlastPlus.
     */
    @Test
    public void testSetDatabase_List() throws Exception {
        System.out.println("setDatabase");
        List sequences = null;
        BlastPlus instance = new BlastPlus();
        instance.setDatabase(sequences);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setDatabase method, of class BlastPlus.
     */
    @Test
    public void testSetDatabase_SequenceArr() throws Exception {
        System.out.println("setDatabase");
        Sequence[] sequences = null;
        BlastPlus instance = new BlastPlus();
        instance.setDatabase(sequences);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setQuery method, of class BlastPlus.
     */
    @Test
    public void testSetQuery_List() throws Exception {
        System.out.println("setQuery");
        List sequences = null;
        BlastPlus instance = new BlastPlus();
        instance.setQuery(sequences);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setQuery method, of class BlastPlus.
     */
    @Test
    public void testSetQuery_SequenceArr() throws Exception {
        System.out.println("setQuery");
        Sequence[] sequences = null;
        BlastPlus instance = new BlastPlus();
        instance.setQuery(sequences);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setTask method, of class BlastPlus.
     */
    @Test
    public void testSetTask() {
        System.out.println("setTask");
        BlastPlus.Task t = null;
        BlastPlus instance = new BlastPlus();
        instance.setTask(t);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setProgram method, of class BlastPlus.
     */
    @Test
    public void testSetProgram() {
        System.out.println("setProgram");
        BlastPlus.Program p = null;
        BlastPlus instance = new BlastPlus();
        instance.setProgram(p);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setNumThreads method, of class BlastPlus.
     */
    @Test
    public void testSetNumThreads() {
        System.out.println("setNumThreads");
        int n = 0;
        BlastPlus instance = new BlastPlus();
        instance.setNumThreads(n);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setNumAlignments method, of class BlastPlus.
     */
    @Test
    public void testSetNumAlignments() {
        System.out.println("setNumAlignments");
        int n = 0;
        BlastPlus instance = new BlastPlus();
        instance.setNumAlignments(n);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of passProgramOption method, of class BlastPlus.
     */
    @Test
    public void testPassProgramOption() {
        System.out.println("passProgramOption");
        String option = "";
        String value = "";
        BlastPlus instance = new BlastPlus();
        instance.passProgramOption(option, value);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getVersion method, of class BlastPlus.
     */
    @Test
    public void testGetVersion() throws Exception {
        System.out.println("getVersion");
        BlastPlus instance = new BlastPlus();
        String expResult, result;
        
        instance.setProgram(BlastPlus.Program.BLASTN);
        expResult = "blastn: 2.2.29+";
        result = instance.getVersion();
        assertEquals(expResult, result);
        
        instance.setProgram(BlastPlus.Program.BLASTP);
        expResult = "blastp: 2.2.29+";
        result = instance.getVersion();
        assertEquals(expResult, result);
        
        instance.setProgram(BlastPlus.Program.BLASTX);
        expResult = "blastx: 2.2.29+";
        result = instance.getVersion();
        assertEquals(expResult, result);
    }

    /**
     * Test of iterator method, of class BlastPlus.
     */
    @Test
    public void testIterator() {
        System.out.println("iterator");
        BlastPlus instance = new BlastPlus();
        Iterator<Result> expResult = null;
        Iterator<Result> result = instance.iterator();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}

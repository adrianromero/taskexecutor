/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adr.taskexecutor.jython;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adrian
 */
public class InitialTests {

    public InitialTests() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // @Test
    // public void hello() {}



     @Test
     public void test1() throws Exception {

//         ClassLoader cl = Thread.currentThread().getContextClassLoader();
//         Enumeration<URL> e = cl.getSystemResources("pepe.txt");

//         try {
//            // create a script engine manager
//            ScriptEngineManager factory = new ScriptEngineManager();
//            // create a JavaScript engine
//            ScriptEngine engine = factory.getEngineByName("jython");
//            // evaluate JavaScript code from String
//            //engine.eval("import sys");
//            //engine.eval("print sys");
//            System.out.println(engine.eval("obj = 2 + 2"));
//            Object obj = engine.get("obj");
//            System.out.println(obj);
//
//            System.out.println(
//                    engine.eval("def multiply_nums(x, y): \n" +
//                        "    return x * y \n" +
//                        "a = multiply_nums(25, 7) \n" +
//                        "print a \n" +
//                        "multiply_nums(3, 1) \n"));


int i;
for (i = 0; i< 7; i++) {
    System.out.println(i);
}
      System.out.println(i);

//
//        } catch (ScriptException ex) {
//            fail(ex.getMessage());
//        }
     }

}
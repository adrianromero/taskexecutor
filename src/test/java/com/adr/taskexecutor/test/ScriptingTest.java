/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adr.taskexecutor.test;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.Invocable;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;


/**
 *
 * @author adrian
 */
public class ScriptingTest {

    public ScriptingTest() {
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

     //@Test
     public void test1() throws ScriptException {
         
            // create a script engine manager
            ScriptEngineManager factory = new ScriptEngineManager();
            // create a JavaScript engine
            ScriptEngine engine = factory.getEngineByName("JavaScript");
            // evaluate JavaScript code from String
            engine.eval("var obj = {}; obj.pepe = '100'; obj.dos=2; ");
            Object obj = engine.get("obj");



            ScriptEngine engine2 = factory.getEngineByName("JavaScript");
            engine2.put("obj", obj);
            engine2.eval("obj.getValue = function(p) { return obj[p];};");
            Object obj2 = engine2.get("obj");


            Invocable inv = (Invocable) engine2;

            engine.eval("println(typeof(null));");
            engine.eval("println(typeof(undefined));");

            engine.eval("println(typeof(\"pepeluis\"));");
            engine.eval("println(typeof(new Date()));");
            engine.eval("println(typeof(123));");
            engine.eval("println(typeof(-123.44));");
            engine.eval("println(typeof([3,3]));");
            engine.eval("println(typeof({a:1}));");
            engine.eval("println(typeof(true));");

            // engine.eval("println(null.constructor);");
            // engine.eval("println(undefined.constructor);");
            
            engine.eval("println(\"pepeluis\".constructor === String);");
            engine.eval("println(new Date().constructor === Date);");
            engine.eval("println((123).constructor === Number);");
            engine.eval("println((-342.44).constructor === Number);");
            engine.eval("println([3,3].constructor === Array);");
            engine.eval("println({a:3}.constructor === Object);");
            engine.eval("println(true.constructor === Boolean);");

//            try {
//                System.out.println(inv.invokeMethod(obj2, "getValue", "pepe"));
//                System.out.println(inv.invokeMethod(obj2, "getValue", "dos"));
//            } catch (NoSuchMethodException ex) {
//                Assert.fail()
//            }
     }

    @Test
    public void test2() throws ScriptException {
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("JavaScript");
        engine.eval("var obj = {}; obj.pepe = '100'; obj.dos=2; ");

        engine.eval("for (var prop in this) { println(prop);}");
        engine.eval("eval(\"println(\\\"esto es siiiiii\\\"); var jj = 33;\");");
        engine.eval("println(jj);");
    }
}
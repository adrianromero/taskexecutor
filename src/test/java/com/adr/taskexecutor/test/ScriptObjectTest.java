/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adr.taskexecutor.test;


import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

//import javax.script.ScriptEngine;
//import javax.script.ScriptEngineManager;
//import javax.script.ScriptException;


/**
 *
 * @author adrian
 */
public class ScriptObjectTest {

    public ScriptObjectTest() {
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

     @Test
     public void test() {

         // THIS WORKS !!!!

         try {
            // create a script engine manager
            ScriptEngineManager factory = new ScriptEngineManager();
            // create a JavaScript engine
            ScriptEngine engine = factory.getEngineByName("JavaScript");


            engine.put("pepe", new ScriptObject());


            // engine.eval("pepe.callme([1,2,2]); ");
            engine.eval("pepe.callme({a : 1, b : 123, c : {a : 4, b : 7}}); ");
            engine.eval("pepe.callMethod(1);");
            engine.eval("pepe.callMethod(1.33);");
            engine.eval("pepe.callMethod(new Date());");
            engine.eval("pepe.callMethodDate(new Date());");
            engine.eval("pepe.callMethod(\"pepe\");");
            engine.eval("pepe.callMethod(null);");
            engine.eval("pepe.callMethod(undefined);");
            engine.eval("pepe.callMethod({uno:12,dos:22});");

            engine.eval("pepe.callMethodArray([1, 2, 3]);");
            engine.eval("pepe.callMethodArrayString([\"uno\", \"dos\", \"tres\"]);");

            engine.eval("pepe.callMethodChar(\"c\");");

            engine.eval("pepe.property = 332;");
            engine.eval("print(pepe.property);");

            engine.eval("pepe.callMethodString(2);");
            engine.eval("pepe.callMethodString(new Date());");
            engine.eval("pepe.callMethodString(\"qdqdqw\");");
            engine.eval("pepe.callMethodString(null);");
            engine.eval("pepe.callMethodString(undefined);");
            engine.eval("pepe.callMethodString({uno:12,dos:22});");
            engine.eval("pepe.callMethodString([1, 2, 3]);");

            engine.eval("pepe.callMethodOverloaded(1);");
            engine.eval("pepe.callMethodOverloaded(1, 2);");
            engine.eval("pepe.callMethodOverloaded(1, 2, 3);");

            Object result = engine.eval("(function(){ return [\"1\", \"2\", \"3\"];})();");
            System.out.println(result.toString());
            result = engine.eval("(function(){ return \"qwdqdqwd\";})();");
            System.out.println(result.toString());
            result = engine.eval("(function(){ return 1232.33;})();");
            System.out.println(result.toString());

            engine.eval("println(pepe.getBoolean().constructor);");

        } catch (ScriptException ex) {
            fail(ex.getMessage());
        }
     }

}
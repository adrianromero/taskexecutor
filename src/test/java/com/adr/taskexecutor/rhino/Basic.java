/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adr.taskexecutor.rhino;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 *
 * @author adrian
 */
public class Basic {

    public Basic() {
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

    // The methods must be annotated with annotation @Test. For example:
    //
     @Test
     public void hello() {
        Scriptable scope; // this is THE OBJECT


        Context cx;

        cx = Context.enter();
        try {
            scope = cx.initStandardObjects();

            ScriptableObject.putProperty(scope, "out", Context.javaToJS(System.out, scope));
            cx.evaluateString(scope, "function println(args) {out.println(args);}", "<cmd>", 1, null);

        } finally {
            Context.exit();
        }

        cx = Context.enter();
        try {
            cx.evaluateString(scope, "println('funciona');", "<cmd>", 1, null);
            Object result = cx.evaluateString(scope, "java.lang.System.out.println('pepeino'); out.println(\"chocholoco\"); 5+5;", "<cmd>", 1, null);
            System.out.println(Context.toString(result));
        } finally {
            Context.exit();
        }


        cx = Context.enter();
        try {
            Object result = cx.evaluateString(scope, "(function() { return {name: 100, id:\"1212\"};})();", "<cmd>", 1, null);
            System.out.println(Context.toString(result));
        } finally {
            Context.exit();
        }

        cx = Context.enter();
        try {
            Object result = cx.evaluateString(scope, "eval('5+7');", "<cmd>", 1, null);
            System.out.println(Context.toString(result));
        } finally {
            Context.exit();
        }
        
        
        cx = Context.enter();
        try {
            Object result = cx.evaluateString(scope, "isFunction(234);", "<cmd>", 1, null);
            System.out.println(Context.toString(result));
        } finally {
            Context.exit();
        }        

    }

}
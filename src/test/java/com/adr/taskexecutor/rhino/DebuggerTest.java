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
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.debugger.Main;
import org.mozilla.javascript.tools.shell.ShellContextFactory;

/**
 *
 * @author adrian
 */
public class DebuggerTest {

    public DebuggerTest() {
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



    public static void main(String[] args) {
        DebuggerTest t = new DebuggerTest();
        t.debug();
    }

    // The methods must be annotated with annotation @Test. For example:
    //
     @Test
     public void debug() {

//       ContextFactory contextFactory = getDebugContextFactory();
        ContextFactory contextFactory = getNormalContextFactory();
       Scriptable scope; // this is THE OBJECT
       Context cx;
       


        cx = contextFactory.enterContext();
        try {
            scope = cx.initStandardObjects();
        } finally {
            Context.exit();
        }

       displayDebugger2(contextFactory, scope);

        cx = contextFactory.enterContext();
        try {
            Object result = cx.evaluateString(scope, "2 + 4 + 5", "<cmd>", 1, null);
             result = cx.evaluateString(scope, "2 + 55 + 5", "<cmd2>", 1, null);
            result = cx.evaluateString(scope, "1/3", "<cmd3>", 1, null);
            result = cx.evaluateString(scope, "32323.333+3232", "<cmd4>", 1, null);
            result = cx.evaluateString(scope, "'cocoloco'", "<cmd5>", 1, null);
            System.out.println(Context.toString(result));
        } finally {
            Context.exit();
        }
     }

     private void displayDebugger2(final ContextFactory fac, final Scriptable scope) {


        Main.mainEmbedded(fac, scope, "my JS debugger");
    }

    private void displayDebugger(final ContextFactory fac, final Scriptable scope) {

//        // Set the look and feel.
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//            // UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
//        } catch (Exception e) {
//        }

        // This code is mostly copied from
        // org.mozilla.javascript.tools.debugger.Main#mainEmbeddedImpl. The
        // difference is that we do not call main.doBreak(), which would cause
        // the debugger to stop on the first line of code that it executes.
        Main main = new Main("JS Debugger");

        main.setExitAction(new Runnable() {
            @Override
            public void run() {
                System.out.println("nos vamos");
                System.exit(0);
            }
        });

        main.attachTo(fac);
        main.setScope(scope);

//        main.setScopeProvider(new ScopeProvider() {
//            @Override
//            public Scriptable getScope() {
//                throw new UnsupportedOperationException();
//            }
//        });

        main.doBreak();


        main.pack();
        main.setSize(600, 460);
        main.setVisible(true);
    }



    private ContextFactory getDebugContextFactory() {
        return ContextFactory.getGlobal(); // works!!!!!!!!!!
//        return new ContextFactory(); // works
//        return new ShellContextFactory(); // works
//        return org.mozilla.javascript.tools.shell.Main.shellContextFactory; // works
    }

    private ContextFactory getNormalContextFactory() {
        ContextFactory f = Context.enter().getFactory(); /// works this way....
        Context.exit();
        return f;
    }

}
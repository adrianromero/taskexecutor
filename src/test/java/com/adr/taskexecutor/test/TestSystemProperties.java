/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adr.taskexecutor.test;

import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author adrian
 */
public class TestSystemProperties {

    public TestSystemProperties() {
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
     public void hello() {

         System.getProperties().list(System.out);
     }

     @Test
     public void map() {
        Map<String, String> map = new HashMap<String, String>();

        map.put(null, "uno");
        map.put("uno", "dos");
        map.put("", "tres");
        System.out.println(map.get(null));
        System.out.println(map.get("uno"));
        System.out.println(map.get(""));
     }


     @Test
     public void file() throws IOException {

        System.out.println(new File(new File("/home/adrian"), "pepeluis.txt").getCanonicalPath());
        System.out.println(new File(new File("/home/adrian"), "/pepeluis.txt").getCanonicalPath());
        System.out.println(new File(new File("/home/adrian"), "../pepeluis.txt").getCanonicalPath());
        System.out.println(new File(new File("/home/adrian"), "./pepeluis.txt").getCanonicalPath());
     }

     @Test
     public void about() throws IOException {

        System.out.println("Java:     " + System.getProperty("java.runtime.name") + " " + System.getProperty("java.runtime.version") + "; " + System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version"));
        System.out.println("System:   " + System.getProperty("os.name") + " " + System.getProperty("os.version") + " runing on " + System.getProperty("os.arch") + "; " +  System.getProperty("file.encoding") + "; " +  System.getProperty("user.language") + "_" + System.getProperty("user.country"));
        System.out.println("User dir: " + System.getProperty("user.dir"));


        System.out.println(        RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB.toString());

        Map desktophints = (Map) Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");
        System.out.println(desktophints.get(RenderingHints.KEY_TEXT_ANTIALIASING).toString());
     }

}
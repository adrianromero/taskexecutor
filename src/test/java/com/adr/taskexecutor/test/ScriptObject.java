/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adr.taskexecutor.test;

import java.util.Date;

/**
 *
 * @author adrian
 */
public class ScriptObject {

    public void callme(Object pepe) {

        System.out.println(pepe);
    
    }

    public void callMethodArrayString(String[] o) {
        try {
            if (o == null) {
                System.out.println("callMethodArrayString null");
            } else {
                System.out.println("callMethodArrayString " + o.getClass().getName() + " -> " + o.toString());
                for (Object e : o) {
                    System.out.print(", " + e.toString());
                }
                System.out.println();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void callMethodArray(Object[] o) {
        try {
            if (o == null) {
                System.out.println("callMethodArray null");
            } else {
                System.out.println("callMethodArray " + o.getClass().getName() + " -> " + o.toString());
                for (Object e : o) {
                    System.out.print(", " + e.toString());
                }
                System.out.println();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void callMethodChar(char c) {
        try {
            System.out.println("callMethodChar " + c);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void callMethod(Object o) {
        try {
            if (o == null) {
                System.out.println("callMethod null");
            } else {
                System.out.println("callMethod " + o.getClass().getName() + " -> " + o.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void callMethodDate(Date o) {
        try {
            if (o == null) {
                System.out.println("callMethodDate null");
            } else {
                System.out.println("callMethodDate " + o.getClass().getName() + " -> " + o.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void callMethodString(String o) {
        try {
            if (o == null) {
                System.out.println("callMethodString null");
            } else {
                System.out.println("callMethodString -> " + o.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void callMethodOverloaded(int i) {
        System.out.println("callMethodOverloaded(int) " + i);
    }

    public void callMethodOverloaded(int i, int j) {
        System.out.println("callMethodOverloaded(int, int) " + i + " " + j);
    }

    public void callMethodOverloaded(int i, int j, int k) {
        System.out.println("callMethodOverloaded(int, int, int) " + i + " " + j + " " + k);
    }

    public void setProperty(Object o) {
        try {
            if (o == null) {
                System.out.println("setProperty null");
            } else {
                System.out.println("setProperty " + o.getClass().getName() + " -> " + o.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public Object getProperty() {
        return "get";
    }

    public boolean getBoolean() {
        return true;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.safester.application.test;

import org.apache.commons.lang3.SystemUtils;

/**
 *
 * @author ndepo
 */
public class Test {
        public static void main(String args[]) throws Exception {
            System.out.println("Java Version : " + SystemUtils.JAVA_VERSION);
           
            boolean ok = isJavaVersion11mini();
            System.out.println("isJavaVersion11mini(): " + ok);
        }

    /**
     * Says if Java current major version is > 11
     * @return true if current major version is > 11
     */
    public static boolean isJavaVersion11mini() {
        String JAVA_11 = "11";
        String currentVersion =  SystemUtils.JAVA_VERSION;
        int compared = currentVersion.compareTo(JAVA_11);
        
        return compared >= 0;
    }
}

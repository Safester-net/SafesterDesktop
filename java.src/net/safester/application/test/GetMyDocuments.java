/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.safester.application.test;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

/**
 * java net.safester.application.test.GetMyDocuments
 * @author Nicolas de Pomereu
 *
 */
public class GetMyDocuments {
  public static void main(String args[]) {
     JFileChooser fr = new JFileChooser();
     FileSystemView fw = fr.getFileSystemView();
     System.out.println(fw.getDefaultDirectory());
  }
}
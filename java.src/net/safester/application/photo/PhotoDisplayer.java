/*
 * This file is part of Safester.                                    
 * Copyright (C) 2019, KawanSoft SAS
 * (https://www.Safester.net). All rights reserved.                                
 *                                                                               
 * Safester is free software; you can redistribute it and/or                 
 * modify it under the terms of the GNU Lesser General Public                    
 * License as published by the Free Software Foundation; either                  
 * version 2.1 of the License, or (at your option) any later version.            
 *                                                                               
 * Safester is distributed in the hope that it will be useful,               
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU             
 * Lesser General Public License for more details.                               
 *                                                                               
 * You should have received a copy of the GNU Lesser General Public              
 * License along with this library; if not, write to the Free Software           
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  
 * 02110-1301  USA
 * 
 * Any modifications to this file must keep this entire header
 * intact.
 */
package net.safester.application.photo;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JRootPane;

import com.swing.util.SwingUtil;

import net.safester.application.tool.UI_Util;

/**
 *
 * @author Nicolas de Pomereu
 */
public class PhotoDisplayer extends javax.swing.JDialog {

    //Calling jFrame
    private Window parent;
    
    private Point start_drag;
    private Point start_loc;
            
    
    /**
     * Creates new form PhotoDisplayer
     */
    public PhotoDisplayer(Window theParent, byte[] imageArray, int size) {
        this(theParent, imageArray, size, true);
    }
    
    /**
     * Creates new form PhotoDisplayer
     */
    public PhotoDisplayer(Window theParent, byte[] imageArray, int size, boolean isModal) {
        initComponents();
        
        if (UI_Util.isSynthetica()) {
            this.getRootPane().setWindowDecorationStyle(JRootPane.NONE);            
        }
        
        setAlwaysOnTop(true);
        setModal(isModal);
                                    
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
            
            public void mouseClicked(MouseEvent e) {
                dispose();
            }
        });
        
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });

        if (imageArray == null) {
            throw new IllegalArgumentException("imageArray can not be null!");
        }

        this.parent = theParent;

        this.setPreferredSize(new Dimension(size, size));
        this.setSize(new Dimension(size, size));

        jLabelImage.setText(null);

        ImageResizer imageResizer = new ImageResizer(imageArray);

        imageResizer.resize(size, size);
        BufferedImage bufferedImage = imageResizer.getBufferedImage();
        jLabelImage.setIcon(new ImageIcon(bufferedImage));
       
        this.keyListenerAdder();
        this.setLocationRelativeTo(parent);
    }

    
    private void formMousePressed(java.awt.event.MouseEvent evt) {
        this.start_drag = this.getScreenLocation(evt);
        this.start_loc = this.getLocation();
    }
    
    private void formMouseDragged(java.awt.event.MouseEvent evt) {
        Point current = this.getScreenLocation(evt);
        Point offset = new Point((int) current.getX() - (int) start_drag.getX(),
                (int) current.getY() - (int) start_drag.getY());

        Point new_location = new Point(
                (int) (this.start_loc.getX() + offset.getX()), (int) (this.start_loc
                .getY() + offset.getY()));
        this.setLocation(new_location);
    }

    Point getScreenLocation(MouseEvent e) {
        Point cursor = e.getPoint();
        Point target_location = this.getLocationOnScreen();
        return new Point((int) (target_location.getX() + cursor.getX()),
                (int) (target_location.getY() + cursor.getY()));
    }  
      
    /**
     * Universal key listener
     */
    private void keyListenerAdder() {
        List<Component> components = SwingUtil.getAllComponants(this);

        for (int i = 0; i < components.size(); i++) {
            Component comp = components.get(i);

            comp.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    this_keyReleased(e);
                }
            });
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // KEYS PART
    ///////////////////////////////////////////////////////////////////////////
    private void this_keyReleased(KeyEvent e) {
        //System.out.println("this_keyReleased(KeyEvent e) " + e.getComponent().getName());
        int id = e.getID();
        if (id == KeyEvent.KEY_RELEASED) {
            int keyCode = e.getKeyCode();

            if (keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_ENTER) {
                this.dispose();
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel = new javax.swing.JPanel();
        jLabelImage = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setUndecorated(true);

        jPanel.setLayout(new java.awt.BorderLayout());
        jPanel.add(jLabelImage, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 200, Short.MAX_VALUE)
                    .addComponent(jPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 200, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 150, Short.MAX_VALUE)
                    .addComponent(jPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 150, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PhotoDisplayer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PhotoDisplayer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PhotoDisplayer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PhotoDisplayer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    byte[] byteArray = ImageResizer.loadImage(new File("C:\\Users\\Nicolas de Pomereu\\Desktop\\chart.png"));
                    new PhotoDisplayer(null, byteArray, 200).setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(PhotoDisplayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelImage;
    private javax.swing.JPanel jPanel;
    // End of variables declaration//GEN-END:variables
}

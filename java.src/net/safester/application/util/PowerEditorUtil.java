/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.safester.application.util;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JList;
import javax.swing.JTable;

/**
 *
 * @author Nicolas de Pomereu
 */
public class PowerEditorUtil {
    
    public static int selectedLineMouseOver = 0;
    
    /**
     *
     * @param jList the table to select the row on
     */
    public static void selectRowWhenMouseOverLine(JList jList) {

        final JList theJlist = jList;

        if (jList.getVisibleRowCount() > 0) {
            try {
                theJlist.setSelectionInterval(0, 0);
            } catch (Exception e) {
                //
            }
        }

        theJlist.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                selectedLineMouseOver = theJlist.locationToIndex(p);
                theJlist.repaint();
            }
        });

        theJlist.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                selectedLineMouseOver = -1;
                theJlist.repaint();
            }
        });
    }
}

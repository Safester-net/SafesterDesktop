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
package net.safester.application.util.slide;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.awt.geom.*;

public class DissolveHack {

	public static void main(String[] args) {
		
		final JFrame frame = new JFrame("Dissolve Hack");
		JButton quit = new JButton("Quit");
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				new Dissolver().dissolveExit(frame);
			}
		});
		
		frame.getContentPane().add(quit);
		frame.pack();
		frame.setLocation(300,300);
		frame.setSize(400,400);
		frame.setVisible(true);
		
	}
}


class Dissolver extends JComponent implements Runnable {
	
	public Dissolver() {
	}
	public void dissolveExit(JFrame frame) {
		try {
			this.frame = frame;
			Robot robot = new Robot();
	
			// cap screen w/ frame to frame buffer
			Rectangle frame_rect = frame.getBounds();
			frame_buffer = robot.createScreenCapture(frame_rect);
			
			// hide frame
			frame.setVisible(false);
			
			// cap screen w/o frame
			Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
			Rectangle screen_rect = new Rectangle(0,0,screensize.width, screensize.height);
			screen_buffer = robot.createScreenCapture(screen_rect);
			
			// create big window w/o decorations
			fullscreen = new Window(new JFrame());
			fullscreen.setSize(screensize);
			fullscreen.add(this);
			this.setSize(screensize);
			fullscreen.setVisible(true);
			// start animation
			new Thread(this).start();
		} catch (Exception ex) {
			System.out.println(ex);
			ex.printStackTrace();
		}
	}
	
	Frame frame;
	Window fullscreen;
	BufferedImage frame_buffer;
	BufferedImage screen_buffer;
	int count;
	
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		// draw the screen, offset in case the window isn't at 0,0
		g.drawImage(screen_buffer,-fullscreen.getX(),-fullscreen.getY(),null);
		
		// draw the frame
		Composite old_comp = g2.getComposite();
		Composite fade = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f-((float)count)/20f);
		g2.setComposite(fade);
		g2.drawImage(frame_buffer,frame.getX(),frame.getY(),null);
		g2.setComposite(old_comp);
	}
	
	public void run() {
		try {
			count = 0;
			Thread.currentThread().sleep(100);
			for(int i=0; i<20; i++) {
				count = i;
				fullscreen.repaint();
				Thread.currentThread().sleep(100);
			}
		} catch (InterruptedException ex) {
		}
		System.exit(0);
	}
	
}


class SpinDissolver extends Dissolver {
	
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		// draw the screen, offset in case the window isn't at 0,0
		g.drawImage(screen_buffer,-fullscreen.getX(),-fullscreen.getY(),null);
		
		// save the current transform
		AffineTransform old_trans = g2.getTransform();
		
		// move to the upper left hand corner of the frame
		g2.translate(frame.getX(), frame.getY());
		
		// move the frame off towards the left
		g2.translate(-((count+1) * (frame.getX()+frame.getWidth())/20),0);
		
		// shrink the frame
		float scale = 1f / ((float)count+1);
		g2.scale(scale,scale);
		
		// rotate around the center
		g2.rotate(((float)count)/3.14/1.3, frame.getWidth()/2, frame.getHeight()/2);
		
		// finally draw the frame
		g2.drawImage(frame_buffer,0,0,null);
		
		// restore the current transform
		g2.setTransform(old_trans);
	}
	
}

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
package net.safester.application;

import com.swing.util.LookAndFeelHelper;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.swing.util.SwingUtil;
import java.awt.Color;

import net.safester.application.addrbooknew.tools.CryptAppUtil;
import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.ClipboardManager;
import net.safester.application.tool.JFileChooserFactory;
import net.safester.application.tool.WindowSettingManager;
import net.safester.application.util.UserPrefManager;
import net.safester.application.util.sound.AudioFilePlayer;

public class SoundChooser extends javax.swing.JFrame {

    public static final String CR_LF = System.getProperty("line.separator");
    private static final String DEFAULT_RESOURCE = "notify_1";

    private ClipboardManager clipboardManager;
    private MessagesManager messages = new MessagesManager();

    private JFrame thisOne;
    private JFrame parent;

    /**
     * Creates new form SafeShareItSettings
     */
    public SoundChooser(JFrame jFrame) {
        initComponents();
        parent = jFrame;
        thisOne = this;

        initCompany();
    }
   
    
    private void initCompany() {

        if (LookAndFeelHelper.isDarkMode()) {
            jLabelSound1.setBackground(Color.BLACK);
            jLabelSound2.setBackground(Color.BLACK);
            jLabelSound3.setBackground(Color.BLACK);
            jLabelSound4.setBackground(Color.BLACK);
            jLabelSound5.setBackground(Color.BLACK);
        }
        
        clipboardManager = new ClipboardManager(rootPane);

        this.jLabelTitle.setText(messages.getMessage("sound_picker"));
        this.setTitle(jLabelTitle.getText());
        this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());

        buttonGroup1.add(jRadioButton1);
        buttonGroup1.add(jRadioButton2);
        buttonGroup1.add(jRadioButton3);
        buttonGroup1.add(jRadioButton4);
        buttonGroup1.add(jRadioButton5);
        buttonGroup1.add(jRadioButtonUser);

        this.jRadioButton1.setText(messages.getMessage("notify_1"));
        this.jRadioButton2.setText(messages.getMessage("notify_2"));
        this.jRadioButton3.setText(messages.getMessage("notify_3"));
        this.jRadioButton4.setText(messages.getMessage("notify_4"));
        this.jRadioButton5.setText(messages.getMessage("notify_5"));
        this.jRadioButtonUser.setText(messages.getMessage("mp3_wav_file"));

        this.jRadioButton1.setName("notify_1");
        this.jRadioButton2.setName("notify_2");
        this.jRadioButton3.setName("notify_3");
        this.jRadioButton4.setName("notify_4");
        this.jRadioButton5.setName("notify_5");
        this.jRadioButtonUser.setName("notify_user_file");

        this.jRadioButton1.setSelected(true);

        String preferenceValue = UserPrefManager.getPreference(UserPrefManager.NOTIFY_SOUND_RESOURCE);
        System.out.println("preferenceValue: " + preferenceValue + ":");

        if (preferenceValue != null) {

            if (!preferenceValue.equals("notify_user_file")) {
                this.selectRadioButtonFromName(buttonGroup1, preferenceValue);
            } else {
                this.jRadioButtonUser.setSelected(true);
            }
        }

        String userFile = UserPrefManager.getPreference(UserPrefManager.NOTIFY_SOUND_USER_FILE);
        this.jTextFieldAudioFile.setText(userFile);

        jRadioButtonUserStateChanged(null);

        jButtonChoose.setText(messages.getMessage("choose"));
        jButtonTest.setText(messages.getMessage("test_selected_sound"));

        jButtonOk.setText(messages.getMessage("ok"));
        jButtonCancel.setText(messages.getMessage("cancel"));

        ButtonResizer br = new ButtonResizer(jPanelSouth);
        br.setWidthToMax();
        this.setLocationByPlatform(true);

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                WindowSettingManager.save(thisOne);
            }
        });

        this.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentMoved(ComponentEvent e) {
                WindowSettingManager.save(thisOne);
            }

            @Override
            public void componentResized(ComponentEvent e) {
                WindowSettingManager.save(thisOne);
            }

        });

        this.keyListenerAdder();
        this.setLocationRelativeTo(parent);

        this.setSize(new Dimension(615, 410));

        WindowSettingManager.load(this);

        SwingUtil.applySwingUpdates(rootPane);
    }
    /**
     * Plays the stored chosen noftify sound
     * @throws IOException 
     */
    public static void playNotifySound() throws Exception {

        String resource = UserPrefManager.getPreference(UserPrefManager.NOTIFY_SOUND_RESOURCE);

        if (resource == null) {
            resource = DEFAULT_RESOURCE;
        }
        
        if (! resource.equals("notify_user_file")) {
            String filename = "images/files/sounds/{0}";
            String wavName = getWavNameFromResource(resource);
            filename = filename.replace("{0}", wavName);
            InputStream inputStream = AudioFilePlayer.getInputStreamOnResource(filename);
            new AudioFilePlayer().playSound(inputStream);
        }
        else {
            String file = UserPrefManager.getPreference(UserPrefManager.NOTIFY_SOUND_USER_FILE);
            File soundFile = new File(file);
            if (soundFile.exists()) {
               new AudioFilePlayer().playSound(soundFile); 
            }
            else {
                // Play default resource
                String filename = "images/files/sounds/{0}";
                String wavName = getWavNameFromResource(DEFAULT_RESOURCE);
                filename = filename.replace("{0}", wavName);
                InputStream inputStream = AudioFilePlayer.getInputStreamOnResource(filename);
                new AudioFilePlayer().playSound(inputStream);            
            }
        }


    }
    
    public static String getWavNameFromResource(String resource) {
        //String wavName = MessagesManager.get(resource);
        //wavName = wavName.toLowerCase();
        //wavName = wavName.replace(",", "");
        //wavName = wavName.replace(" ", "_");
        String wavName = null;
        if (resource.equals("notify_1")) {
            wavName = "melodious_high-pitched_alert_with_reverb.wav";
        } else if (resource.equals("notify_2")) {
            wavName = "melodious_alert_with_arpeggios_and_delay.wav";
        } else if (resource.equals("notify_3")) {
            wavName = "melodious_and_happy_alert.wav";
        } else if (resource.equals("notify_4")) {
            wavName = "echoing_two-tone_alerting.wav";
        } else if (resource.equals("notify_5")) {
            wavName = "pure_high-pitched_reverberating_chord.wav";
        }
        return wavName;
    }
    
    private void testSound() {
        try {

            String name = getSelectedButtonName(buttonGroup1);
            //System.out.println("name: " + name);

            if (!name.equals("notify_user_file")) {
                String resource = "images/files/sounds/{0}";
                resource = resource.replace("{0}", getWavNameFromResource(name));
                InputStream inputStream = AudioFilePlayer.getInputStreamOnResource(resource);
                AudioFilePlayer audioFilePlayer = new AudioFilePlayer();
                audioFilePlayer.playSound(inputStream);
                testExceptionRaised(audioFilePlayer);
                return;
            }

            // We are asked to play the file
            if (jTextFieldAudioFile.getText().isEmpty()) {
                String msg = messages.getMessage("please_select_a_mp3_or_wav_file");
                JOptionPane.showMessageDialog(this, msg, Parms.APP_NAME, JOptionPane.ERROR_MESSAGE);
                return;
            }

            File file = new File(jTextFieldAudioFile.getText());

            if (! isWawOrMP3(file)) {
                String msg = messages.getMessage("please_select_a_mp3_or_wav_file");
                JOptionPane.showMessageDialog(this, msg, Parms.APP_NAME, JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!file.exists()) {
                String msg = messages.getMessage("the_selected_file_does_not_exist") + CR_LF + file + CR_LF + CR_LF + messages.getMessage("please_select_a_mp3_or_wav_file");
                JOptionPane.showMessageDialog(this, msg, Parms.APP_NAME, JOptionPane.ERROR_MESSAGE);
                return;
            }

//            // Futur usage to chang dynamicall the sound wave
//            Runnable r = new Runnable() {
//                public void run() {
//                    //jLabel1.setIcon(Parms.createImageIcon(Parms.ICON_PATH));
//                }
//            };
//            new Thread(r).start();
           
            // Play file
            AudioFilePlayer audioFilePlayer = new AudioFilePlayer();
            audioFilePlayer.playSound(file);
            
            testExceptionRaised(audioFilePlayer);

        } catch (Exception iOException) {
            String msg = iOException.getMessage();
            JOptionPane.showMessageDialog(this, msg, Parms.APP_NAME, JOptionPane.ERROR_MESSAGE);
            return;
        }

    }

    public void testExceptionRaised(AudioFilePlayer audioFilePlayer) throws HeadlessException {
        // Test Exceptions for 2 seconds maximum
        long begin = System.currentTimeMillis();
        while (true) {
            if (audioFilePlayer.getException() != null) {
                JOptionPane.showMessageDialog(this, "Sound can not be played. Reason:" + CR_LF + audioFilePlayer.getException() , Parms.APP_NAME, JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(SoundChooser.class.getName()).log(Level.SEVERE, null, ex);
            }
            long now = System.currentTimeMillis();
            if (now - begin > 1500) {
                return;
            }
        }
    }

    public static boolean isWawOrMP3(File file) {
        return file.toString().toLowerCase().endsWith(".mp3") || file.toString().toLowerCase().endsWith(".wav")  ;
    }

    private void update() {

        String name = getSelectedButtonName(buttonGroup1);
        //System.out.println("name: " + name);

        if (!name.equals("notify_user_file")) {
            UserPrefManager.setPreference(UserPrefManager.NOTIFY_SOUND_RESOURCE, name);
            dispose();
            return;
        }

        if (jTextFieldAudioFile.getText().isEmpty()) {
            String msg = messages.getMessage("please_select_a_mp3_or_wav_file");
            JOptionPane.showMessageDialog(this, msg, Parms.APP_NAME, JOptionPane.ERROR_MESSAGE);
            return;
        }

        File file = new File(jTextFieldAudioFile.getText());

        if (! isWawOrMP3(file)) {
            String msg = messages.getMessage("please_select_a_mp3_or_wav_file");
            JOptionPane.showMessageDialog(this, msg, Parms.APP_NAME, JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!file.exists()) {
            String msg = messages.getMessage("the_selected_file_does_not_exist") + CR_LF + file + CR_LF + CR_LF + messages.getMessage("please_select_a_mp3_or_wav_file");
            JOptionPane.showMessageDialog(this, msg, Parms.APP_NAME, JOptionPane.ERROR_MESSAGE);
            return;
        }

        UserPrefManager.setPreference(UserPrefManager.NOTIFY_SOUND_RESOURCE, name);
        UserPrefManager.setPreference(UserPrefManager.NOTIFY_SOUND_USER_FILE, file.toString());
        this.dispose();

    }

    private void selectRadioButtonFromName(ButtonGroup buttonGroup, String preferenceValue) {
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();

            //System.out.println("button name: " + button.getName() );
            if (button.getName() != null && button.getName().equals(preferenceValue)) {
                button.setSelected(true);
                button.requestFocusInWindow();
                return;
            }

        }
    }

    public String getSelectedButtonName(ButtonGroup buttonGroup) {
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {
                return button.getName();
            }
        }

        return null;
    }

    /**
     * Universal key listener
     *
     */
    private void keyListenerAdder() {
        java.util.List<Component> components = SwingUtil.getAllComponants(this);

        for (int i = 0; i < components.size(); i++) {
            Component comp = components.get(i);

            comp.addKeyListener(new KeyAdapter() {

                @Override
                public void keyPressed(KeyEvent e) {
                    this_keyPressed(e);
                }
            });
        }
    }

    private void this_keyPressed(KeyEvent e) {
        int id = e.getID();
        if (id == KeyEvent.KEY_PRESSED) {
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanelNorth = new javax.swing.JPanel();
        jPanelWest = new javax.swing.JPanel();
        jPanelEast = new javax.swing.JPanel();
        jPanelCenter = new javax.swing.JPanel();
        jPanelTop = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jPanelSepLine1 = new javax.swing.JPanel();
        jSeparator3 = new javax.swing.JSeparator();
        jPanelSep13 = new javax.swing.JPanel();
        jPanelMain = new javax.swing.JPanel();
        jPanelSepBlank1 = new javax.swing.JPanel();
        jPanelSound1 = new javax.swing.JPanel();
        jPanelSep7 = new javax.swing.JPanel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jLabelSound1 = new javax.swing.JLabel();
        jPanelSound2 = new javax.swing.JPanel();
        jPanelSep6 = new javax.swing.JPanel();
        jRadioButton2 = new javax.swing.JRadioButton();
        jLabelSound2 = new javax.swing.JLabel();
        jPanelSound3 = new javax.swing.JPanel();
        jPanelSep5 = new javax.swing.JPanel();
        jRadioButton3 = new javax.swing.JRadioButton();
        jLabelSound3 = new javax.swing.JLabel();
        jPanelSound4 = new javax.swing.JPanel();
        jPanelSep4 = new javax.swing.JPanel();
        jRadioButton4 = new javax.swing.JRadioButton();
        jLabelSound4 = new javax.swing.JLabel();
        jPanelSound5 = new javax.swing.JPanel();
        jPanelSep3 = new javax.swing.JPanel();
        jRadioButton5 = new javax.swing.JRadioButton();
        jLabelSound5 = new javax.swing.JLabel();
        jPanelSoundUser = new javax.swing.JPanel();
        jPanelSep = new javax.swing.JPanel();
        jRadioButtonUser = new javax.swing.JRadioButton();
        jTextFieldAudioFile = new javax.swing.JTextField();
        jPanelSep1 = new javax.swing.JPanel();
        jButtonChoose = new javax.swing.JButton();
        jPanelSound6 = new javax.swing.JPanel();
        jPanelSep2 = new javax.swing.JPanel();
        jButtonTest = new javax.swing.JButton();
        jPanelSep14 = new javax.swing.JPanel();
        jPanelSepBlank2 = new javax.swing.JPanel();
        jPanelSepLine2 = new javax.swing.JPanel();
        jSeparator4 = new javax.swing.JSeparator();
        jPanelSouth = new javax.swing.JPanel();
        jButtonOk = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().add(jPanelNorth, java.awt.BorderLayout.NORTH);
        getContentPane().add(jPanelWest, java.awt.BorderLayout.WEST);
        getContentPane().add(jPanelEast, java.awt.BorderLayout.EAST);

        jPanelCenter.setLayout(new javax.swing.BoxLayout(jPanelCenter, javax.swing.BoxLayout.Y_AXIS));

        jPanelTop.setMaximumSize(new java.awt.Dimension(32767, 45));
        jPanelTop.setMinimumSize(new java.awt.Dimension(94, 45));
        jPanelTop.setPreferredSize(new java.awt.Dimension(45, 45));
        jPanelTop.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/window_equalizer.png"))); // NOI18N
        jLabelTitle.setText("jLabelTitle");
        jPanelTop.add(jLabelTitle);

        jPanelCenter.add(jPanelTop);

        jPanelSepLine1.setMaximumSize(new java.awt.Dimension(32767, 6));
        jPanelSepLine1.setMinimumSize(new java.awt.Dimension(0, 6));
        jPanelSepLine1.setPreferredSize(new java.awt.Dimension(0, 6));
        jPanelSepLine1.setLayout(new javax.swing.BoxLayout(jPanelSepLine1, javax.swing.BoxLayout.LINE_AXIS));

        jSeparator3.setMaximumSize(new java.awt.Dimension(32767, 6));
        jSeparator3.setMinimumSize(new java.awt.Dimension(0, 6));
        jSeparator3.setPreferredSize(new java.awt.Dimension(0, 6));
        jPanelSepLine1.add(jSeparator3);

        jPanelCenter.add(jPanelSepLine1);

        jPanelSep13.setMaximumSize(new java.awt.Dimension(32767, 5000));
        jPanelSep13.setMinimumSize(new java.awt.Dimension(10, 5));
        jPanelSep13.setPreferredSize(new java.awt.Dimension(10, 5));
        jPanelCenter.add(jPanelSep13);

        jPanelMain.setLayout(new javax.swing.BoxLayout(jPanelMain, javax.swing.BoxLayout.Y_AXIS));

        jPanelSepBlank1.setMaximumSize(new java.awt.Dimension(32767, 7));
        jPanelSepBlank1.setMinimumSize(new java.awt.Dimension(0, 7));
        jPanelSepBlank1.setPreferredSize(new java.awt.Dimension(0, 7));
        jPanelSepBlank1.setLayout(new javax.swing.BoxLayout(jPanelSepBlank1, javax.swing.BoxLayout.LINE_AXIS));
        jPanelMain.add(jPanelSepBlank1);

        jPanelSound1.setMaximumSize(new java.awt.Dimension(32767, 31));
        jPanelSound1.setMinimumSize(new java.awt.Dimension(10, 31));
        jPanelSound1.setPreferredSize(new java.awt.Dimension(10, 31));
        jPanelSound1.setLayout(new javax.swing.BoxLayout(jPanelSound1, javax.swing.BoxLayout.LINE_AXIS));

        jPanelSep7.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanelSep7.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanelSep7.setPreferredSize(new java.awt.Dimension(5, 5));
        jPanelSep7.setLayout(new javax.swing.BoxLayout(jPanelSep7, javax.swing.BoxLayout.LINE_AXIS));
        jPanelSound1.add(jPanelSep7);

        jRadioButton1.setText("jRadioButton1");
        jRadioButton1.setMaximumSize(new java.awt.Dimension(300, 25));
        jRadioButton1.setMinimumSize(new java.awt.Dimension(300, 25));
        jRadioButton1.setName(""); // NOI18N
        jRadioButton1.setPreferredSize(new java.awt.Dimension(300, 25));
        jRadioButton1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButton1ItemStateChanged(evt);
            }
        });
        jPanelSound1.add(jRadioButton1);

        jLabelSound1.setBackground(new java.awt.Color(255, 255, 255));
        jLabelSound1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files/notify_1.png"))); // NOI18N
        jLabelSound1.setOpaque(true);
        jPanelSound1.add(jLabelSound1);

        jPanelMain.add(jPanelSound1);

        jPanelSound2.setMaximumSize(new java.awt.Dimension(32767, 31));
        jPanelSound2.setMinimumSize(new java.awt.Dimension(10, 31));
        jPanelSound2.setPreferredSize(new java.awt.Dimension(10, 31));
        jPanelSound2.setLayout(new javax.swing.BoxLayout(jPanelSound2, javax.swing.BoxLayout.LINE_AXIS));

        jPanelSep6.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanelSep6.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanelSep6.setPreferredSize(new java.awt.Dimension(5, 5));
        jPanelSep6.setLayout(new javax.swing.BoxLayout(jPanelSep6, javax.swing.BoxLayout.LINE_AXIS));
        jPanelSound2.add(jPanelSep6);

        jRadioButton2.setText("jRadioButton2");
        jRadioButton2.setMaximumSize(new java.awt.Dimension(300, 25));
        jRadioButton2.setMinimumSize(new java.awt.Dimension(300, 25));
        jRadioButton2.setPreferredSize(new java.awt.Dimension(300, 25));
        jRadioButton2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jRadioButton2StateChanged(evt);
            }
        });
        jPanelSound2.add(jRadioButton2);

        jLabelSound2.setBackground(new java.awt.Color(255, 255, 255));
        jLabelSound2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files/notify_2.png"))); // NOI18N
        jLabelSound2.setOpaque(true);
        jPanelSound2.add(jLabelSound2);

        jPanelMain.add(jPanelSound2);

        jPanelSound3.setMaximumSize(new java.awt.Dimension(32767, 31));
        jPanelSound3.setMinimumSize(new java.awt.Dimension(10, 31));
        jPanelSound3.setPreferredSize(new java.awt.Dimension(10, 31));
        jPanelSound3.setLayout(new javax.swing.BoxLayout(jPanelSound3, javax.swing.BoxLayout.LINE_AXIS));

        jPanelSep5.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanelSep5.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanelSep5.setPreferredSize(new java.awt.Dimension(5, 5));
        jPanelSep5.setLayout(new javax.swing.BoxLayout(jPanelSep5, javax.swing.BoxLayout.LINE_AXIS));
        jPanelSound3.add(jPanelSep5);

        jRadioButton3.setText("jRadioButton3");
        jRadioButton3.setMaximumSize(new java.awt.Dimension(300, 25));
        jRadioButton3.setMinimumSize(new java.awt.Dimension(300, 25));
        jRadioButton3.setName(""); // NOI18N
        jRadioButton3.setPreferredSize(new java.awt.Dimension(300, 25));
        jRadioButton3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButton3ItemStateChanged(evt);
            }
        });
        jPanelSound3.add(jRadioButton3);

        jLabelSound3.setBackground(new java.awt.Color(255, 255, 255));
        jLabelSound3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files/notify_3.png"))); // NOI18N
        jLabelSound3.setOpaque(true);
        jPanelSound3.add(jLabelSound3);

        jPanelMain.add(jPanelSound3);

        jPanelSound4.setMaximumSize(new java.awt.Dimension(32767, 31));
        jPanelSound4.setMinimumSize(new java.awt.Dimension(10, 31));
        jPanelSound4.setPreferredSize(new java.awt.Dimension(10, 31));
        jPanelSound4.setLayout(new javax.swing.BoxLayout(jPanelSound4, javax.swing.BoxLayout.LINE_AXIS));

        jPanelSep4.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanelSep4.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanelSep4.setPreferredSize(new java.awt.Dimension(5, 5));
        jPanelSep4.setLayout(new javax.swing.BoxLayout(jPanelSep4, javax.swing.BoxLayout.LINE_AXIS));
        jPanelSound4.add(jPanelSep4);

        jRadioButton4.setText("jRadioButton4");
        jRadioButton4.setMaximumSize(new java.awt.Dimension(300, 25));
        jRadioButton4.setMinimumSize(new java.awt.Dimension(300, 25));
        jRadioButton4.setName(""); // NOI18N
        jRadioButton4.setPreferredSize(new java.awt.Dimension(300, 25));
        jRadioButton4.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButton4ItemStateChanged(evt);
            }
        });
        jPanelSound4.add(jRadioButton4);

        jLabelSound4.setBackground(new java.awt.Color(255, 255, 255));
        jLabelSound4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files/notify_4.png"))); // NOI18N
        jLabelSound4.setOpaque(true);
        jPanelSound4.add(jLabelSound4);

        jPanelMain.add(jPanelSound4);

        jPanelSound5.setMaximumSize(new java.awt.Dimension(32767, 31));
        jPanelSound5.setMinimumSize(new java.awt.Dimension(10, 31));
        jPanelSound5.setPreferredSize(new java.awt.Dimension(10, 31));
        jPanelSound5.setLayout(new javax.swing.BoxLayout(jPanelSound5, javax.swing.BoxLayout.LINE_AXIS));

        jPanelSep3.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanelSep3.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanelSep3.setPreferredSize(new java.awt.Dimension(5, 5));
        jPanelSep3.setLayout(new javax.swing.BoxLayout(jPanelSep3, javax.swing.BoxLayout.LINE_AXIS));
        jPanelSound5.add(jPanelSep3);

        jRadioButton5.setText("jRadioButton5");
        jRadioButton5.setMaximumSize(new java.awt.Dimension(300, 25));
        jRadioButton5.setMinimumSize(new java.awt.Dimension(300, 25));
        jRadioButton5.setName(""); // NOI18N
        jRadioButton5.setPreferredSize(new java.awt.Dimension(300, 25));
        jRadioButton5.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButton5ItemStateChanged(evt);
            }
        });
        jPanelSound5.add(jRadioButton5);

        jLabelSound5.setBackground(new java.awt.Color(255, 255, 255));
        jLabelSound5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files/notify_5.png"))); // NOI18N
        jLabelSound5.setOpaque(true);
        jPanelSound5.add(jLabelSound5);

        jPanelMain.add(jPanelSound5);

        jPanelSoundUser.setMaximumSize(new java.awt.Dimension(32767, 35));
        jPanelSoundUser.setMinimumSize(new java.awt.Dimension(10, 35));
        jPanelSoundUser.setPreferredSize(new java.awt.Dimension(10, 35));
        jPanelSoundUser.setLayout(new javax.swing.BoxLayout(jPanelSoundUser, javax.swing.BoxLayout.LINE_AXIS));

        jPanelSep.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanelSep.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanelSep.setPreferredSize(new java.awt.Dimension(5, 5));
        jPanelSep.setLayout(new javax.swing.BoxLayout(jPanelSep, javax.swing.BoxLayout.LINE_AXIS));
        jPanelSoundUser.add(jPanelSep);

        jRadioButtonUser.setText("jRadioButtonUser");
        jRadioButtonUser.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jRadioButtonUserStateChanged(evt);
            }
        });
        jPanelSoundUser.add(jRadioButtonUser);

        jTextFieldAudioFile.setMaximumSize(new java.awt.Dimension(2147483647, 22));
        jPanelSoundUser.add(jTextFieldAudioFile);

        jPanelSep1.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanelSep1.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanelSep1.setPreferredSize(new java.awt.Dimension(5, 5));
        jPanelSep1.setLayout(new javax.swing.BoxLayout(jPanelSep1, javax.swing.BoxLayout.LINE_AXIS));
        jPanelSoundUser.add(jPanelSep1);

        jButtonChoose.setText("jButtonChoose");
        jButtonChoose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonChooseActionPerformed(evt);
            }
        });
        jPanelSoundUser.add(jButtonChoose);

        jPanelMain.add(jPanelSoundUser);

        jPanelSound6.setMaximumSize(new java.awt.Dimension(32767, 31));
        jPanelSound6.setMinimumSize(new java.awt.Dimension(10, 31));
        jPanelSound6.setPreferredSize(new java.awt.Dimension(10, 31));
        jPanelSound6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 5));

        jPanelSep2.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanelSep2.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanelSep2.setPreferredSize(new java.awt.Dimension(5, 5));
        jPanelSep2.setLayout(new javax.swing.BoxLayout(jPanelSep2, javax.swing.BoxLayout.LINE_AXIS));
        jPanelSound6.add(jPanelSep2);

        jButtonTest.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/media_play.png"))); // NOI18N
        jButtonTest.setText("jButtonTest");
        jButtonTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTestActionPerformed(evt);
            }
        });
        jPanelSound6.add(jButtonTest);

        jPanelMain.add(jPanelSound6);

        jPanelSep14.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanelSep14.setMinimumSize(new java.awt.Dimension(10, 5));
        jPanelSep14.setPreferredSize(new java.awt.Dimension(10, 5));
        jPanelMain.add(jPanelSep14);

        jPanelSepBlank2.setMaximumSize(new java.awt.Dimension(32767, 7));
        jPanelSepBlank2.setMinimumSize(new java.awt.Dimension(0, 7));
        jPanelSepBlank2.setPreferredSize(new java.awt.Dimension(0, 7));
        jPanelSepBlank2.setLayout(new javax.swing.BoxLayout(jPanelSepBlank2, javax.swing.BoxLayout.LINE_AXIS));
        jPanelMain.add(jPanelSepBlank2);

        jPanelCenter.add(jPanelMain);

        jPanelSepLine2.setLayout(new javax.swing.BoxLayout(jPanelSepLine2, javax.swing.BoxLayout.LINE_AXIS));

        jSeparator4.setMaximumSize(new java.awt.Dimension(32767, 6));
        jSeparator4.setMinimumSize(new java.awt.Dimension(0, 6));
        jSeparator4.setPreferredSize(new java.awt.Dimension(0, 6));
        jPanelSepLine2.add(jSeparator4);

        jPanelCenter.add(jPanelSepLine2);

        getContentPane().add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jPanelSouth.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 10));

        jButtonOk.setText("jButtonOk");
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });
        jPanelSouth.add(jButtonOk);

        jButtonCancel.setText("jButtonCancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        jPanelSouth.add(jButtonCancel);

        jPanel9.setMaximumSize(new java.awt.Dimension(1, 32767));
        jPanel9.setMinimumSize(new java.awt.Dimension(1, 10));
        jPanel9.setPreferredSize(new java.awt.Dimension(1, 10));
        jPanelSouth.add(jPanel9);

        getContentPane().add(jPanelSouth, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
        update();
    }//GEN-LAST:event_jButtonOkActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jRadioButtonUserStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jRadioButtonUserStateChanged
        if (jRadioButtonUser.isSelected()) {
            jTextFieldAudioFile.setEnabled(true);
            jButtonChoose.setEnabled(true);
        } else {
            jTextFieldAudioFile.setEnabled(false);
            jButtonChoose.setEnabled(false);
        }
    }//GEN-LAST:event_jRadioButtonUserStateChanged

    private void jButtonChooseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonChooseActionPerformed
        JFileChooser saveTo = JFileChooserFactory.getInstance();

        saveTo.setMultiSelectionEnabled(false);
        saveTo.setFileSelectionMode(JFileChooser.FILES_ONLY);

        saveTo.setFileFilter(new Mp3OrWavFileFilter());

        int returnVal = saveTo.showSaveDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            
            File file = saveTo.getSelectedFile();
            
            if (file.length() > CryptAppUtil.MB * 1.5) {
                String msg = messages.getMessage("the_file_must_be_lessa_than_1dot5_mb");
                JOptionPane.showMessageDialog(this, msg, Parms.APP_NAME, JOptionPane.ERROR_MESSAGE);
                jButtonChooseActionPerformed(null);
                return;
            }
            
            jTextFieldAudioFile.setText(file.toString());
        }
    }//GEN-LAST:event_jButtonChooseActionPerformed

    private void jButtonTestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTestActionPerformed
        testSound();
    }//GEN-LAST:event_jButtonTestActionPerformed

    private void jRadioButton1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton1ItemStateChanged
        if (jRadioButton1.isSelected()) {
            jLabelSound1.setIcon(Parms.createImageIcon("images/files/notify_on_1.png"));
        } else {
            jLabelSound1.setIcon(Parms.createImageIcon("images/files/notify_1.png"));
        }
        //this.repaint();
        //this.update(getGraphics());
    }//GEN-LAST:event_jRadioButton1ItemStateChanged

    private void jRadioButton2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jRadioButton2StateChanged
        if (jRadioButton2.isSelected()) {
            jLabelSound2.setIcon(Parms.createImageIcon("images/files/notify_on_2.png"));
        } else {
            jLabelSound2.setIcon(Parms.createImageIcon("images/files/notify_2.png"));
        }
    }//GEN-LAST:event_jRadioButton2StateChanged

    private void jRadioButton3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton3ItemStateChanged
       if (jRadioButton3.isSelected()) {
            jLabelSound3.setIcon(Parms.createImageIcon("images/files/notify_on_3.png"));
        } else {
            jLabelSound3.setIcon(Parms.createImageIcon("images/files/notify_3.png"));
        }
    }//GEN-LAST:event_jRadioButton3ItemStateChanged

    private void jRadioButton4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton4ItemStateChanged
       if (jRadioButton4.isSelected()) {
            jLabelSound4.setIcon(Parms.createImageIcon("images/files/notify_on_4.png"));
        } else {
            jLabelSound4.setIcon(Parms.createImageIcon("images/files/notify_4.png"));
        }
    }//GEN-LAST:event_jRadioButton4ItemStateChanged

    private void jRadioButton5ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton5ItemStateChanged
       if (jRadioButton5.isSelected()) {
            jLabelSound5.setIcon(Parms.createImageIcon("images/files/notify_on_5.png"));
        } else {
            jLabelSound5.setIcon(Parms.createImageIcon("images/files/notify_5.png"));
        }
    }//GEN-LAST:event_jRadioButton5ItemStateChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception ex) {
            System.out.println("Failed loading L&F: ");
            System.out.println(ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {

                new SoundChooser(null).setVisible(true);

            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonChoose;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JButton jButtonTest;
    private javax.swing.JLabel jLabelSound1;
    private javax.swing.JLabel jLabelSound2;
    private javax.swing.JLabel jLabelSound3;
    private javax.swing.JLabel jLabelSound4;
    private javax.swing.JLabel jLabelSound5;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelEast;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelSep;
    private javax.swing.JPanel jPanelSep1;
    private javax.swing.JPanel jPanelSep13;
    private javax.swing.JPanel jPanelSep14;
    private javax.swing.JPanel jPanelSep2;
    private javax.swing.JPanel jPanelSep3;
    private javax.swing.JPanel jPanelSep4;
    private javax.swing.JPanel jPanelSep5;
    private javax.swing.JPanel jPanelSep6;
    private javax.swing.JPanel jPanelSep7;
    private javax.swing.JPanel jPanelSepBlank1;
    private javax.swing.JPanel jPanelSepBlank2;
    private javax.swing.JPanel jPanelSepLine1;
    private javax.swing.JPanel jPanelSepLine2;
    private javax.swing.JPanel jPanelSound1;
    private javax.swing.JPanel jPanelSound2;
    private javax.swing.JPanel jPanelSound3;
    private javax.swing.JPanel jPanelSound4;
    private javax.swing.JPanel jPanelSound5;
    private javax.swing.JPanel jPanelSound6;
    private javax.swing.JPanel jPanelSoundUser;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelTop;
    private javax.swing.JPanel jPanelWest;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JRadioButton jRadioButtonUser;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JTextField jTextFieldAudioFile;
    // End of variables declaration//GEN-END:variables

}

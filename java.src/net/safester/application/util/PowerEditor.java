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
package net.safester.application.util;
import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

/**
 * <dl>
 * <dt><b>Creation date :</b></dt>
 * <dd>8 oct. 2003</dd>
 * </dl>
 * 
 * @author Pierre LE LANNIC
 */

public class PowerEditor extends JPanel
{
    
	private static final long serialVersionUID = 9053274198450193568L;

	public static boolean DEBUG = false;
    
    private Set<String> theSet;

    private WordMenuWindow theWordMenu;

    private JTextComponent theTextComponent;

    private Window theOwner;

    // private static final char[] WORD_SEPARATORS =
    // {' ', '\n', '\t', '.', ',', ';', '!', '?', '\'', '(', ')', '[', ']',
    // '\"', '{', '}', '/', '\\', '<','>'};

    // Remote the '.' as Word Separator
    private static final char[] WORD_SEPARATORS =
        { '\n', '\t', ',', ';', '!', '?', '\'', '(', ')', '[', ']', '\"', '{',
                '}', '/', '\\' };

    private Word theCurrentWord;

    private class Word
    {
        private int theWordStart;

        private int theWordLength;

        public Word()
        {
            theWordStart = -1;
            theWordLength = 0;
        }

        public void setBounds(int aStart, int aLength)
        {
            theWordStart = Math.max(-1, aStart);
            theWordLength = Math.max(0, aLength);
            if (theWordStart == -1)
                theWordLength = 0;
            if (theWordLength == 0)
                theWordStart = -1;
        }
        public void increaseLength(int newCharLength)
        {
            int max = theTextComponent.getText().length() - theWordStart;
            theWordLength = Math.min(max, theWordLength + newCharLength);
            if (theWordLength == 0)
                theWordStart = -1;
        }

        public void decreaseLength(int removedCharLength)
        {
            theWordLength = Math.max(0, theWordLength - removedCharLength);
            if (theWordLength == 0)
                theWordStart = -1;
        }

        public int getStart()
        {
            return theWordStart;
        }

        public int getLength()
        {
            return theWordLength;
        }

        public int getEnd()
        {
            return theWordStart + theWordLength;
        }

        public String toString()
        {
            String toReturn = null;
            try
            {
                toReturn = theTextComponent
                        .getText(theWordStart, theWordLength);
            }
            catch (BadLocationException e)
            {
            }
            if (toReturn == null)
                toReturn = "";
            return toReturn;
        }
    }

    private class WordMenuWindow extends JWindow
    {
		private static final long serialVersionUID = 7646767713027983185L;

		public JList<?> theList;

        private DefaultListModel<String> theModel;

        private Point theRelativePosition;

        private class WordMenuKeyListener extends KeyAdapter
        {
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    onSelected();
                }
            }
        }

        private class WordMenuMouseListener extends MouseAdapter
        {
            public void mouseClicked(MouseEvent e)
            {
                // SAFELOGIC HACK
                if ((e.getButton() == MouseEvent.BUTTON1)
                        && (e.getClickCount() >= 1))
                {
                    onSelected();
                }
            }
        }

        public WordMenuWindow()
        {
            super(theOwner);
            theModel = new DefaultListModel<>();
            theRelativePosition = new Point(0, 0);
            loadUIElements();
            setEventManagement();
        }

        private void loadUIElements()
        {
            theList = new JList<>(theModel)
            {
				private static final long serialVersionUID = -2357183904884548144L;

				public int getVisibleRowCount()
                {
                    return Math.min(theModel.getSize(), 10);
                }
            };

            //HACK 23/09/19: add clean cursor over 
            PowerEditorUtil.selectRowWhenMouseOverLine(theList);
            theList.setCellRenderer(new PowerEditorListCellRenderer());
        
            theList.setFont(theTextComponent.getFont());

            theList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            // theList.setBackground(new Color(235, 244, 254));
            JScrollPane scrollPane = new JScrollPane(theList,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

            // scrollPane.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
            scrollPane.setBorder(BorderFactory.createEtchedBorder());
            setContentPane(scrollPane);
        }

        private void setEventManagement()
        {
            theList.addKeyListener(new WordMenuKeyListener());
            theList.addMouseListener(new WordMenuMouseListener());                        
        }

        private void onSelected()
        {
            String word = (String) theList.getSelectedValue();
            if (!word.endsWith(";"))
            {
                word += ";";
            }
            setCurrentTypedWord(word);
        }

        public void display(Point aPoint)
        {
            theRelativePosition = aPoint;
            Point p = theTextComponent.getLocationOnScreen();
            setLocation(new Point(p.x + aPoint.x, p.y + aPoint.y));
        }

        public void move()
        {
            if (theRelativePosition != null)
            {
                Point p = theTextComponent.getLocationOnScreen();
                setLocation(new Point(p.x + theRelativePosition.x, p.y
                        + theRelativePosition.y));
            }
        }

        public void setWords(String[] someWords)
        {
            theModel.clear();
            if ((someWords == null) || (someWords.length == 0))
            {
                setVisible(false);
                return;
            }
            for (int i = 0; i < someWords.length; i++)
            {
                theModel.addElement(someWords[i]);
            }

            pack();
            // pack();
        }

        public void moveDown()
        {
            if (theModel.getSize() < 1)
                return;
            int current = theList.getSelectedIndex();
            int newIndex = Math.min(theModel.getSize() - 1, current + 1);
            theList.setSelectionInterval(newIndex, newIndex);
            theList.scrollRectToVisible(theList.getCellBounds(newIndex,
                    newIndex));
        }

        public void moveUp()
        {
            if (theModel.getSize() < 1)
                return;
            int current = theList.getSelectedIndex();
            int newIndex = Math.max(0, current - 1);
            theList.setSelectionInterval(newIndex, newIndex);
            theList.scrollRectToVisible(theList.getCellBounds(newIndex,
                    newIndex));
        }

        public void moveStart()
        {
            if (theModel.getSize() < 1)
                return;
            theList.setSelectionInterval(0, 0);
            theList.scrollRectToVisible(theList.getCellBounds(0, 0));
        }

        public void moveEnd()
        {
            if (theModel.getSize() < 1)
                return;
            int endIndex = theModel.getSize() - 1;
            theList.setSelectionInterval(endIndex, endIndex);
            theList.scrollRectToVisible(theList.getCellBounds(endIndex,
                    endIndex));
        }

        public void movePageUp()
        {
            if (theModel.getSize() < 1)
                return;
            int current = theList.getSelectedIndex();
            int newIndex = Math.max(0, current
                    - Math.max(0, theList.getVisibleRowCount() - 1));
            theList.setSelectionInterval(newIndex, newIndex);
            theList.scrollRectToVisible(theList.getCellBounds(newIndex,
                    newIndex));
        }

        public void movePageDown()
        {
            if (theModel.getSize() < 1)
                return;
            int current = theList.getSelectedIndex();
            int newIndex = Math.min(theModel.getSize() - 1, current
                    + Math.max(0, theList.getVisibleRowCount() - 1));
            theList.setSelectionInterval(newIndex, newIndex);
            theList.scrollRectToVisible(theList.getCellBounds(newIndex,
                    newIndex));
        }
    }

    public PowerEditor(Set<String> aLexiconSet, JFrame anOwner,
            JTextComponent aTextComponent)
    {
        super(new BorderLayout());
        theOwner = anOwner;
        theTextComponent = aTextComponent;
        theWordMenu = new WordMenuWindow();
        theSet = aLexiconSet;
        theCurrentWord = new Word();
        loadUIElements();
        setEventManagement();
    }

    public JTextComponent getTextComponent()
    {
        return theTextComponent;
    }

    private void loadUIElements()
    {
        add(theTextComponent, BorderLayout.CENTER);
    }

    public void close()
    {
        if (this.theWordMenu != null && this.theWordMenu.isVisible())
        {
            this.theWordMenu.dispose();
        }
    }
    
    private void setEventManagement()
    {

        /*
         * NDP: BAD! Keeps focus on componant ==> cursor can never leave!
         * theTextComponent.addFocusListener(new FocusAdapter() { public void
         * focusLost(FocusEvent e) { theTextComponent.requestFocus(); } });
         */

        theTextComponent.getInputMap()
                .put(
                        KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,
                                InputEvent.CTRL_MASK), "controlEspace");
        theTextComponent.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_HOME, InputEvent.CTRL_MASK),
                "home");
        theTextComponent.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_END, InputEvent.CTRL_MASK),
                "end");

//         theTextComponent.getActionMap().put("controlEspace", new
//         AbstractAction() {
//         public void actionPerformed(ActionEvent e) {
//         onControlSpace();
//         }
//         });
        
        
        theTextComponent.addKeyListener(new KeyAdapter()
        {
            
            @Override
            public void keyPressed(KeyEvent e)
            {
                int theKey = e.getKeyCode();

                // if ((theKey >= KeyEvent.VK_A && theKey <= KeyEvent.VK_Z) ||
                // theKey == KeyEvent.VK_PERIOD)
                if (theKey >= KeyEvent.VK_A && theKey <= KeyEvent.VK_Z)
                {                    
                     onControlSpace();
                }

            }
        });

        theTextComponent.getActionMap().put("home", new AbstractAction()
        {
			private static final long serialVersionUID = -1372095964257826576L;

			public void actionPerformed(ActionEvent e)
            {
                theWordMenu.moveStart();
            }
        });
        theTextComponent.getActionMap().put("end", new AbstractAction()
        {
			private static final long serialVersionUID = -4698429261639389935L;

			public void actionPerformed(ActionEvent e)
            {
                theWordMenu.moveEnd();
            }
        });
        theTextComponent.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e)
            {
                super.mouseClicked(e);
                if (theWordMenu.isVisible())
                {
                    theWordMenu.setVisible(false);
                }
            }
        });
        theTextComponent.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                if (e.isConsumed())
                    return;
                
                if (theWordMenu.isVisible())
                {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    {
                        theWordMenu.onSelected();
                        e.consume();
                    }
                    else if (e.getKeyCode() == KeyEvent.VK_DOWN)
                    {
                        theWordMenu.moveDown();
                        e.consume();
                    }
                    else if (e.getKeyCode() == KeyEvent.VK_UP)
                    {
                        theWordMenu.moveUp();
                        e.consume();
                    }
                    else if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN)
                    {
                        theWordMenu.movePageDown();
                        e.consume();
                    }
                    else if (e.getKeyCode() == KeyEvent.VK_PAGE_UP)
                    {
                        theWordMenu.movePageUp();
                        e.consume();
                    }
                }
            }
        });
        theOwner.addComponentListener(new ComponentAdapter()
        {
            public void componentHidden(ComponentEvent e)
            {
                theWordMenu.setVisible(false);
            }

            public void componentMoved(ComponentEvent e)
            {
                if (theWordMenu.isVisible())
                {
                    theWordMenu.move();
                }
            }
        });
        theTextComponent.getDocument().addDocumentListener(
                new DocumentListener()
                {
                    public void insertUpdate(DocumentEvent e)
                    {
                        if (theWordMenu.isVisible())
                        {
                            int beginIndex = e.getOffset();
                            int endIndex = beginIndex + e.getLength();
                            String newCharacters = theTextComponent.getText()
                                    .substring(beginIndex, endIndex);
                            for (int i = 0; i < WORD_SEPARATORS.length; i++)
                            {
                                if (newCharacters.indexOf(WORD_SEPARATORS[i]) != -1)
                                {
                                    theCurrentWord.setBounds(-1, 0);
                                    theWordMenu.setWords(null);
                                    theWordMenu.setVisible(false);
                                    return;
                                }
                            }
                            theCurrentWord.increaseLength(e.getLength());
                            updateMenu();
                        }
                    }

                    public void removeUpdate(DocumentEvent e)
                    {
                        if (theWordMenu.isVisible())
                        {
                            theCurrentWord.decreaseLength(e.getLength());
                            if (theCurrentWord.getLength() == 0)
                            {
                                theWordMenu.setWords(null);
                                theWordMenu.setVisible(false);
                                return;
                            }
                            updateMenu();
                        }
                    }

                    public void changedUpdate(DocumentEvent e)
                    {
                    }
                });
    }

    private String[] getWords(String aWord)
    {

        // 17/05/10 17:10 - NDP: PowerEditor: Test word presence with all in
        // lowercase() ==> caps independant

        aWord = aWord.trim(); // SAFELOGIC HACK

        debug("aWord.length(): " + aWord.length());
                
        Set<String> returnSet = new TreeSet<String> ();
        
        for (Iterator<String>  iterator = theSet.iterator(); iterator.hasNext();)
        {
            String string = (String) iterator.next();
            
            // if (string.startsWith(aWord)) {
            // returnSet.add(string);
            // }

            // SAFELOGIC HACK
            if (string.toLowerCase().startsWith(aWord.toLowerCase()))
            {
                returnSet.add(string);
            }
        }
           
        return (String[]) returnSet.toArray(new String[0]);
    }

    private static boolean isWordSeparator(char aChar)
    {
        for (int i = 0; i < WORD_SEPARATORS.length; i++)
        {
            if (aChar == WORD_SEPARATORS[i])
                return true;
        }
        return false;
    }

    private void onControlSpace()
    {
        theCurrentWord = getCurrentTypedWord();
        if (theCurrentWord.getLength() == 0)
            return;
        int index = theCurrentWord.getStart();
        Rectangle rect = null;
        try
        {
            rect = theTextComponent.getUI()
                    .modelToView(theTextComponent, index);
        }
        catch (BadLocationException e)
        {
        }
        
        debug("rect.height: " + rect.height);
        debug("index      : " + index);
        
        if (rect == null)
            return;

        theWordMenu.theList.setSelectedIndex(0);
        theWordMenu.display(new Point(rect.x, rect.y + rect.height));
        
        words = null;
        
        updateMenu();
                
        // HACK SafeLogic
        if ( words != null && words.length > 0)
        {            
            if (!theWordMenu.isVisible())            
            {
                theWordMenu.setVisible(true);
            }
            
            theTextComponent.requestFocus();
        }
        
    }

    // HACK SAFELOGIC
    private String[] words = null;
    
    private void updateMenu()
    {
        if (theCurrentWord.getLength() == 0)
            return;
        words = getWords(theCurrentWord.toString());
                
        theWordMenu.setWords(words);
    }

    private Word getCurrentTypedWord()
    {
        Word word = new Word();
        int position = theTextComponent.getCaretPosition();
        if (position == 0)
            return word;
        int index = position - 1;
        boolean found = false;
        while ((index > 0) && (!found))
        {
            char current = theTextComponent.getText().charAt(index);
            if (isWordSeparator(current))
            {
                found = true;
                index++;
            }
            else
            {
                index--;
            }
        }
        word.setBounds(index, position - index);
        return word;
    }

    private void setCurrentTypedWord(String aWord)
    {
        theWordMenu.setVisible(false);
        if (aWord != null)
        {
            if (aWord.length() > theCurrentWord.getLength())
            {
                String newLetters = aWord.substring(theCurrentWord.getLength());
                try
                {
                    theTextComponent.getDocument().insertString(
                            theCurrentWord.getEnd(), newLetters, null);
                }
                catch (BadLocationException e)
                {
                }
                theCurrentWord.increaseLength(newLetters.length());
            }
        }
        theTextComponent.requestFocus();
        theTextComponent.setCaretPosition(theCurrentWord.getEnd());
        theCurrentWord.setBounds(-1, 0);
    }

    private static Set<String> loadLexiconFromFile(File aFile)
            throws IOException
    {
        Set<String> returnSet = new TreeSet<String>();
        BufferedReader reader = new BufferedReader(new FileReader(aFile));
        String line = reader.readLine();
        while (line != null)
        {
            returnSet.add(line);
            line = reader.readLine();
        }
        reader.close();
        return returnSet;
    }

    /**
     * debug tool
     */
    private void debug(String s)
    {
        if (DEBUG)
        {
            System.out.println(s);
        }
    }

    
    public static void main(String[] args) throws Exception
    {
        final JFrame frame = new JFrame("Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Set<String> lexicon = null;
        
        File lexiconFile = new File("c:\\temp\\real_addr_list_1.txt");
        lexicon = loadLexiconFromFile(lexiconFile);

        /*
        lexicon = new TreeSet<String>();
        lexicon.add("Nico de Pom");
        lexicon.add("Ed");
        lexicon.add("Franklin");
        lexicon.add("Franklin Servan Schreiber");
        lexicon.add("Alexandre");
        */
        
        JTextPane textArea = new JTextPane();
        PowerEditor powerEditor = new PowerEditor(lexicon, frame, textArea);

        JScrollPane scrollpane = new JScrollPane(powerEditor);
        scrollpane.setBorder(new CompoundBorder(BorderFactory
                .createEmptyBorder(10, 10, 10, 10), BorderFactory
                .createBevelBorder(BevelBorder.LOWERED)));
        frame.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });

        frame.setContentPane(scrollpane);
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                frame.setSize(500, 500);
                frame.setVisible(true);
            }
        });

    }
}

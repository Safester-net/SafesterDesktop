/*
Definitive Guide to Swing for Java 2, Second Edition
By John Zukowski     
ISBN: 1-893115-78-X
Publisher: APress
*/

import java.awt.Container;
import java.awt.Frame;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;

public class EditorPaneSample {
  public static void main(String args[]) throws IOException {
    JFrame frame = new JFrame("EditorPane Example");
    Container content = frame.getContentPane();

    JEditorPane editorPane = new JEditorPane("http://www.apress.com");
    editorPane.setEditable(false);

    HyperlinkListener hyperlinkListener = new ActivatedHyperlinkListener(
        frame, editorPane);
    editorPane.addHyperlinkListener(hyperlinkListener);

    JScrollPane scrollPane = new JScrollPane(editorPane);
    content.add(scrollPane);

    frame.setSize(640, 480);
    frame.setVisible(true);
  }
}

class ActivatedHyperlinkListener implements HyperlinkListener {

  Frame frame;

  JEditorPane editorPane;

  public ActivatedHyperlinkListener(Frame frame, JEditorPane editorPane) {
    this.frame = frame;
    this.editorPane = editorPane;
  }

  public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
    HyperlinkEvent.EventType type = hyperlinkEvent.getEventType();
    final URL url = hyperlinkEvent.getURL();
    if (type == HyperlinkEvent.EventType.ENTERED) {
      System.out.println("URL: " + url);
    } else if (type == HyperlinkEvent.EventType.ACTIVATED) {
      System.out.println("Activated");
      Runnable runner = new Runnable() {
        public void run() {
          // Retain reference to original
          Document doc = editorPane.getDocument();
          try {
            editorPane.setPage(url);
          } catch (IOException ioException) {
            JOptionPane.showMessageDialog(frame,
                "Error following link", "Invalid link",
                JOptionPane.ERROR_MESSAGE);
            editorPane.setDocument(doc);
          }
        }
      };
      SwingUtilities.invokeLater(runner);
    }
  }
}

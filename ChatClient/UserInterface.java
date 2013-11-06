/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 


/*
 * TextSamplerDemo.java requires the following files:
 *   TextSamplerDemoHelp.html (which references images/dukeWaveRed.gif)
 *   images/Pig.gif
 *   images/sound.gif
 */

import java.awt.BorderLayout;              //for layout managers and more
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;        //for action events
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class UserInterface extends JPanel
                             implements ActionListener {
	
    protected static final String textFieldString = "Enter: ";
    protected JLabel actionLabel;
    protected JEditorPane editorPane;
    private static BlockingQueue<String> messageQueue = new LinkedBlockingQueue<String>();
   
	static String currentRoom = null;
	static boolean inChatRoom = false;

    public UserInterface() {
    	
        setLayout(new BorderLayout());

        //Create a regular text field.
        JTextField textField = new JTextField(10);
        textField.setActionCommand(textFieldString);
        textField.addActionListener(this);

        //Create a formatted text field.
        JFormattedTextField ftf = new JFormattedTextField(
                java.util.Calendar.getInstance().getTime());
        ftf.setActionCommand(textFieldString);
        ftf.addActionListener(this);

        //Create some labels for the fields.
        JLabel textFieldLabel = new JLabel(textFieldString);
        textFieldLabel.setLabelFor(textField);

        //Create a label to put messages during an action event.
        actionLabel = new JLabel("Type chat message in the field and press Enter.");
        actionLabel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));

        //Lay out the text controls and the labels.
        JPanel textControlsPane = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        textControlsPane.setLayout(gridbag);

        JLabel[] labels = {textFieldLabel};
        JTextField[] textFields = {textField, ftf};
        addLabelTextRows(labels, textFields, gridbag, textControlsPane);

        c.gridwidth = GridBagConstraints.REMAINDER; //last
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 1.0;
        textControlsPane.add(actionLabel, c);
        textControlsPane.setBorder(
                BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Chat Message Input"),
                                BorderFactory.createEmptyBorder(5,5,5,5)));

        //Create an editor pane.
        editorPane = new JEditorPane();
        editorPane.setEditable(false);
        JScrollPane editorScrollPane = new JScrollPane(editorPane);
        editorScrollPane.setVerticalScrollBarPolicy(
                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        editorScrollPane.setPreferredSize(new Dimension(300, 500));
        editorScrollPane.setMinimumSize(new Dimension(50, 50));

        //Put the editor pane and the text pane in a split pane.
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
        										editorScrollPane,
        										textControlsPane);
      //  splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(1);
        JPanel pane = new JPanel(new GridLayout(1,0));
        pane.add(splitPane);
        pane.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Messages"),
                        BorderFactory.createEmptyBorder(5,5,5,5)));


        //Put everything together.
        
        add(pane, BorderLayout.LINE_START);
        
        new Thread(new Runnable () {
			@Override
			public void run() {
				while (true) {
					try {
						addMessage(messageQueue.take());
					} catch (InterruptedException e) {
						System.err.println("InterruptedException found while waiting to print message in UI.");
					}
				}
			}
		}).start();
    }

    private void addLabelTextRows(JLabel[] labels,
                                  JTextField[] textFields,
                                  GridBagLayout gridbag,
                                  Container container) {
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.EAST;
        int numLabels = labels.length;

        for (int i = 0; i < numLabels; i++) {
            c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
            c.fill = GridBagConstraints.NONE;      //reset to default
            c.weightx = 0.0;                       //reset to default
            container.add(labels[i], c);

            c.gridwidth = GridBagConstraints.REMAINDER;     //end row
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
            container.add(textFields[i], c);
        }
    }

    public void actionPerformed(ActionEvent e) {
      /*  String prefix = "You typed \"";
        if (textFieldString.equals(e.getActionCommand())) {
            JTextField source = (JTextField)e.getSource();
            actionLabel.setText(prefix + source.getText() + "\"");
        }
        */
    	JTextField source = (JTextField) e.getSource();
    	String text = source.getText();
		source.setText("");
    	if (text.length() != 0) {
    		
    		if (inChatRoom) {
				try {
					client.x.sendMessage(currentRoom, text);
				} catch (RemoteException e1) {
					System.out
							.println("Found remore exception when trying to send message to chat room");
				}
    		} else {
    			JOptionPane.showMessageDialog(this, "You must select a room first!");
    		}
    		
    	}
    }

    protected void addStylesToDocument(StyledDocument doc) {
        //Initialize some styles.
        Style def = StyleContext.getDefaultStyleContext().
                        getStyle(StyleContext.DEFAULT_STYLE);

        Style regular = doc.addStyle("regular", def);
        StyleConstants.setFontFamily(def, "SansSerif");

        Style s = doc.addStyle("italic", regular);
        StyleConstants.setItalic(s, true);

        s = doc.addStyle("bold", regular);
        StyleConstants.setBold(s, true);

        s = doc.addStyle("small", regular);
        StyleConstants.setFontSize(s, 10);

        s = doc.addStyle("large", regular);
        StyleConstants.setFontSize(s, 16);
        
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    protected static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Chat Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add content to the window.
        frame.add(new UserInterface());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    /*
    public static void main(String[] args) {
        //Schedule a job for the event dispatching thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                 //Turn off metal's use of bold fonts
		UIManager.put("swing.boldMetal", Boolean.FALSE);
		createAndShowGUI();
            }
        });
    }
    */
    
    public void addMessage (String message) {
    	editorPane.setText(editorPane.getText() + message + "\n");
    }
    
    static void queueMessage (String message) {
    	try {
			messageQueue.put(message);
		} catch (InterruptedException e) {
			System.err.println("InterruptedException found while waiting to get message in UI.");
		}
    }
}


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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class UserInterface extends JPanel
                             implements ActionListener {
	
    protected static final String titleString = "Title";
    protected static final String descriptionString = "Description";
    protected static final String addMemberString = "AddMember";
    
    protected static final String addAllButtonString = "Add All";
    protected static final String addButtonString = "Add";
    protected static final String sendButtonString = "Send";

    protected static JEditorPane editorPane;
    protected JLabel actionLabel;

    public UserInterface() {
        setLayout(new BorderLayout());

        //Create a regular text field.
        JTextField textField1 = new JTextField(15);
        textField1.setActionCommand(titleString);
        textField1.addActionListener(this);
        
        //Create a regular text field.
        JTextField textField2 = new JTextField(20);
        textField2.setActionCommand(descriptionString);
        textField2.addActionListener(this);

        //Create the combo box, select item at index 4.
        //Indices start at 0, so 4 specifies the pig.
        JComboBox clientsListSelect = new JComboBox(User.clients);
        clientsListSelect.setSelectedIndex(0);
      	clientsListSelect.addActionListener(this);

      	// Create 3 buttons
      	JButton addAllButton = new JButton(addAllButtonString);
      	addAllButton.addActionListener(this);
      	JButton addButton = new JButton(addButtonString);
      	addButton.addActionListener(this);
      	JButton sendButton = new JButton(sendButtonString);
      	sendButton.addActionListener(this);
      	
        //Create some labels for the fields.
        JLabel titleLabel = new JLabel(titleString + ": ");
        titleLabel.setLabelFor(textField1);
        JLabel descriptionLabel = new JLabel(descriptionString + ": ");
        descriptionLabel.setLabelFor(textField2);
        JLabel addMemberLabel = new JLabel(addMemberString + ": ");
        addMemberLabel.setLabelFor(clientsListSelect);

        //Create a label to put messages during an action event.
        actionLabel = new JLabel("Type text in a field and press Enter.");
        actionLabel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));

        //Lay out the text controls and the labels.
        JPanel textControlsPane = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        textControlsPane.setLayout(gridbag);

        JLabel[] labels = {titleLabel, descriptionLabel};
        JTextField[] textFields = {textField1, textField2};
        addLabelTextRows(labels, textFields, gridbag, textControlsPane);

        c.gridwidth = GridBagConstraints.REMAINDER; //last
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 1.0;
        textControlsPane.add(actionLabel, c);
        textControlsPane.setBorder(
                BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Poll"),
                                BorderFactory.createEmptyBorder(5,5,5,5)));
        textControlsPane.add(clientsListSelect);
        textControlsPane.add(addAllButton);
        textControlsPane.add(addButton);
        textControlsPane.add(sendButton);

        //Create an editor pane.
        editorPane = createEditorPane();
        JScrollPane messagePane = new JScrollPane(editorPane);
        messagePane.setVerticalScrollBarPolicy(
                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        messagePane.setPreferredSize(new Dimension(250, 145));
        messagePane.setMinimumSize(new Dimension(10, 10));
        messagePane.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Messages"),
                        BorderFactory.createEmptyBorder(5,5,5,5)));

        // Create response panel
        JPanel responsePane = new JPanel();
        responsePane.setBorder(
                BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Response"),
                                BorderFactory.createEmptyBorder(5,5,5,5)));
        
        
        //Put the editor pane and the text pane in a split pane.
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
        									  textControlsPane,
                                              responsePane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0.5);
        JPanel inputsPane = new JPanel(new GridLayout(1,0));
        inputsPane.add(splitPane);
        inputsPane.setBorder(
                BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("User Inputs"),
                                BorderFactory.createEmptyBorder(5,5,5,5)));

        add(messagePane, BorderLayout.LINE_START);
        add(inputsPane, BorderLayout.LINE_END);
    }

    private void addLabelTextRows(JLabel[] labels,
                                  JTextField[] textFields,
                                  GridBagLayout gridbag,
                                  Container container) {
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
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
        String prefix = "You typed \"";
        if (titleString.equals(e.getActionCommand())) {
            JTextField source = (JTextField)e.getSource();
            actionLabel.setText(prefix + source.getText() + "\"");
        }
    }

    private JEditorPane createEditorPane() {
        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false);
        return editorPane;
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Calendar Poll");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add content to the window.
        frame.add(new UserInterface());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    
    /**
     * This method is used to add a new message to the screen
     * @param message
     */
    public static void addMessage (String message) {
    	editorPane.setText(editorPane.getText() + message + "\n");
    }

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
}


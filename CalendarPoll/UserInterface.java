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
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;        //for action events
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

public class UserInterface extends JPanel
                             implements ActionListener {
	
    protected static final String titleString = "Title";
    protected static final String descriptionString = "Description";
    protected static final String addMemberString = "AddMember";
    
    protected static final String addAllButtonString = "Add All";
    protected static final String addButtonString = "Add";
    protected static final String sendButtonString = "Send";
    
    protected static final String loadOptionsPrompt = "Enter Poll Name: ";
    protected static final String loadOptionsString = "Load Options";
    protected static final String submitString = "Submit";
    
    JTextField titleTextField, descriptionTextField, pollNameTextField;
    
    JLabel loadOptionsLabel;
    JPanel availabilityPane;
    
    protected static JEditorPane editorPane;
    
    JComboBox<String> clientsListSelect;
    
    List<String> responses;
    List<String> availability;
    List<JRadioButton> yesRadioButtons;
    List<JRadioButton> maybeRadioButtons;
    List<JRadioButton> noRadioButtons;
    
    Set<String> recipients;
    String newPollName;
    Set<String> possibleTimes;

    public UserInterface() {
    	
    	recipients = new HashSet<String>();
    	possibleTimes = new HashSet<String>();
    	newPollName = "";
    	
    	availability = new ArrayList<String>();
    	
        setLayout(new BorderLayout());

        JPanel textControlsPane = createTextControlsPane();

        //Create an editor pane.
        editorPane = createEditorPane();
        JScrollPane messagePane = new JScrollPane(editorPane);
        messagePane.setVerticalScrollBarPolicy(
                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        messagePane.setPreferredSize(new Dimension(400, 400));
        messagePane.setMinimumSize(new Dimension(250, 145));
        messagePane.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Messages"),
                        BorderFactory.createEmptyBorder(5,5,5,5)));

        // Create response panel
        JPanel responsePane = createResponsePane();
        
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
    
    private JPanel createTextControlsPane() {
        //Create a regular text field.
        titleTextField = new JTextField(15);
        titleTextField.setActionCommand(titleString);
        titleTextField.addActionListener(this);
        
        //Create a regular text field.
        descriptionTextField = new JTextField(20);
        descriptionTextField.setActionCommand(descriptionString);
        descriptionTextField.addActionListener(this);

        //Create the combo box, select item at index 4.
        //Indices start at 0, so 4 specifies the pig.
        clientsListSelect = new JComboBox<String>(User.clients);
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
        titleLabel.setLabelFor(titleTextField);
        JLabel descriptionLabel = new JLabel(descriptionString + ": ");
        descriptionLabel.setLabelFor(descriptionTextField);
        JLabel addMemberLabel = new JLabel(addMemberString + ": ");
        addMemberLabel.setLabelFor(clientsListSelect);

        //Lay out the text controls and the labels.
        JPanel textControlsPane = new JPanel();
        GridLayout grid = new GridLayout(0, 2);
        grid.setHgap(5);
        grid.setVgap(5);
        
        textControlsPane.setLayout(grid);
        textControlsPane.setBorder(
                BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Poll"),
                                BorderFactory.createEmptyBorder(5,5,5,5)));
        
        textControlsPane.add(titleLabel);
        textControlsPane.add(titleTextField);
        textControlsPane.add(descriptionLabel);
        textControlsPane.add(descriptionTextField);
        textControlsPane.add(clientsListSelect);
        textControlsPane.add(addAllButton);
        textControlsPane.add(addButton);
        textControlsPane.add(sendButton);

        return textControlsPane;
    }

    private JPanel createResponsePane() {
    	
        //Create a regular text field.
        pollNameTextField = new JTextField(15);
        pollNameTextField.setActionCommand(loadOptionsString);
        pollNameTextField.addActionListener(this);

      	JButton loadOptionsButton = new JButton(loadOptionsString);
      	loadOptionsButton.addActionListener(this);
      	JButton submitButton = new JButton(submitString);
      	submitButton.addActionListener(this);
      	
        //Create some labels for the fields.
        loadOptionsLabel = new JLabel(loadOptionsPrompt);
        loadOptionsLabel.setLabelFor(pollNameTextField);
        
      //-------------------------------  
        
        availabilityPane = new JPanel();
        final GridLayout experimentLayout = new GridLayout(0,4);
        final JPanel availabilityComponents = new JPanel();
        availabilityComponents.setLayout(experimentLayout);
        availabilityComponents.setPreferredSize(new Dimension(250, 100));
        
        List<ButtonGroup> groups = new ArrayList<ButtonGroup>();
        yesRadioButtons = new ArrayList<JRadioButton>();
        maybeRadioButtons = new ArrayList<JRadioButton>();
        noRadioButtons = new ArrayList<JRadioButton>();
        
        for (int i = 0; i < availability.size(); i++) {
        	
        	availabilityComponents.add(new JLabel(availability.get(i)));
        	yesRadioButtons.add(new JRadioButton());
        	availabilityComponents.add(yesRadioButtons.get(i));
        	maybeRadioButtons.add(new JRadioButton());
        	availabilityComponents.add(maybeRadioButtons.get(i));
        	noRadioButtons.add(new JRadioButton());
        	noRadioButtons.get(i).setSelected(true);
        	availabilityComponents.add(noRadioButtons.get(i));
        	
        	groups.add(new ButtonGroup());
            groups.get(i).add(yesRadioButtons.get(i));
            groups.get(i).add(maybeRadioButtons.get(i));
            groups.get(i).add(noRadioButtons.get(i));
        }

        availabilityPane.add(availabilityComponents, BorderLayout.NORTH);
        availabilityPane.add(new JSeparator(), BorderLayout.CENTER);

      //--------------------------------------- 
        
    	
   	 // Create response panel
		JPanel responsePane = new JPanel();
		responsePane.setLayout(new BoxLayout(responsePane, BoxLayout.PAGE_AXIS));

		responsePane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Response"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		responsePane.setPreferredSize(new Dimension(500, 300));

		responsePane.add(loadOptionsLabel);
		responsePane.add(pollNameTextField);
        responsePane.add(availabilityPane);
        responsePane.add(loadOptionsButton);
        responsePane.add(submitButton);

        availabilityPane.setVisible(false);
       
        return responsePane;
    }

    public void actionPerformed(ActionEvent e) {
    	
        if (addButtonString.equals(e.getActionCommand())) {
        	executeAdd();
        } else if (addAllButtonString.equals(e.getActionCommand())) {
        	executeAddAll();
        } else if (sendButtonString.equals(e.getActionCommand())) {
        	executeSend();
        } else if (loadOptionsString.equals(e.getActionCommand())) {
        	executeLoadOptions();
        } else if (submitString.equals(e.getActionCommand())) {
        	availabilityPane.setVisible(false);
        	executeSubmit();
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
    static void createAndShowGUI() {
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
    
    private void executeAdd() {
    	
    	boolean itemsListReset = false;
    	
    	// Reset the container and poll name first
    	if (!newPollName.equals(titleTextField.getText())) {
    		newPollName = titleTextField.getText();
    		recipients = new HashSet<String>();
    		clientsListSelect.removeAllItems();
    		for (String client: User.clients){
    			clientsListSelect.addItem(client);
    		}
    		itemsListReset = true;
    	}
    	
    	// Get the item
    	String item;
    	try {
    		item = clientsListSelect.getSelectedItem().toString();
    	} catch (NullPointerException e) {
    		return;
    	}
    	
		if (!itemsListReset) {
			recipients.add(item);
			clientsListSelect.removeItem(item);
			addMessage("System: Added " + item + " to Poll '" + newPollName
					+ "'");
		}
    }
    
    private void executeAddAll() {
    	
    	int size = clientsListSelect.getItemCount();
    	if (size == 0) {
    		return;
    	}
    	for (int i = 0; i < size; i++) {
    		String item = clientsListSelect.getItemAt(0);
    		recipients.add(item);
    		clientsListSelect.removeItemAt(0);
    	}
    	addMessage("System: " + "Added all recipients to Poll '" + newPollName + "'");
    }
    
    private void executeSend() {
    	
    	String descr = descriptionTextField.getText();
    	// TODO Error Check for all parameters!
    	
    	// Create poll
    	List<String> myPossibleTimes = new ArrayList<String>();
    	myPossibleTimes.addAll(possibleTimes);
    	User.client.createPoll(newPollName, descr, myPossibleTimes);
    	
    	// Send poll
    	List<String> recipientsList = new ArrayList<String>();
    	recipientsList.addAll(recipients);
    	User.client.sendPoll(newPollName, recipientsList);
    	addMessage("System: Poll '" + newPollName + "' was sent to all specified recipients!");
    	
    	// Reset variables
    	newPollName = "";
    	recipients.clear();
    	possibleTimes.clear();
    	
    	// Reset UI
    	titleTextField.setText("");
    	descriptionTextField.setText("");
    	clientsListSelect.removeAllItems();
    	for (String client: User.clients){
			clientsListSelect.addItem(client);
		}
    	
    }
    
    private void executeLoadOptions() {
    	
    	if (pollNameTextField.getText().isEmpty()) {
    		return;
    	}
    	String poll_name = pollNameTextField.getText();
    	pollNameTextField.setText("");
    	
    	// TODO Get the possible meeting times for the poll, 
    	// or return with an appropriate message.
    	
        // TODO REMOVE!
    	// Load the options
        availability.clear();
        availability.add("Monday");
        availability.add("Tuesday");
        availability.add("Wednesday");
        availability.add("Thursday");
        availability.add("Friday");
        availability.add("Saturday");
        availability.add("Sunday");
    	availabilityPane.setVisible(true);
    	
    	loadOptionsLabel.setText("Options for: " + poll_name);
    	pollNameTextField.setVisible(false);
    }
    
    private void executeSubmit() {
    	
    	if (loadOptionsPrompt.equals(loadOptionsLabel.getText())) {
    		return;
    	}
    	JOptionPane.showMessageDialog(this, "In the Execute Submit method!");
    	loadOptionsLabel.setText(loadOptionsPrompt);
    	pollNameTextField.setVisible(true);
    }
    
}


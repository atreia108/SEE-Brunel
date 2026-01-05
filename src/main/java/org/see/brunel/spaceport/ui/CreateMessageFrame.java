package org.see.brunel.spaceport.ui;

import org.see.brunel.spaceport.SpaceportFederate;

import javax.swing.*;

public class CreateMessageFrame extends JFrame {
    private final SpaceportFederate federate;

    public CreateMessageFrame(SpaceportFederate federate) {
        super();
        this.federate = federate;

        setTitle("Create Message");
        setSize(500, 200);
        setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

        JPanel form = new JPanel(new SpringLayout());
        JLabel receiverLabel = new JLabel("Receiver: ", SwingConstants.TRAILING);
        JTextField receiverTextField = new JTextField();
        receiverLabel.setLabelFor(receiverTextField);
        form.add(receiverLabel);
        form.add(receiverTextField);

        JLabel messageTypeLabel = new JLabel("Type: ", SwingConstants.TRAILING);
        JComboBox<FederateMessageType> messageTypeComboBox = new JComboBox<>(
                new FederateMessageType[] {
                        FederateMessageType.BRUNEL_SPACEPORT_CABLECAR_LANDER_TOUCHDOWN,
                        FederateMessageType.BRUNEL_SPACEPORT_LANDER_REQUEST_DEPARTURE
                }
        );
        messageTypeLabel.setLabelFor(messageTypeComboBox);
        form.add(messageTypeLabel);
        form.add(messageTypeComboBox);

        JLabel contentLabel = new JLabel("Content: ", SwingConstants.TRAILING);
        JTextField contentTextField = new JTextField();
        contentLabel.setLabelFor(contentTextField);
        form.add(contentLabel);
        form.add(contentTextField);

        JButton sendButton = new JButton("Send message");
        sendButton.addActionListener(e -> {
            if (!receiverTextField.getText().isEmpty() && messageTypeComboBox.getSelectedItem() != null && !contentTextField.getText().isEmpty()) {
                boolean rtiOperation = federate.dispatchMessage(receiverTextField.getText(), messageTypeComboBox.getSelectedItem().toString(), contentTextField.getText());

                if (rtiOperation) {
                    JOptionPane.showMessageDialog(null,
                            "Acknowledgement made successfully. Message has been dispatched to the RTI.",
                            "Operation Success",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Acknowledgement could not be made. There was a problem with sending the interaction to the RTI.",
                            "Operation Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null,
                        "Acknowledgement could not be made. One or more fields may be missing.",
                        "Operation Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        add(form);
        add(sendButton);

        SpringUtilities.makeCompactGrid(form, 3, 2, 10, 10, 10, 10);
    }
}

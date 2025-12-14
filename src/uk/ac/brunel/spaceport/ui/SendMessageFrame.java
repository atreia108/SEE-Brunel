package uk.ac.brunel.spaceport.ui;

import uk.ac.brunel.spaceport.SpaceportSimulation;

import javax.swing.*;

public class SendMessageFrame extends JFrame {
    private final SpaceportSimulation simulator;

    public SendMessageFrame(SpaceportSimulation simulator) {
        super();

        this.simulator = simulator;

        setTitle("Create Message");
        setSize(500, 200);
        setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        JPanel form = new JPanel(new SpringLayout());

        JLabel receiverLabel = new JLabel("Receiver: ", SwingConstants.TRAILING);
        JTextField receiverTextField = new JTextField();
        receiverLabel.setLabelFor(receiverTextField);
        form.add(receiverLabel);
        form.add(receiverTextField);

        JLabel messageTypeLabel = new JLabel("Message: ", SwingConstants.TRAILING);
        JComboBox<FederateMessageType> messageTypeComboBox = new JComboBox<>(new FederateMessageType[]{
                FederateMessageType.BRUNEL_SPACEPORT_CABLECAR_LANDER_TOUCHDOWN,
                FederateMessageType.BRUNEL_SPACEPORT_LANDER_REQUEST_DEPARTURE
        });
        messageTypeLabel.setLabelFor(messageTypeComboBox);
        form.add(messageTypeLabel);
        form.add(messageTypeComboBox);

        JLabel contentLabel = new JLabel("Content: ", SwingConstants.TRAILING);
        JTextField contentTextField = new JTextField();
        contentLabel.setLabelFor(contentTextField);
        form.add(contentLabel);
        form.add(contentTextField);

        JButton sendButton = new JButton("Send Message");

        sendButton.addActionListener(e -> {
            if (SpaceportSimulation.isDebugModeEnabled()) {
                String messageTypeSelection = (messageTypeComboBox.getSelectedItem() == null) ? "null" : messageTypeComboBox.getSelectedItem().toString();
                printDebugData(receiverTextField.getText(), messageTypeSelection, contentTextField.getText());
            }

            if (!receiverTextField.getText().isEmpty() && messageTypeComboBox.getSelectedItem() != null && !contentTextField.getText().isEmpty()) {
                boolean rtiOperation = simulator.sendHLAInteraction(receiverTextField.getText(), messageTypeComboBox.getSelectedItem().toString(), contentTextField.getText());

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

    private void printDebugData(String receiverText, String messageTypeText, String contentText) {
        System.out.println("An attempt to send the following message was made:");
        System.out.println("Receiver: " + receiverText);
        System.out.println("MessageType: " + messageTypeText);
        System.out.println("Content: " + contentText);
    }
}

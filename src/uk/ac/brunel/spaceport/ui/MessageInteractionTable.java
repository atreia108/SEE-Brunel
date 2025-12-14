package uk.ac.brunel.spaceport.ui;

import uk.ac.brunel.spaceport.SpaceportSimulation;

import javax.swing.*;
import java.awt.*;

public class MessageInteractionTable extends JPanel {
    private final MessageInteractionTableModel tableModel;

    private JTextField receiverTextField;
    private JComboBox<FederateMessageType> messageTypeComboBox;
    private JLabel contentTextFieldLabel;
    private JTextField contentTextField;
    private JLabel launchPadLabel;
    private JComboBox<String> launchPadComboBox;
    private JButton acknowledgementButton;

    private final SpaceportSimulation simulator;

    public MessageInteractionTable(SpaceportSimulation simulator) {
        super();

        this.simulator = simulator;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        tableModel = new MessageInteractionTableModel();

        JTable table = new JTable(tableModel);
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.getSelectionModel().addListSelectionListener(
                e -> {
                    receiverTextField.setEnabled(true);
                    messageTypeComboBox.setEnabled(true);
                    contentTextField.setEnabled(true);
                    acknowledgementButton.setEnabled(true);
                }
        );

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);

        // Create the form
        JPanel form = new JPanel(new SpringLayout());

        JLabel receiverLabel = new JLabel("Receiver:", SwingConstants.TRAILING);
        receiverTextField = new JTextField();
        receiverTextField.setEnabled(false);
        receiverLabel.setLabelFor(receiverTextField);
        form.add(receiverLabel);
        form.add(receiverTextField);

        JLabel messageTypeLabel = new JLabel("Message Type:", SwingConstants.TRAILING);
        messageTypeComboBox = new JComboBox<>(new FederateMessageType[] {
                FederateMessageType.BRUNEL_SPACEPORT_BEACON_ACKNOWLEDGED,
                FederateMessageType.BRUNEL_SPACEPORT_LANDER_ARRIVAL_ACKNOWLEDGED,
                FederateMessageType.BRUNEL_SPACEPORT_LANDER_TOUCHDOWN_ACKNOWLEDGED,
                FederateMessageType.BRUNEL_LANDER_SPACEPORT_DEPARTURE_COMPLETED
        });

        messageTypeComboBox.addActionListener(e -> {
            if (messageTypeComboBox.getSelectedItem() != null && messageTypeComboBox.getSelectedItem().equals(FederateMessageType.BRUNEL_SPACEPORT_LANDER_ARRIVAL_ACKNOWLEDGED)) {
                configureLaunchPadComboBox();
                launchPadLabel.setEnabled(true);
                // launchPadComboBox.setEnabled(true);

                contentTextFieldLabel.setEnabled(false);
                contentTextField.setEnabled(false);
            } else {
                launchPadLabel.setEnabled(false);
                launchPadComboBox.setEnabled(false);

                contentTextFieldLabel.setEnabled(true);
                contentTextField.setEnabled(true);
            }
        });

        messageTypeComboBox.setEnabled(false);
        messageTypeLabel.setLabelFor(messageTypeComboBox);
        form.add(messageTypeLabel);
        form.add(messageTypeComboBox);

        contentTextFieldLabel = new JLabel("Content:", SwingConstants.TRAILING);
        contentTextField = new JTextField();
        contentTextField.setEnabled(false);
        contentTextFieldLabel.setLabelFor(contentTextField);
        form.add(contentTextFieldLabel);
        form.add(contentTextField);

        launchPadLabel = new JLabel("Launch Pad:", SwingConstants.TRAILING);
        launchPadLabel.setEnabled(false);
        launchPadComboBox = new JComboBox<>();
        launchPadComboBox.setEnabled(false);
        launchPadLabel.setLabelFor(launchPadComboBox);
        form.add(launchPadLabel);
        form.add(launchPadComboBox);

        SpringUtilities.makeCompactGrid(form, 4, 2, 10, 10, 10, 10);
        add(form);

        acknowledgementButton = new JButton("Acknowledge");
        acknowledgementButton.setEnabled(false);

        acknowledgementButton.addActionListener(e -> {
            if (SpaceportSimulation.isDebugModeEnabled()) {
                String messageTypeSelection = (messageTypeComboBox.getSelectedItem() == null) ? "null" : messageTypeComboBox.getSelectedItem().toString();
                String launchPadSelection = (launchPadComboBox.getSelectedItem() == null) ? "null" : launchPadComboBox.getSelectedItem().toString();
                printDebugData(receiverTextField.getText(), messageTypeSelection, contentTextField.getText(), launchPadSelection);
            }

            if (!receiverTextField.getText().isEmpty()
                   && messageTypeComboBox.getSelectedItem() != null && messageTypeComboBox.getSelectedItem().equals(FederateMessageType.BRUNEL_SPACEPORT_LANDER_ARRIVAL_ACKNOWLEDGED)
                   && launchPadComboBox.getSelectedItem() != null) {

                boolean allocOperation = simulator.allocLaunchPad(launchPadComboBox.getSelectedItem().toString(), receiverTextField.getText());
                boolean rtiOperation = simulator.sendHLAInteraction(receiverTextField.getText(), messageTypeComboBox.getSelectedItem().toString(), launchPadComboBox.getSelectedItem().toString());

                if (allocOperation && rtiOperation) {
                    configureLaunchPadComboBox();

                    int modelRow = table.convertRowIndexToModel(table.getSelectedRow());
                    tableModel.removeRow(modelRow);

                    resetForm();
                    defocusForm();

                    JOptionPane.showMessageDialog(null,
                            "Acknowledgement made successfully. Message has been dispatched to the RTI.",
                            "Operation Success",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    resetForm();
                    JOptionPane.showMessageDialog(null,
                            "Acknowledgement could not be made. Error trying to allocate a launch pad.",
                            "Operation Failed",
                            JOptionPane.ERROR_MESSAGE);
                }

           } else if (!receiverTextField.getText().isEmpty() && messageTypeComboBox.getSelectedItem() != null && !contentTextField.getText().isEmpty()) {
                boolean rtiOperation = simulator.sendHLAInteraction(receiverTextField.getText(), messageTypeComboBox.getSelectedItem().toString(), contentTextField.getText());
                boolean deallocOperation = true;

                if (messageTypeComboBox.getSelectedItem().equals(FederateMessageType.BRUNEL_LANDER_SPACEPORT_DEPARTURE_COMPLETED)) {
                    String landerName = receiverTextField.getText();
                    deallocOperation = simulator.deallocLaunchPad(landerName);
                }

                if (deallocOperation && rtiOperation) {
                    configureLaunchPadComboBox();

                    int modelRow = table.convertRowIndexToModel(table.getSelectedRow());
                    tableModel.removeRow(modelRow);

                    resetForm();
                    defocusForm();

                    JOptionPane.showMessageDialog(null,
                            "Acknowledgement made successfully. Message has been dispatched to the RTI.",
                            "Operation Success",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Acknowledgement could not be made. One or more fields may be missing.",
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

        add(acknowledgementButton);
    }

    private void printDebugData(String receiverText, String messageTypeText, String contentText, String launchPadId) {
        System.out.println("The following acknowledgement attempt was made:");
        System.out.println("Receiver: " + receiverText);
        System.out.println("MessageType: " + messageTypeText);
        System.out.println("Content: " + contentText);
        System.out.println("Launch Pad: " + launchPadId);
    }

    public MessageInteractionTableModel getModel() {
        return tableModel;
    }

    public void configureLaunchPadComboBox() {
        launchPadComboBox.removeAllItems();

        if (simulator.getLaunchPadStatus(1))
            launchPadComboBox.addItem("LPAD_1");

        if (simulator.getLaunchPadStatus(2))
            launchPadComboBox.addItem("LPAD_2");

        launchPadComboBox.setEnabled(launchPadComboBox.getItemCount() > 0);
    }

    private void resetForm() {
        receiverTextField.setText("");
        messageTypeComboBox.setSelectedIndex(0);
        contentTextField.setText("");
    }

    private void defocusForm() {
        receiverTextField.setEnabled(false);
        messageTypeComboBox.setEnabled(false);
        contentTextField.setEnabled(false);
        acknowledgementButton.setEnabled(false);
    }

    /*
    public void showNotification(String landerName) {
        JOptionPane.showMessageDialog(null,
                landerName + "has touched down on the launch pad platform",
                "Notification",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
     */

    public void showNotification(String message) {
        JOptionPane.showMessageDialog(this, message, "Notification", JOptionPane.INFORMATION_MESSAGE);
    }
}

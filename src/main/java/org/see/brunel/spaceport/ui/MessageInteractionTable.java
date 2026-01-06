package org.see.brunel.spaceport.ui;

import org.see.brunel.models.objects.Spaceport;

import javax.swing.*;
import java.awt.*;

public class MessageInteractionTable extends JPanel {
    private final Spaceport spaceport;
    private final MessageInteractionTableModel tableModel;

    private JTextField receiverTextField;
    private JComboBox<FederateMessageType> messageTypeComboBox;
    private JLabel contentTextFieldLabel;
    private JTextField contentTextField;
    private JLabel launchPadLabel;
    private JComboBox<String> launchPadComboBox;
    private JButton acknowledgementButton;

    public MessageInteractionTable(Spaceport spaceport) {
        super();
        this.spaceport = spaceport;

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
            if (!receiverTextField.getText().isEmpty()
                    && messageTypeComboBox.getSelectedItem() != null && messageTypeComboBox.getSelectedItem().equals(FederateMessageType.BRUNEL_SPACEPORT_LANDER_ARRIVAL_ACKNOWLEDGED)
                    && launchPadComboBox.getSelectedItem() != null) {

                int padNumber = deriveLaunchPadNumber(launchPadComboBox.getSelectedItem().toString());
                spaceport.allocLaunchPad(padNumber, receiverTextField.getText());

                boolean rtiOperation = spaceport.dispatchMessage(receiverTextField.getText(), messageTypeComboBox.getSelectedItem().toString(), launchPadComboBox.getSelectedItem().toString());
                if (rtiOperation) {
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
                boolean rtiOperation = spaceport.dispatchMessage(receiverTextField.getText(), messageTypeComboBox.getSelectedItem().toString(), contentTextField.getText());

                if (messageTypeComboBox.getSelectedItem().equals(FederateMessageType.BRUNEL_LANDER_SPACEPORT_DEPARTURE_COMPLETED)) {
                    String landerName = receiverTextField.getText();
                    spaceport.evictLander(landerName);
                }

                if (rtiOperation) {
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

    private int deriveLaunchPadNumber(String padName) {
        String[] padNameSplit = padName.split("_");
        return Integer.parseInt(padNameSplit[1]);
    }

    public MessageInteractionTableModel getModel() {
        return tableModel;
    }

    private void configureLaunchPadComboBox() {
        launchPadComboBox.removeAllItems();

        if (!spaceport.isLaunchPadOccupied(1)) {
            launchPadComboBox.addItem("LPAD_1");
        }

        if (!spaceport.isLaunchPadOccupied(2)) {
            launchPadComboBox.addItem("LPAD_2");
        }

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

    public void showNotification(String message) {
        JOptionPane.showMessageDialog(this, message, "Notification", JOptionPane.INFORMATION_MESSAGE);
    }
}
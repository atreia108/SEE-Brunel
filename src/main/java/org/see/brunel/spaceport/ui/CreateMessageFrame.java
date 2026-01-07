/*-
 * Copyright (c) 2026 Hridyanshu Aatreya
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 */

package org.see.brunel.spaceport.ui;

import org.see.brunel.models.objects.Spaceport;

import javax.swing.*;

public class CreateMessageFrame extends JFrame {
    public CreateMessageFrame(Spaceport spaceport) {
        super();

        setTitle("Create message");
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

        JButton sendButton = new JButton("Send message");

        sendButton.addActionListener(e -> {
            if (!receiverTextField.getText().isEmpty() && messageTypeComboBox.getSelectedItem() != null && !contentTextField.getText().isEmpty()) {
                boolean rtiOperation = spaceport.dispatchMessage(receiverTextField.getText(), messageTypeComboBox.getSelectedItem().toString(), contentTextField.getText());

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
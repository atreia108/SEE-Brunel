package uk.ac.brunel.spaceport;

import com.badlogic.ashley.core.Entity;
import io.github.atreia108.vega.components.HLAInteractionComponent;
import io.github.atreia108.vega.core.ASpaceFomSimulation;
import io.github.atreia108.vega.core.HLAInteractionManager;
import io.github.atreia108.vega.core.HLAInteractionQueue;
import uk.ac.brunel.components.FederateMessageComponent;
import uk.ac.brunel.spaceport.ui.MessageInteractionTable;
import uk.ac.brunel.spaceport.ui.SendMessageFrame;
import uk.ac.brunel.utils.ComponentMappers;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SpaceportSimulation extends ASpaceFomSimulation {
    private static final boolean DEBUG_MODE = true;
    private static final String CONFIG_FILE = "resources/Spaceport.xml";

    private MessageInteractionTable tablePane;

    private final Map<Integer, String> landerPadMap = new HashMap<>();
    private final Map<String, Integer> padLanderMap = new HashMap<>();

    public SpaceportSimulation() {
        super(CONFIG_FILE);
        init();
    }

    public void createAndShowGUI() {
        try {
            for (UIManager.LookAndFeelInfo info: UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
        }

        JFrame frame = new JFrame("Spaceport Command Center");
        frame.setSize(1920, 1080);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        WindowListener closeWindow = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
            }
        };
        frame.addWindowListener(closeWindow);

        SendMessageFrame sendMessageFrame = new SendMessageFrame(this);
        sendMessageFrame.setVisible(false);

        JMenuBar menuBar = new JMenuBar();

        JMenu optionsMenu = new JMenu("Options");
        JMenuItem newMessage = new JMenuItem("Create Message");
        newMessage.addActionListener(e -> {
            sendMessageFrame.setVisible(true);
        });
        JMenuItem clearAll = new JMenuItem("Clear All");
        clearAll.addActionListener(e -> {
            tablePane.getModel().removeAllRows();
        });

        optionsMenu.add(newMessage);
        optionsMenu.add(clearAll);

        menuBar.add(optionsMenu);

        frame.setJMenuBar(menuBar);

        tablePane = new MessageInteractionTable(this);
        tablePane.setOpaque(true);
        frame.setContentPane(tablePane);

        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
    }

    @Override
    protected void onInit() {
        Runnable r = this::createAndShowGUI;
        SwingUtilities.invokeLater(r);
    }

    @Override
    protected void onRun() {
        updateCommandCenter();
    }

    private void updateCommandCenter() {
        ArrayList<Entity> federateMessageInteractions = HLAInteractionQueue.filter("HLAinteractionRoot.FederateMessage");

        for (Entity interaction : federateMessageInteractions) {
            FederateMessageComponent federateMessageComponent = ComponentMappers.federateMessage.get(interaction);

            if (!federateMessageComponent.sender.isEmpty() && !federateMessageComponent.receiver.isEmpty() && !federateMessageComponent.content.isEmpty() && !federateMessageComponent.type.isEmpty()) {
                if (federateMessageComponent.type.equals("BRUNEL_LANDER_SPACEPORT_DEPARTURE_COMPLETED")) {
                    String landerName = federateMessageComponent.sender;
                    deallocLaunchPad(landerName);
                }
                else if (federateMessageComponent.type.equals("BRUNEL_LANDER_SPACEPORT_TOUCHDOWN")) {
                    tablePane.showNotification(federateMessageComponent.sender);
                    System.out.println("show notification.");
                } else {
                    tablePane.getModel().addRow(federateMessageComponent.sender, federateMessageComponent.type, federateMessageComponent.content);
                }
            }
        }

        HLAInteractionQueue.free(federateMessageInteractions);
    }

    @Override
    protected void onShutdown() {
        // Do nothing.
    }

    public static boolean isDebugModeEnabled() {
        return DEBUG_MODE;
    }

    public boolean sendHLAInteraction(String receiverName, String messageType, String content) {
        Entity interaction = engine.createEntity();
        FederateMessageComponent federateMessageComponent = engine.createComponent(FederateMessageComponent.class);
        HLAInteractionComponent interactionComponent = engine.createComponent(HLAInteractionComponent.class);

        federateMessageComponent.sender = "Spaceport";
        federateMessageComponent.receiver = receiverName;
        federateMessageComponent.type = messageType;
        federateMessageComponent.content = content;

        interactionComponent.className = "HLAinteractionRoot.FederateMessage";

        interaction.add(federateMessageComponent);
        interaction.add(interactionComponent);

        return HLAInteractionManager.sendInteraction(interaction);
    }

    public boolean getLaunchPadStatus(int designation) {
        return !landerPadMap.containsKey(designation);
    }

    public boolean deallocLaunchPad(String landerName) {
        try {
            int designation = padLanderMap.get(landerName);
            landerPadMap.remove(designation);
            padLanderMap.remove(landerName);

            return true;
        } catch (NullPointerException ignored) {
            return false;
        }
    }

    public boolean allocLaunchPad(String padName, String landerName) {
        if (padName.equals("LPAD_1")) {
            landerPadMap.put(1, landerName);
            padLanderMap.put(landerName, 1);
            return true;
        } else if (padName.equals("LPAD_2")) {
            landerPadMap.put(2, landerName);
            padLanderMap.put(landerName, 2);
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        new SpaceportSimulation();
    }
}

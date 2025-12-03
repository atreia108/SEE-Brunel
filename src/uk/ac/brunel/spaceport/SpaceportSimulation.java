package uk.ac.brunel.spaceport;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import io.github.atreia108.vega.components.HLAInteractionComponent;
import io.github.atreia108.vega.core.ASpaceFomSimulation;
import io.github.atreia108.vega.core.HLAInteractionManager;
import io.github.atreia108.vega.core.HLAInteractionQueue;
import io.github.atreia108.vega.core.HLAObjectManager;
import io.github.atreia108.vega.utils.VegaUtilities;
import uk.ac.brunel.components.FederateMessageComponent;
import uk.ac.brunel.components.PositionComponent;
import uk.ac.brunel.spaceport.ui.MessageInteractionTable;
import uk.ac.brunel.spaceport.ui.SendMessageFrame;
import uk.ac.brunel.utils.ComponentMappers;
import uk.ac.brunel.utils.World;

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

    private static PooledEngine engine;

    private MessageInteractionTable tablePane;

    private Entity spaceport;
    private final Map<Integer, String> landerPadMap = new HashMap<>();
    private final Map<String, Integer> padLanderMap = new HashMap<>();
    private boolean spaceportAttributesSent = false;

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
        frame.setSize(1600, 900);
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
        optionsMenu.add(newMessage);
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
        engine = VegaUtilities.engine();
        World world = new World(engine);

        spaceport = world.createSpaceport("Spaceport", "Operational", "AitkenBasinLocalFixed", 0.0f, 0.0f, 0.0f);
        Runnable r = this::createAndShowGUI;
        SwingUtilities.invokeLater(r);
    }

    @Override
    protected void onRun() {
        if (!spaceportAttributesSent) {
            HLAObjectManager.sendInstanceUpdate(spaceport);
            spaceportAttributesSent = true;
        } else {
            PositionComponent positionComponent = ComponentMappers.position.get(spaceport);
            // positionComponent.pos.x += 1.0f;
            // positionComponent.pos.y += 1.0f;

            HLAObjectManager.sendInstanceUpdate(spaceport);
        }

        updateCommandCenter();

        /*
        Set<Entity> remoteEntities = HLAObjectManager.getAllRemoteEntities();
        System.out.println("Total Remote Entities: " + remoteEntities.size());

        int counter = 0;

        for (Entity remoteEntity : remoteEntities) {
            if (counter == 3) {
                break;
            } else {
                ++counter;
            }

            HLAObjectComponent objectComponent = VegaUtilities.objectComponentMapper().get(remoteEntity);

            if (objectComponent.className.equals("HLAobjectRoot.ReferenceFrame")) {
                PhysicalEntityComponent physicalEntityComponent = ComponentMappers.physicalEntity.get(remoteEntity);
                ReferenceFrameComponent referenceFrameComponent = ComponentMappers.frame.get(remoteEntity);
                PositionComponent positionComponent = ComponentMappers.position.get(remoteEntity);

                System.out.println("Reference Frame: " + physicalEntityComponent.name + ", Parent: " + referenceFrameComponent.name + ", Position: " + positionComponent.pos.x + ", " + positionComponent.pos.y + ", " + positionComponent.pos.z);
            }
        }
         */
    }

    private void updateCommandCenter() {
        ArrayList<Entity> federateMessageInteractions = HLAInteractionQueue.filter("HLAinteractionRoot.FederateMessage");

        for (Entity interaction : federateMessageInteractions) {
            FederateMessageComponent federateMessageComponent = ComponentMappers.federateMessage.get(interaction);

            if (!federateMessageComponent.sender.isEmpty() && !federateMessageComponent.receiver.isEmpty() && !federateMessageComponent.content.isEmpty() && !federateMessageComponent.type.isEmpty()) {
                tablePane.getModel().addRow(federateMessageComponent.sender,  federateMessageComponent.type, federateMessageComponent.content);
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
        if (spaceport == null) {
            return false;
        }

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

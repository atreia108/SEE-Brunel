package uk.ac.brunel.spaceport.ui;

import uk.ac.brunel.archetypes.FederateMessage;
import uk.ac.brunel.spaceport.SpaceportSimulation;

import javax.swing.table.AbstractTableModel;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MessageInteractionTableModel extends AbstractTableModel {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSSS");

    private final String[] columnNames = {"Time", "Sender", "MessageType", "Content"};

    private Object[][] legacyData = {
            // {formatter.format(LocalTime.now()), "lander_1", FederateMessageType.getValue(FederateMessageType.BRUNEL_LANDER_SPACEPORT_REQUEST_LANDING), "Greetings, I must land at once!"},
            // {formatter.format(LocalTime.now()), "lander_2", FederateMessageType.getValue(FederateMessageType.BRUNEL_LANDER_SPACEPORT_REQUEST_LANDING), "STC tower, we're closing in to spaceport."},
            // {formatter.format(LocalTime.now()), "lander_3", FederateMessageType.getValue(FederateMessageType.BRUNEL_LANDER_SPACEPORT_REQUEST_LANDING), "What's up doc? Need to land and refuel."},
            // {formatter.format(LocalTime.now()), "lander_1", FederateMessageType.getValue(FederateMessageType.BRUNEL_LANDER_SPACEPORT_NOTIFY_DEPARTURE), "Alas, I must now depart. Farewell!"}
    };

    private final ArrayList<ArrayList<Object>> data = new ArrayList<>();

    public MessageInteractionTableModel() {
        /*
        ArrayList<Object> row1 = new ArrayList<>();
        ArrayList<Object> row2 = new ArrayList<>();
        ArrayList<Object> row3 = new ArrayList<>();
        ArrayList<Object> row4 = new ArrayList<>();

        row1.add(legacyData[0][0]);
        row1.add(legacyData[0][1]);
        row1.add(legacyData[0][2]);
        row1.add(legacyData[0][3]);

        row2.add(legacyData[1][0]);
        row2.add(legacyData[1][1]);
        row2.add(legacyData[1][2]);
        row2.add(legacyData[1][3]);

        row3.add(legacyData[2][0]);
        row3.add(legacyData[2][1]);
        row3.add(legacyData[2][2]);
        row3.add(legacyData[2][3]);

        row4.add(legacyData[3][0]);
        row4.add(legacyData[3][1]);
        row4.add(legacyData[3][2]);
        row4.add(legacyData[3][3]);

        data.add(row1);
        data.add(row2);
        data.add(row3);
        data.add(row4);
         */
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (data.isEmpty()) {
            return null;
        } else {
            // return data[rowIndex][columnIndex];
            return data.get(rowIndex).get(columnIndex);
        }
    }

    public void setValueAt(Object value, int row, int column) {
        if (SpaceportSimulation.isDebugModeEnabled()) {
            System.out.println("Setting value at " + row + ", " + column
            + " to " + value
            + " (an instance of "
            + value.getClass() + ")");
        }

        // data[row][column] = value;
        data.get(row).set(column, value);
        fireTableCellUpdated(row, column);

        if (SpaceportSimulation.isDebugModeEnabled()) {
            System.out.println("New value of data:");
            printDebugData();
        }
    }

    private void printDebugData() {
        int numRows = getRowCount();
        int numCols = getColumnCount();

        for (int i = 0; i < numRows; i++) {
            System.out.println(" row " + i + ":");
            for (int j = 0; j < numCols; j++) {
                // System.out.println("  " + data[i][j]);
                System.out.println("  " + data.get(i).get(j));
            }
            System.out.println();
        }
        System.out.println("--------------------------");
    }

    @Override
    public Class<?> getColumnClass(int c) {
        // It's a table header (String) if the rows are empty.
        if (getRowCount() > 0) {
            return String.class;
        } else {
            return getValueAt(0, c).getClass();
        }
    }

    public void removeRow(int row) {
        data.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public void addRow(String sender, String messageType, String content) {
        ArrayList<Object> rowData = new ArrayList<>();
        rowData.add(formatter.format(LocalTime.now()));
        rowData.add(sender);
        rowData.add(messageType);
        rowData.add(content);

        data.add(rowData);
        fireTableRowsInserted(0, data.size() - 1);
    }
}

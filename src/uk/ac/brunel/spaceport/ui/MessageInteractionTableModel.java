package uk.ac.brunel.spaceport.ui;

import uk.ac.brunel.spaceport.SpaceportSimulation;

import javax.swing.table.AbstractTableModel;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MessageInteractionTableModel extends AbstractTableModel {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSSS");

    private final String[] columnNames = {"Time", "Sender", "MessageType", "Content"};

    private Object[][] legacyData = {};

    private final ArrayList<ArrayList<Object>> data = new ArrayList<>();

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

    public void removeAllRows() {
        data.clear();
        fireTableDataChanged();
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

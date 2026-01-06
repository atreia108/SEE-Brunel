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

import javax.swing.table.AbstractTableModel;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MessageInteractionTableModel extends AbstractTableModel {
    private final transient DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSSS");

    private final String[] columnNames = {"Time", "Sender", "MessageType", "Content"};

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
            return data.get(rowIndex).get(columnIndex);
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        data.get(row).set(column, value);
        fireTableCellUpdated(row, column);
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

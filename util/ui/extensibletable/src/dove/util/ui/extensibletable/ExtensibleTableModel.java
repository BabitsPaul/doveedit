package dove.util.ui.extensibletable;

import javax.swing.table.AbstractTableModel;
import java.util.Arrays;

/**
 * represents an extensible tablemodel
 * <p>
 * this tablemodel provides (in addition to
 * the default methods) methods for
 * removing/adding columns/rows
 */
public class ExtensibleTableModel
        extends AbstractTableModel {
    /**
     * the number of rows in this table
     */
    private int rows;

    /**
     * the number of columns in the model
     */
    private int cols;

    /**
     * contains the data of this table
     * <p>
     * representation: table[row][column]
     */
    private Object[][] table;

    /**
     * the typetable for the columns
     */
    private Class[] columnClasses;

    /**
     * the nametable for the columns
     */
    private String[] columnNames;

    /**
     * the editablelookuptable for this
     * tablemodel
     * <p>
     * representation: table[row][column]
     */
    private boolean[][] cellEditable;

    /**
     * creates a new tablemodel with the given
     * size
     * <p>
     * this columns are by default not named,
     * all types of data are allowed for all columnes,
     * all cells are editable
     *
     * @param rows number of rows in the generated table
     * @param cols number of columns in the generated table
     */
    public ExtensibleTableModel(int rows, int cols) {
        this(null, null, null, rows, cols);
    }

    /**
     * creates a new tablemodel with the given names, types, editability and size
     * incomplete data (null-arguments, too small or too big arrays) will automatically
     * be completed.
     * <p>
     * incomplete editability will be completed with cellEditable = true;
     * incomplete nametables will be filled up with ""
     * incomplete typetables will be filled up with Object.clas
     *
     * @param columnNames  the names of the columns
     * @param columnTypes  the type of the columns
     * @param cellEditable the editablility of cells
     * @param rows         the number of rows
     * @param cols         the number of columns
     */
    public ExtensibleTableModel(String[] columnNames, Class[] columnTypes,
                                boolean[][] cellEditable, int rows, int cols) {
        ///////////////////////////////////////
        // initialze table
        ///////////////////////////////////////

        this.rows = rows;
        this.cols = cols;

        this.table = new Object[rows][cols];

        /////////////////////////////////////////
        // initialize columns
        /////////////////////////////////////////

        String[] colNamesTemp = new String[cols];

        if (columnNames == null) {
            for (int i = 0; i < cols; i++)
                colNamesTemp[i] = "";
        }
        else if (columnNames.length < cols - 1) {
            int i;
            for (i = 0; i < columnNames.length; i++)
                colNamesTemp[i] = columnNames[i];

            for (; i < cols; i++)
                colNamesTemp[i] = columnNames[i];
        }
        else {
            System.arraycopy(columnNames, 0, colNamesTemp, 0, cols);
        }

        this.columnNames = colNamesTemp;

        /////////////////////////////////////////////
        // initialize typetable
        /////////////////////////////////////////////

        Class<?>[] colTypesTemp = new Class[cols];

        if (columnTypes == null) {
            for (int i = 0; i < cols; i++)
                colTypesTemp[i] = Object.class;
        }
        else if (columnTypes.length < cols - 1) {
            int i;
            for (i = 0; i < columnTypes.length; i++)
                colTypesTemp[i] = columnTypes[i];

            for (; i < colTypesTemp.length; i++)
                colTypesTemp[i] = Object.class;
        }
        else {
            System.arraycopy(columnTypes, 0, colTypesTemp, 0, cols);
        }

        columnClasses = colTypesTemp;

        ////////////////////////////////////////////////
        // initialize editable
        ////////////////////////////////////////////////

        boolean[][] cellEditableTemp = new boolean[rows][cols];

        if (cellEditable == null) {
            for (int i = 0; i < rows; i++)
                for (int j = 0; j < cols; j++)
                    cellEditableTemp[i][j] = true;
        }
        else {
            int i = 0;
            int j = 0;

            for (; i < cellEditable.length; i++) {
                for (j = 0; j < cellEditable[i].length; j++)
                    cellEditableTemp[i][j] = cellEditable[i][j];

                for (; j < cellEditableTemp[i].length; j++)
                    cellEditableTemp[i][j] = true;
            }
            for (; i < cellEditableTemp.length; i++)
                for (; j < cellEditableTemp[i].length; j++)
                    cellEditableTemp[i][j] = true;
        }

        this.cellEditable = cellEditableTemp;
    }

    //////////////////////////////////////////////////
    // data access
    //////////////////////////////////////////////////

    /**
     * returns the current number of rows in
     * the data of this table
     *
     * @return the number of rows in this table
     */
    @Override
    public int getRowCount() {
        return rows;
    }

    /**
     * returns the current number of columns in
     * the data of this table
     *
     * @return the number of columns in this table
     */
    @Override
    public int getColumnCount() {
        return cols;
    }

    /**
     * returns the element of the table at the given position
     *
     * @param rowIndex    the rowindex of the element
     * @param columnIndex the columnindex of the element
     * @return the element at the given position
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return table[rowIndex][columnIndex];
    }

    /**
     * checks the type of aValue and inserts it in
     * the table, if the type matches the columnType
     *
     * @param aValue the value to insert
     * @param row    the row to insert data
     * @param col    the column to insert data
     */
    @Override
    public void setValueAt(Object aValue, int row, int col) {
        if (!cellEditable[row][col])
            throw new IllegalStateException("Cell " + row + "/" + col + " is not editable");

        if (aValue != null && !columnClasses[col].isInstance(aValue))
            throw new ClassCastException("Invalid value - required: " + columnClasses[col].getCanonicalName() +
                    "given: " + aValue.getClass().getCanonicalName());

        table[row][col] = aValue;

        fireTableCellUpdated(row, col);
    }

    /**
     * inserts the given data
     * starting at the specified row and column
     * into the table
     *
     * @param nData data to insert
     * @param atRow startingrow
     * @param atCol startingcolumn
     */
    public void insertData(Object[][] nData, int atRow, int atCol) {
        int totalRowLengthIns = atRow + nData.length;
        int maxRowLengthIns = rows - atRow;

        int totalColLengthIns = atCol + nData[0].length;
        int maxColLengthIns = cols - atCol;

        int insertRowCount;
        int insertColCount;

        if (totalRowLengthIns > rows)
            insertRowCount = maxRowLengthIns;
        else
            insertRowCount = nData.length;

        if (totalColLengthIns > cols)
            insertColCount = maxColLengthIns;
        else
            insertColCount = nData[0].length;

        for (int i = 0; i < insertRowCount; i++)
            for (int j = 0; j < insertColCount; j++)
                setValueAt(nData[i][j], i + atRow, j + atCol);
    }

    ///////////////////////////////////////////////////////
    // names
    ///////////////////////////////////////////////////////

    /**
     * returns the name of the specified column
     *
     * @param column the columnindex of the requested column
     * @return the name of the specified column
     */
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    /**
     * sets the name of the specified column
     *
     * @param col  the column to change
     * @param name the new name of the column
     */
    public void setColumnName(int col, String name) {
        columnNames[col] = name;

        fireTableStructureChanged();
    }

    /**
     * searches a column with matching name
     *
     * @param columnName the columnname to search for
     * @return the columnindex or -1, if no matches are found
     */
    @Override
    public int findColumn(String columnName) {
        for (int i = 0; i < columnNames.length; i++)
            if (columnNames[i].equals(columnName))
                return i;

        return -1;
    }

    /////////////////////////////////////////////////////////
    // editing
    /////////////////////////////////////////////////////////

    /**
     * checks whether the given cell is editable
     *
     * @param rowIndex    the index of the row to check
     * @param columnIndex the index of the column to check
     * @return true, if the cell is editable
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return cellEditable[rowIndex][columnIndex];
    }

    /**
     * makes the specified cell un-/editable
     *
     * @param rowIndex    the row of the cell to make editable
     * @param columnIndex the index of the cell to make editable
     * @param editable    the new editability of the cell
     */
    public void setCellEditable(int rowIndex, int columnIndex, boolean editable) {
        cellEditable[rowIndex][columnIndex] = editable;

        fireTableStructureChanged();
    }

    /**
     * makes the specified column un-/editable
     *
     * @param col      the column to make editable
     * @param editable new editability of the row
     */
    public void setColumnEditable(int col, boolean editable) {
        for (int i = 0; i < rows; i++)
            cellEditable[i][col] = editable;

        fireTableStructureChanged();
    }

    /**
     * updates the editability of the specified table
     *
     * @param row      the row to update
     * @param editable the new editability of the row
     */
    public void setRowEditable(int row, boolean editable) {
        for (int i = 0; i < cols; i++)
            cellEditable[row][i] = editable;

        fireTableStructureChanged();
    }

    /**
     * makes the complete table un-/editable
     *
     * @param editable the new editability of the table
     */
    public void setAllEditable(boolean editable) {
        for (int i = 0; i < rows; i++)
            Arrays.fill(cellEditable[i], editable);

        fireTableStructureChanged();
    }

    ////////////////////////////////////////////////////////////
    // typesafety
    ////////////////////////////////////////////////////////////

    /**
     * returns the type of the specified column
     *
     * @param columnIndex the column for which the type is requested
     * @return the type of the column
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnClasses[columnIndex];
    }

    /**
     * overrides the columntype of a column
     * and checks whether all entrys match the
     * given new type
     *
     * @param col   the index of the row
     * @param clazz the new type of this row
     */
    public void setColumnType(int col, Class<?> clazz) {
        columnClasses[col] = clazz;

        for (int i = 0; i < rows; i++)
            if (!clazz.isInstance(table[col][rows]))
                throw new ClassCastException("Entry at " + i + "/" + col + " is no instance of given class");

        fireTableStructureChanged();
    }

    ////////////////////////////////////////////////////////////
    // tableextension
    ////////////////////////////////////////////////////////////

    /**
     * adds a new column after the  specified column
     * with the specified attributes
     *
     * @param afterColumn the column to insert after
     * @param name        the name of the new column
     * @param clazz       the type of the new column
     */
    public void addColumn(int afterColumn, String name, Class<?> clazz, Object[] nCol, boolean editable) {
        Object[][] tempData = new Object[rows][cols + 1];
        boolean[][] tempEditable = new boolean[rows][cols + 1];

        //move data to new tables and leave the specified column empty
        for (int i = 0; i < afterColumn; i++)
            for (int j = 0; j < rows; j++) {
                tempData[i][j] = table[i][j];
                tempEditable[i][j] = cellEditable[i][j];
            }

        for (int i = afterColumn + 1; i < cols + 1; i++)
            for (int j = 0; j < rows; j++) {
                tempData[j][i] = table[j][i - 1];
                tempEditable[j][i] = cellEditable[j][i];
            }

        //initialize the empty column with new data
        for (int i = 0; i < rows; i++) {
            tempData[i][afterColumn] = nCol[i];
            tempEditable[i][afterColumn] = editable;
        }

        table = tempData;
        cellEditable = tempEditable;

        Class<?>[] clazzTemp = new Class[cols + 1];
        String[] nameTemp = new String[cols + 1];

        System.arraycopy(columnClasses, 0, clazzTemp, 0, afterColumn);
        System.arraycopy(columnClasses, afterColumn + 1, clazzTemp, afterColumn, cols - afterColumn - 1);
        clazzTemp[afterColumn] = clazz;

        System.arraycopy(columnNames, 0, nameTemp, 0, afterColumn);
        System.arraycopy(columnNames, afterColumn + 1, nameTemp, afterColumn, cols - afterColumn - 1);
        nameTemp[afterColumn] = name;

        columnNames = nameTemp;
        columnClasses = clazzTemp;

        cols += 1;

        fireTableStructureChanged();
    }

    /**
     * removes the specified column and reduces the
     * arraysize to the new size
     *
     * @param col the column to remove
     */
    public void removeColumn(int col) {
        Object[][] tempTable = new Object[rows][cols - 1];
        boolean[][] tempEditable = new boolean[rows][cols - 1];

        for (int i = 0; i < rows; i++) {
            System.arraycopy(table[i], 0, tempTable[i], 0, col);
            System.arraycopy(table[i], col + 1, tempTable[i], col, cols - col - 1);

            System.arraycopy(cellEditable[i], 0, tempEditable[i], 0, col);
            System.arraycopy(cellEditable[i], col + 1, tempEditable[i], col, cols - col - 1);
        }

        table = tempTable;
        cellEditable = tempEditable;

        Class<?>[] clazzTemp = new Class[cols - 1];
        String[] nameTemp = new String[cols - 1];

        System.arraycopy(columnClasses, 0, clazzTemp, 0, col);
        System.arraycopy(columnClasses, col + 1, clazzTemp, col, cols - col - 1);

        System.arraycopy(columnNames, 0, nameTemp, 0, col);
        System.arraycopy(columnNames, col + 1, nameTemp, col, cols - col - 1);

        columnClasses = clazzTemp;
        columnNames = nameTemp;

        cols -= 1;

        fireTableStructureChanged();
    }

    /**
     * adds the specified row to the table
     * and makes all new cells editable
     * and fills them with null
     *
     * @param afterRow insert new row after this row
     * @param nRow the values to insert in the new row
     */
    public void addRow(int afterRow, Object[] nRow, boolean[] editability) {
        Object[][] tableTemp = new Object[rows + 1][cols];
        boolean[][] editableTemp = new boolean[rows + 1][cols];

        for (int i = 0; i < afterRow; i++) {
            tableTemp[i] = table[i];
            editableTemp[i] = cellEditable[i];
        }

        for (int i = afterRow + 1; i < rows + 1; i++) {
            tableTemp[i] = table[i - 1];
            editableTemp[i] = cellEditable[i - 1];
        }

        tableTemp[afterRow] = new Object[cols];

        System.arraycopy(editability, 0, editableTemp[afterRow], 0, cols);

        for (int i = 0; i < cols; i++) {
            if (!columnClasses[i].isInstance(nRow[i]))
                throw new ClassCastException("Invalid class - required: " + columnClasses[i].getCanonicalName() +
                        "actual argument: " + nRow[i].getClass().getCanonicalName());

            tableTemp[afterRow][i] = nRow[i];
        }

        table = tableTemp;
        cellEditable = editableTemp;

        rows += 1;

        fireTableStructureChanged();
    }

    /**
     * removes the specified row
     *
     * @param row the row to remove
     */
    public void removeRow(int row) {
        Object[][] tableTemp = new Object[rows - 1][cols];
        boolean[][] editableTemp = new boolean[rows - 1][cols];

        System.arraycopy(table, 0, tableTemp, 0, row);
        System.arraycopy(table, row + 1, tableTemp, row, rows - row - 1);

        System.arraycopy(cellEditable, 0, editableTemp, 0, row);
        System.arraycopy(cellEditable, row + 1, editableTemp, row, rows - row - 1);

        table = tableTemp;
        cellEditable = editableTemp;

        fireTableStructureChanged();
    }
}
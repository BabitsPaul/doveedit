package dove.util.ui.extensibletable;

import javax.swing.table.AbstractTableModel;

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
        if (aValue != null && !columnClasses[col].isInstance(aValue))
            throw new ClassCastException("Invalid value - required: " + columnClasses[col].getCanonicalName() +
                    "given: " + aValue.getClass().getCanonicalName());

        table[row][col] = aValue;
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
    public void makeCellEditable(int rowIndex, int columnIndex, boolean editable) {
        cellEditable[rowIndex][columnIndex] = editable;
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
    public void addColumn(int afterColumn, String name, Class<?> clazz) {

    }

    public void removeColumn(int col) {

    }

    public void addRow(int afterRow) {

    }

    public void removeRow(int row) {

    }
}
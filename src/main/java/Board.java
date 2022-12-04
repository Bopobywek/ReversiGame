import java.util.ArrayList;
import java.util.Comparator;
import java.util.Stack;

public class Board {
    private ArrayList<ArrayList<Cell>> board;
    private Stack<BoardSnapshot> stepsHistory = new Stack<>();
    private final int size;
    private boolean isPossibleStepsDisplay = false;
    public final int displaySize;

    public boolean isRestoreAllowed() {
        return isStepBackAllowed;
    }

    public void setStepBackAllowed(boolean stepBackAllowed) {
        isStepBackAllowed = stepBackAllowed;
    }

    private boolean isStepBackAllowed;
    private DiskColor diskColorWithDisplayedSteps = DiskColor.NONE;

    public Board(int size, boolean isStepBackAllowed) {
        this.isStepBackAllowed = isStepBackAllowed;
        board = new ArrayList<>();
        this.size = size;
        displaySize = size * 4 - 1;
        for (int i = 0; i < size; ++i) {
            ArrayList<Cell> row = new ArrayList<>(size);
            for (int j = 0; j < size; ++j) {
                row.add(new Cell(j, i, DiskColor.NONE));
            }
            board.add(row);
        }
    }

    public void initialize() {
        getCell(size / 2, size / 2).setDisk(DiskColor.WHITE);
        getCell(size / 2, size / 2 - 1).setDisk(DiskColor.BLACK);
        getCell(size / 2 - 1, size / 2).setDisk(DiskColor.BLACK);
        getCell(size / 2 - 1, size / 2 - 1).setDisk(DiskColor.WHITE);
    }

    public int getSize() {
        return size;
    }

    public void makeStep(int x, int y, DiskColor diskColor) {
        makeSnapshot();
        getCell(x, y).setDisk(diskColor);
        update(getCell(x, y), diskColor);
    }

    private void makeSnapshot() {
        ArrayList<ArrayList<Cell>> snapshot = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            ArrayList<Cell> row = new ArrayList<>(size);
            for (int j = 0; j < size; ++j) {
                row.add(new Cell(j, i, getCell(j, i).getDisk()));
            }
            snapshot.add(row);
        }
        stepsHistory.add(new BoardSnapshot(snapshot));
    }

    public Cell getCell(int x, int y) {
        return board.get(y).get(x);
    }

    private int getClosingCellIndex(ArrayList<Cell> row, int diskPosition, DiskColor diskColor) {
        int position = diskPosition - 1;
        while (position >= 0) {
            DiskColor diskColor1 = row.get(position).getDisk();
            if (diskColor == diskColor1 && diskPosition - position > 1) {
                return position;
            } else if (diskColor1 == DiskColor.NONE || (diskColor == diskColor1 && diskPosition - position <= 1)) {
                break;
            } else {
                position -= 1;
            }
        }

        int rowSize = row.size();
        position = diskPosition + 1;
        while (position < rowSize) {
            DiskColor diskColor1 = row.get(position).getDisk();
            if (diskColor == diskColor1 && position - diskPosition > 1) {
                return position;
            } else if (diskColor1 == DiskColor.NONE || (diskColor == diskColor1 && position - diskPosition <= 1)) {
                break;
            } else {
                position += 1;
            }
        }

        return -1;
    }

    private ArrayList<Cell> getHorizontalRow(int index) {
        return board.get(index);
    }

    private ArrayList<Cell> getVerticalRow(int index) {
        ArrayList<Cell> verticalRow = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            verticalRow.add(getCell(index, i));
        }

        return verticalRow;
    }

    /**
     *
     */
    public void restorePreviousStep() {
        if (!isStepBackAllowed || stepsHistory.isEmpty()) {
            return;
        }

        var snapshot = stepsHistory.pop();
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                getCell(j, i).setDisk(snapshot.board.get(i).get(j).getDisk());
            }
        }
    }

    private ArrayList<Cell> getDiagonalRow(int x, int y) {
        ArrayList<Cell> diagonalRow = new ArrayList<>();
        int startX = x - 1;
        int startY = y - 1;
        while (startY < size && startX < size && startX >= 0 && startY >= 0) {
            diagonalRow.add(getCell(startX, startY));
            --startX;
            --startY;
        }

        startX = x;
        startY = y;
        while (startY < size && startX < size && startX >= 0 && startY >= 0) {
            diagonalRow.add(getCell(startX, startY));
            ++startX;
            ++startY;
        }

        diagonalRow.sort(Comparator.comparingInt(Cell::getPositionY));
        return diagonalRow;
    }

    private ArrayList<Cell> getSideDiagonalRow(int x, int y) {
        int startX = x;
        int startY = y;
        ArrayList<Cell> diagonalRow = new ArrayList<>();
        while (startY < size && startX < size && startX >= 0 && startY >= 0) {
            diagonalRow.add(getCell(startX, startY));
            ++startX;
            --startY;
        }

        startX = x - 1;
        startY = y + 1;
        while (startY < size && startX < size && startX >= 0 && startY >= 0) {
            diagonalRow.add(getCell(startX, startY));
            --startX;
            ++startY;
        }

        diagonalRow.sort(Comparator.comparingInt(Cell::getPositionY));
        return diagonalRow;
    }

    public ArrayList<Cell> getClosedCells(Cell cell, DiskColor diskColor) {
        ArrayList<Cell> closedCells = new ArrayList<>();
        ArrayList<Cell> horizontalRow = getHorizontalRow(cell.getPositionY());
        int horizontalIndex = cell.getPositionX();

        ArrayList<Cell> verticalRow = getVerticalRow(cell.getPositionX());
        int verticalIndex = cell.getPositionY();

        ArrayList<Cell> diagonalRow = getDiagonalRow(cell.getPositionX(), cell.getPositionY());
        int diagonalIndex = getIndexInRow(diagonalRow, cell);

        ArrayList<Cell> sideDiagonalRow = getSideDiagonalRow(cell.getPositionX(), cell.getPositionY());
        int sideDiagonalIndex = getIndexInRow(sideDiagonalRow, cell);

        closedCells.addAll(getClosedCellsInRow(horizontalRow, horizontalIndex, diskColor));
        closedCells.addAll(getClosedCellsInRow(verticalRow, verticalIndex, diskColor));
        closedCells.addAll(getClosedCellsInRow(diagonalRow, diagonalIndex, diskColor));
        closedCells.addAll(getClosedCellsInRow(sideDiagonalRow, sideDiagonalIndex, diskColor));


        return closedCells;
    }

    private ArrayList<Cell> getClosedCellsInRow(ArrayList<Cell> row, int index, DiskColor diskColor) {
        ArrayList<Cell> closedCells = new ArrayList<>();
        int closingCellIndex = getClosingCellIndex(row, index, diskColor);
        if (closingCellIndex < 0) {
            return closedCells;
        }
        int from = Math.min(index, closingCellIndex);
        int to = Math.max(index, closingCellIndex);
        int i = from + 1;
        while (i != to) {
            closedCells.add(row.get(i));
            ++i;
        }

        return closedCells;
    }

    private int getIndexInRow(ArrayList<Cell> row, Cell cell) {
        int i = 0;
        for (Cell cell1: row) {
            if (cell1.getPositionX() == cell.getPositionX() && cell1.getPositionY() == cell.getPositionY()) {
                return i;
            }
            ++i;
        }

        return -1;
    }

    private boolean isPossibleStep(Cell cell, DiskColor diskColor) {
        ArrayList<Cell> horizontalRow = getHorizontalRow(cell.getPositionY());
        int horizontalIndex = cell.getPositionX();

        ArrayList<Cell> verticalRow = getVerticalRow(cell.getPositionX());
        int verticalIndex = cell.getPositionY();

        ArrayList<Cell> diagonalRow = getDiagonalRow(cell.getPositionX(), cell.getPositionY());
        int diagonalIndex = getIndexInRow(diagonalRow, cell);

        ArrayList<Cell> sideDiagonalRow = getSideDiagonalRow(cell.getPositionX(), cell.getPositionY());
        int sideDiagonalIndex = getIndexInRow(sideDiagonalRow, cell);

        return getClosingCellIndex(horizontalRow, horizontalIndex, diskColor) != -1
                || getClosingCellIndex(verticalRow, verticalIndex, diskColor) != -1
                || getClosingCellIndex(diagonalRow, diagonalIndex, diskColor) != -1
                || getClosingCellIndex(sideDiagonalRow, sideDiagonalIndex, diskColor) != -1;
    }

    public ArrayList<Cell> getAvailableSteps(DiskColor diskColor) {
        ArrayList<Cell> available = new ArrayList<>();
        if (diskColor == DiskColor.NONE) {
            return available;
        }

        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if (getCell(j, i).getDisk() != DiskColor.NONE) {
                    continue;
                }
                var cell = getCell(j, i);
                if (isPossibleStep(cell, diskColor)) {
                    available.add(cell);
                }
            }
        }

        return available;
    }

    private void updateRow(ArrayList<Cell> row, int diskPosition, DiskColor last) {
        int closingCellIndex = getClosingCellIndex(row, diskPosition, last);
        if (closingCellIndex < 0) {
            return;
        }

        int from = Math.min(diskPosition, closingCellIndex);
        int to = Math.max(diskPosition, closingCellIndex);
        int i = from + 1;
        while (i != to) {
            row.get(i).setDisk(last);
            ++i;
        }
    }

    private void update(Cell cell, DiskColor last) {
        updateRow(getHorizontalRow(cell.getPositionY()), cell.getPositionX(), last);
        updateRow(getVerticalRow(cell.getPositionX()), cell.getPositionY(), last);
        var diagonalRow = getDiagonalRow(cell.getPositionX(), cell.getPositionY());
        updateRow(diagonalRow, getIndexInRow(diagonalRow, cell), last);
        var sideDiagonalRow = getSideDiagonalRow(cell.getPositionX(), cell.getPositionY());
        updateRow(sideDiagonalRow, getIndexInRow(sideDiagonalRow, cell), last);
    }

    public void setDisplayPossibleSteps(boolean value, DiskColor diskColor) {
        isPossibleStepsDisplay = value;
        diskColorWithDisplayedSteps = diskColor;
    }

    public int getDiskAmount(DiskColor diskColor) {
        int amount = 0;
        for (int i = 0; i < getSize(); ++i) {
            for (int j = 0; j < getSize(); ++j) {
                if (getCell(j, i).getDisk() == diskColor) {
                    ++amount;
                }
            }
        }

        return amount;
    }

    @Override
    public String toString() {
        var possibleSteps = getAvailableSteps(diskColorWithDisplayedSteps);
        var out = new StringBuilder();
        out.append("  ");
        out.append("|").append("-".repeat(Math.max(0, size * 4 - 1))).append("|\n");
        for (int i = 0; i < size; ++i) {
            out.append(size - i).append(" ").append('|');
            for (int j = 0; j < size; ++j) {
                if (isPossibleStepsDisplay && possibleSteps.contains(getCell(j, i))) {
                    out.append(" @ ");
                } else {
                    switch (getCell(j, i).getDisk()) {
                        case WHITE -> out.append(" W ");
                        case BLACK -> out.append(" B ");
                        case NONE -> out.append("   ");
                    }
                }
                out.append('|');
            }
            out.append('\n').append("  ");
            out.append("|").append("-".repeat(size * 4 - 1)).append("|\n");
        }
        out.append("    ");
        for (int i = 0; i < size; ++i) {
            out.append((char) ('a' + i)).append("   ");
        }
        out.append('\n');

        return out.toString();
    }

    public Board getBoardCopy() {
        Board boardCopy = new Board(getSize(), isStepBackAllowed);
        for (int i = 0; i < getSize(); ++i) {
            for (int j = 0; j < getSize(); ++j) {
                boardCopy.getCell(j, i).setDisk(getCell(j, i).getDisk());
            }
        }

        return boardCopy;
    }
}

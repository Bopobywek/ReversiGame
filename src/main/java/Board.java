import java.util.ArrayList;
import java.util.Comparator;
import java.util.Stack;
import java.util.function.Function;

public class Board {
    private ArrayList<ArrayList<Cell>> board;
    private Stack<BoardSnapshot> stepsHistory = new Stack<>();
    private final int size;
    private boolean isPossibleStepsDisplay = false;
    private Disk diskWithDisplayedSteps = Disk.None;

    public Board(int size) {
        board = new ArrayList<>();
        this.size = size;
        for (int i = 0; i < size; ++i) {
            ArrayList<Cell> row = new ArrayList<>(size);
            for (int j = 0; j < size; ++j) {
                row.add(new Cell(j, i, Disk.None));
            }
            board.add(row);
        }
    }

    void initialize() {
        getCell(size / 2, size / 2).setDisk(Disk.White);
        getCell(size / 2, size / 2 - 1).setDisk(Disk.Black);
        getCell(size / 2 - 1, size / 2).setDisk(Disk.Black);
        getCell(size / 2 - 1, size / 2 - 1).setDisk(Disk.White);
    }

    public int getSize() {
        return size;
    }

    public void makeStep(int x, int y, Disk disk) {
        makeSnapshot();
        getCell(x, y).setDisk(disk);
        update(getCell(x, y), disk);
    }

    private void makeSnapshot() {
        ArrayList<ArrayList<Cell>> snapshot = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            ArrayList<Cell> row = new ArrayList<>(size);
            for (int j = 0; j < size; ++j) {
                row.add(new Cell(j, i, getCell(j, i).getDisk()));
            }
            board.add(row);
        }
        stepsHistory.add(new BoardSnapshot(snapshot));
    }

    public Cell getCell(int x, int y) {
        return board.get(y).get(x);
    }

    private int getClosingCellIndex(ArrayList<Cell> row, int diskPosition, Disk disk) {
        int position = diskPosition - 1;
        while (position >= 0) {
            Disk disk1 = row.get(position).getDisk();
            if (disk == disk1 && diskPosition - position > 1) {
                return position;
            } else if (disk1 == Disk.None || (disk == disk1 && diskPosition - position <= 1)) {
                break;
            } else {
                position -= 1;
            }
        }

        int rowSize = row.size();
        position = diskPosition + 1;
        while (position < rowSize) {
            Disk disk1 = row.get(position).getDisk();
            if (disk == disk1 && position - diskPosition > 1) {
                return position;
            } else if (disk1 == Disk.None || (disk == disk1 && position - diskPosition <= 1)) {
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

    private boolean isPossibleStep(Cell cell, Disk disk) {
        ArrayList<Cell> horizontalRow = getHorizontalRow(cell.getPositionY());
        int horizontalIndex = cell.getPositionX();

        ArrayList<Cell> verticalRow = getVerticalRow(cell.getPositionX());
        int verticalIndex = cell.getPositionY();

        ArrayList<Cell> diagonalRow = getDiagonalRow(cell.getPositionX(), cell.getPositionY());
        int diagonalIndex = getIndexInRow(diagonalRow, cell);

        ArrayList<Cell> sideDiagonalRow = getSideDiagonalRow(cell.getPositionX(), cell.getPositionY());
        int sideDiagonalIndex = getIndexInRow(sideDiagonalRow, cell);

        return getClosingCellIndex(horizontalRow, horizontalIndex, disk) != -1
                || getClosingCellIndex(verticalRow, verticalIndex, disk) != -1
                || getClosingCellIndex(diagonalRow, diagonalIndex, disk) != -1
                || getClosingCellIndex(sideDiagonalRow, sideDiagonalIndex, disk) != -1;
    }

    public ArrayList<Cell> getAvailableSteps(Disk disk) {
        ArrayList<Cell> available = new ArrayList<>();
        if (disk == Disk.None) {
            return available;
        }

        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if (getCell(j, i).getDisk() != Disk.None) {
                    continue;
                }
                var cell = getCell(j, i);
                if (isPossibleStep(cell, disk)) {
                    available.add(cell);
                }
            }
        }

        return available;
    }

    private void updateRow(ArrayList<Cell> row, int diskPosition, Disk last) {
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

    private void update(Cell cell, Disk last) {
        updateRow(getHorizontalRow(cell.getPositionY()), cell.getPositionX(), last);
        updateRow(getVerticalRow(cell.getPositionX()), cell.getPositionY(), last);
        var diagonalRow = getDiagonalRow(cell.getPositionX(), cell.getPositionY());
        updateRow(diagonalRow, getIndexInRow(diagonalRow, cell), last);
        var sideDiagonalRow = getSideDiagonalRow(cell.getPositionX(), cell.getPositionY());
        updateRow(sideDiagonalRow, getIndexInRow(sideDiagonalRow, cell), last);
    }

    public void displayPossibleSteps(boolean value, Disk disk) {
        isPossibleStepsDisplay = value;
        diskWithDisplayedSteps = disk;
    }

    @Override
    public String toString() {
        var possibleSteps = getAvailableSteps(diskWithDisplayedSteps);
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
                        case White -> out.append(" W ");
                        case Black -> out.append(" B ");
                        case None -> out.append("   ");
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
}

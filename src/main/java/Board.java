import java.util.ArrayList;
import java.util.Stack;
import java.util.function.Function;

public class Board {
    private ArrayList<ArrayList<Cell>> board;
    private Stack<BoardSnapshot> stepsHistory = new Stack<>();
    private final int size;

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

    void initializeBoard() {
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

    private boolean checkRow(ArrayList<Cell> row, int diskPosition, Disk disk) {
        int position = diskPosition - 1;
        while (position >= 0) {
            Disk disk1 = row.get(position).getDisk();
            if (disk == disk1 && diskPosition - position > 1) {
                return true;
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
                return true;
            } else if (disk1 == Disk.None || (disk == disk1 && position - diskPosition <= 1)) {
                break;
            } else {
                position += 1;
            }
        }

        return false;
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

    private ArrayList<Cell> getDiagonalRow(int startX, int startY) {
        ArrayList<Cell> diagonalRow = new ArrayList<>();
        while (startY < size && startX < size) {
            diagonalRow.add(getCell(startX, startY));
            ++startX;
            ++startY;
        }

        return diagonalRow;
    }
    private boolean isPossibleStep(Cell cell, Disk disk) {
        ArrayList<Cell> horizontalRow = getHorizontalRow(cell.getPositionY());
        int horizontalIndex = cell.getPositionX();

        ArrayList<Cell> verticalRow = getVerticalRow(cell.getPositionX());
        int verticalIndex = cell.getPositionY();

        int diagonalIndex = Math.min(cell.getPositionX(), cell.getPositionY());
        int startX = cell.getPositionX() < cell.getPositionY() ? 0 : cell.getPositionX() - cell.getPositionY();
        int startY = cell.getPositionY() < cell.getPositionX() ? 0 : cell.getPositionY() - cell.getPositionX();
        ArrayList<Cell> diagonalRow = getDiagonalRow(startX, startY);


        return checkRow(horizontalRow, horizontalIndex, disk)
                || checkRow(verticalRow, verticalIndex, disk)
                || checkRow(diagonalRow, diagonalIndex, disk);
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

    // TODO: Перепроверить
    private void updateRow(ArrayList<Cell> row, Disk last) {
        ArrayList<Cell> streak = new ArrayList<>();
        boolean isStreak = false;
        for (Cell value : row) {
            var currentDisk = value.getDisk();
            if (currentDisk == Disk.None && !isStreak) {
                continue;
            } else if (currentDisk == Disk.None) {
                streak.clear();
                isStreak = false;
            }

            if (currentDisk == last && !isStreak) {
                isStreak = true;
            } else if (currentDisk != last && isStreak) {
                streak.add(value);
            } else {
                for (var cell : streak) {
                    cell.setDisk(last);
                }
                streak.clear();
                isStreak = false;
            }
        }
    }

    public void update(Disk last) {
        for (int i = 0; i < size; ++i) {
            updateRow(getHorizontalRow(i), last);
            updateRow(getVerticalRow(i), last);
            updateRow(getDiagonalRow(0, i), last);
            updateRow(getDiagonalRow(i, 0), last);
        }
    }

    @Override
    public String toString() {
        var out = new StringBuilder();
        out.append("  ");
        out.append("|").append("-".repeat(Math.max(0, size * 4 - 1))).append("|\n");
        for (int i = 0; i < size; ++i) {
            out.append(size - i).append(" ").append('|');
            for (int j = 0; j < size; ++j) {
                switch (getCell(j, i).getDisk()) {
                    case White -> out.append(" W ");
                    case PossibleWhite -> out.append(" ŵ ");
                    case PossibleBlack -> out.append(" Ḇ ");
                    case Black -> out.append(" B ");
                    case None -> out.append("   ");
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

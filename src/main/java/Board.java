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

    private boolean isPossibleStep(Cell cell, Disk disk) {
        ArrayList<Cell> horizontalRow = board.get(cell.getPositionY());
        int horizontalIndex = cell.getPositionX();

        ArrayList<Cell> verticalRow = new ArrayList<>();
        int verticalIndex = cell.getPositionY();
        for (int i = 0; i < size; ++i) {
            verticalRow.add(getCell(cell.getPositionX(), i));
        }

        ArrayList<Cell> diagonalRow = new ArrayList<>();
        int diagonalIndex = Math.min(cell.getPositionX(), cell.getPositionY());
        int startX = cell.getPositionX() < cell.getPositionY() ? 0 : cell.getPositionX() - cell.getPositionY();
        int startY = cell.getPositionY() < cell.getPositionX() ? 0 : cell.getPositionY() - cell.getPositionX();
        while (startY < size && startX < size) {
            diagonalRow.add(getCell(startX, startY));
            ++startX;
            ++startY;
        }

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

    public void update() {
        for (int i = 0; i < size; ++i) {
            Disk disk = Disk.None;
            ArrayList<Cell> streak = new ArrayList<>();
            for (int j = 0; j < size; ++j) {
                var currentDisk = getCell(j, i).getDisk();
                if (currentDisk == Disk.None && disk == Disk.None) {
                    continue;
                } else if (currentDisk == Disk.None) {
                    disk = Disk.None;
                    streak.clear();
                }

                if (disk == Disk.None) {
                    disk = currentDisk;
                } else if (currentDisk != disk) {
                    streak.add(getCell(j, i));
                } else {
                    for (var cell : streak) {
                        cell.setDisk(disk);
                    }
                    streak.clear();
                    disk = Disk.None;
                }
            }
        }
    }

    @Override
    public String toString() {
        var out = new StringBuilder();
        out.append("    ");
        for (int i = 0; i < size; ++i) {
            out.append((char) ('a' + i)).append("   ");
        }
        out.append('\n').append("  ");
        out.append("|").append("-".repeat(Math.max(0, size * 4 - 1))).append("|\n");
        for (int i = 0; i < size; ++i) {
            out.append(i + 1).append(" ").append('|');
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

        return out.toString();
    }
}

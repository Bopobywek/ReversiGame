/**
 * Класс-клетка доски.
 */
public class Cell {
    private final int positionX;
    private final int positionY;

    private DiskColor diskColor;

    public Cell(int posX, int posY, DiskColor diskColor) {
        positionX = posX;
        positionY = posY;
        this.diskColor = diskColor;
    }

    public void setDisk(DiskColor diskColor) {
        this.diskColor = diskColor;
    }

    public DiskColor getDisk() {
        return diskColor;
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }
}

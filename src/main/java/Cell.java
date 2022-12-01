public class Cell {
    private final int positionX;
    private final int positionY;

    private Disk disk;

    public Cell(int posX, int posY, Disk disk) {
        positionX = posX;
        positionY = posY;
        this.disk = disk;
    }

    public void setDisk(Disk disk) {
        this.disk = disk;
    }

    public Disk getDisk() {
        return disk;
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }
}

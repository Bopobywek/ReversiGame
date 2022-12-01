public class Player {
    private Behaviour behaviour;
    private Disk disk;

    Player(Behaviour behaviour, Disk disk) {
        this.behaviour = behaviour;
        this.disk = disk;
    }

    void makeStep(Board board) {
        behaviour.makeStep(board);
    }

    public boolean isBot() {
        return !(behaviour instanceof Human);
    }

    public Disk getDisk() {
        return disk;
    }
}

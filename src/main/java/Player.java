public class Player {
    private Behaviour behaviour;
    private Disk disk;

    Player(Behaviour behaviour, Disk disk) {
        this.behaviour = behaviour;
        this.disk = disk;
    }

    void makeStep(Board board) {
        Decision decision = behaviour.makeDecision(board, disk);
        if (decision.action == Action.Step) {
            board.makeStep(decision.coordinateX, decision.coordinateY, disk);
        }
    }

    public boolean isBot() {
        return !(behaviour instanceof HumanBehaviour);
    }

    public Disk getDisk() {
        return disk;
    }
}

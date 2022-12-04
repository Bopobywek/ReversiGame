public class Player {
    private Behaviour behaviour;
    private DiskColor diskColor;

    Player(Behaviour behaviour, DiskColor diskColor) {
        this.behaviour = behaviour;
        this.diskColor = diskColor;
    }

    Decision makeStep(Board board) {
        Decision decision = behaviour.makeDecision(board, diskColor);
        if (decision.action == Action.STEP) {
            board.makeStep(decision.coordinateX, decision.coordinateY, diskColor);
        }

        return decision;
    }

    public boolean isBot() {
        return !(behaviour instanceof HumanBehaviour);
    }

    public DiskColor getDisk() {
        return diskColor;
    }
}

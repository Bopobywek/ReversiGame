import java.util.Hashtable;

public class EasyBotBehaviour implements Behaviour {
    @Override
    public Decision makeDecision(Board board, DiskColor diskColor) {
        var possibleSteps = board.getAvailableSteps(diskColor);
        if (possibleSteps.isEmpty()) {
            return new Decision(Action.SKIP);
        }

        Hashtable<Cell, Double> costs = new Hashtable<>();
        for (Cell possibleStep : possibleSteps) {
            if (getCellType(board, possibleStep) == CellType.CORNER) {
                return new Decision(Action.STEP, possibleStep.getPositionX(), possibleStep.getPositionY());
            }
            double r = 0.0;
            var closed = board.getClosedCells(possibleStep, diskColor);
            for (Cell closedCell : closed) {
                switch (getCellType(board, closedCell)) {
                    case CORNER -> r += 0.8;
                    case NEAR_WALL -> r += 0.4;
                }
            }
            if (getCellType(board, possibleStep) == CellType.INTERNAL) {
                r += 1;
            } else {
                r += 2;
            }

            costs.put(possibleStep, r);
        }

        Cell bestStep = possibleSteps.get(0);
        double maxCost = 0;
        for (Cell possibleStep : possibleSteps) {
            if (costs.get(possibleStep) > maxCost) {
                bestStep = possibleStep;
                maxCost = costs.get(possibleStep);
            }
        }

        return new Decision(Action.STEP, bestStep.getPositionX(), bestStep.getPositionY());
    }

    private CellType getCellType(Board board, Cell cell) {
        if ((cell.getPositionX() == 0 || cell.getPositionX() == board.getSize() - 1)
                && (cell.getPositionY() == 0 || cell.getPositionY() == board.getSize() - 1)) {
            return CellType.CORNER;
        } else if ((cell.getPositionX() == 0 || cell.getPositionX() == board.getSize() - 1)
                || (cell.getPositionY() == 0 || cell.getPositionY() == board.getSize() - 1)) {
            return CellType.NEAR_WALL;
        }
        return CellType.INTERNAL;
    }
}

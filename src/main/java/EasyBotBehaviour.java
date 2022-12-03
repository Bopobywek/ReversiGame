import java.util.Dictionary;
import java.util.Hashtable;

public class EasyBotBehaviour implements Behaviour {
    @Override
    public Decision makeDecision(Board board, Disk disk) {
        var possibleSteps = board.getAvailableSteps(disk);
        if (possibleSteps.isEmpty()) {
            return new Decision(Action.Skip);
        }
        Hashtable<Cell, Double> costs = new Hashtable<>();
        for (Cell possibleStep : possibleSteps) {
            if (getCellType(board, possibleStep) == CellType.Corner) {
                return new Decision(Action.Step, possibleStep.getPositionX(), possibleStep.getPositionY());
            }
            double r = 0.0;
            var closed = board.getClosedCells(possibleStep, disk);
            for (Cell closedCell : closed) {
                switch (getCellType(board, closedCell)) {
                    case Corner -> r += 0.8;
                    case NearWall -> r += 0.4;
                }
            }
            if (getCellType(board, possibleStep) == CellType.Internal) {
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

        return new Decision(Action.Step, bestStep.getPositionX(), bestStep.getPositionY());
    }

    private CellType getCellType(Board board, Cell cell) {
        boolean a = cell.getPositionX() == 0 || cell.getPositionX() == board.getSize() - 1;
        boolean b = cell.getPositionY() == 0 || cell.getPositionY() == board.getSize() - 1;
        if ((cell.getPositionX() == 0 || cell.getPositionX() == board.getSize() - 1)
                && (cell.getPositionY() == 0 || cell.getPositionY() == board.getSize() - 1)) {
            return CellType.Corner;
        } else if ((cell.getPositionX() == 0 || cell.getPositionX() == board.getSize() - 1)
                || (cell.getPositionY() == 0 || cell.getPositionY() == board.getSize() - 1)) {
            return CellType.NearWall;
        }
        return CellType.Internal;
    }
}

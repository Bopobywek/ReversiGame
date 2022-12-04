import java.util.Hashtable;

public class EasyBotBehaviour implements Behaviour {
    @Override
    public Decision makeDecision(Board board, DiskColor diskColor) {
        var possibleSteps = board.getAvailableSteps(diskColor);
        if (possibleSteps.isEmpty()) {
            return new Decision(Action.SKIP);
        }

        Hashtable<Cell, Double> rates = new Hashtable<>();
        for (Cell possibleStep : possibleSteps) {

            double rate = getRate(board, possibleStep, diskColor);
            if (rate >= 1) {
                rates.put(possibleStep, rate);
            }
        }

        Cell bestStep = getCellWithMaxRate(rates);
        bestStep = bestStep == null ? possibleSteps.get(0) : bestStep;

        return new Decision(Action.STEP, bestStep.getPositionX(), bestStep.getPositionY());
    }

    protected Cell getCellWithMaxRate(Hashtable<Cell, Double> rates) {
        Cell bestStep = null;
        double maxCost = 0;
        var keys = rates.keys();
        while (keys.hasMoreElements()) {
            Cell possibleStep = keys.nextElement();
            if (rates.get(possibleStep) > maxCost) {
                bestStep = possibleStep;
                maxCost = rates.get(possibleStep);
            }
        }

        return bestStep;
    }

    protected double getRate(Board board, Cell cell, DiskColor diskColor) {
        double rate = 0.0;
        var closed = board.getClosedCells(cell, diskColor);
        for (Cell closedCell : closed) {
            if (getCellType(board, closedCell) == CellType.NEAR_WALL) {
                rate += 2;
            } else {
                rate += 1;
            }
        }
        if (getCellType(board, cell) == CellType.CORNER) {
            rate += 0.8 ;
        } else if (getCellType(board, cell) == CellType.NEAR_WALL) {
            rate += 0.4;
        }

        return rate;
    }
    protected CellType getCellType(Board board, Cell cell) {
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

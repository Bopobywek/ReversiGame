import java.util.Hashtable;

/**
 * Класс, описывающий поведения бота уровня новичка.
 */
public class EasyBotBehaviour implements Behaviour {
    /**
     * Метод для принятия решения о следующем ходе для бота уровня новичка.
     * @param board доска, на которой проходит игра.
     * @param diskColor цвет фишки, которой нужно совершить ход.
     * @return объект-решение, которое было принято.
     * @see Decision объект-решение
     */
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

    /**
     * Возвращает клетку с самым выской эффективностью хода в неё.
     * @param rates эффективности всех клеток.
     * @return клетка с самой высокой эффективностью.
     */
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

    /**
     * Возвращает численную оценку эффективности хода в указанную клетку на некоторой доске для заданной фишки.
     * @param board доска, на которой располагается переданная клетка.
     * @param cell клетка, для которой нужно посчитать эффективность хода в неё.
     * @param diskColor цвет фишки.
     * @return численная оценка эффективности хода.
     */
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

    /**
     * Возвращает тип клетки по её расположению на доске.
     * @param board доска, на которой располагается клетка.
     * @param cell клетка, тип которой нужно определить.
     * @return тип клетки по её расположению на доске.
     */
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

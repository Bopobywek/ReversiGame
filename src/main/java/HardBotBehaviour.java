import java.util.Hashtable;

/**
 * Класс, описывающий поведения бота уровня профессионала.
 */
public class HardBotBehaviour extends EasyBotBehaviour implements Behaviour {
    /**
     * Метод для принятия решения о следующем ходе для бота уровня профессионала.
     * @param board доска, на которой проходит игра.
     * @param diskColor цвет фишки, которой нужно совершить ход.
     * @return объект-решение, которое было принято.
     * @see Decision объект-решение
     */
    @Override
    public Decision makeDecision(Board board, DiskColor diskColor) {
        Board boardCopy = board.getBoardCopy();
        boardCopy.setStepBackAllowed(true);

        Hashtable<Cell, Double> rates = new Hashtable<>();
        var possibleSteps = board.getAvailableSteps(diskColor);
        for (var step : possibleSteps) {
            double rate = getRate(board, step, diskColor);

            boardCopy.makeStep(step.getPositionX(), step.getPositionY(), diskColor);
            DiskColor otherDiskColor = diskColor == DiskColor.WHITE ? DiskColor.BLACK : DiskColor.WHITE;

            // Считаем для каждого из возможных ходов противника R_1(x_1, y_1)
            var otherPossibleSteps = boardCopy.getAvailableSteps(otherDiskColor);
            Hashtable<Cell, Double> otherRates = new Hashtable<>();
            for (var otherStep : otherPossibleSteps) {
                double otherRate = getRate(boardCopy, otherStep, otherDiskColor);
                otherRates.put(otherStep, otherRate);
            }

            // Находим maxR_1(x_1, y_1)
            Cell bestOtherStep = getCellWithMaxRate(otherRates);
            double otherMaxRate = bestOtherStep == null ? 0.0 : getRate(boardCopy, bestOtherStep, otherDiskColor);

            // Вычисляем для данного возможного шага R(x,y) - maxR_1(x_1, y_1)
            rate -= otherMaxRate;
            rates.put(step, rate);

            // Восстанавливаем доску
            boardCopy.restorePreviousStep();
        }

        Cell bestStep = getCellWithMaxRate(rates);
        bestStep = bestStep == null ? possibleSteps.get(0) : bestStep;

        return new Decision(Action.STEP, bestStep.getPositionX(), bestStep.getPositionY());
    }
}

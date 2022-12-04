/**
 * Интерфейс для классов, описывающих поведение игрока.
 */
public interface Behaviour {
    /**
     * Метод для принятия решения о следующем ходе.
     * @param board доска, на которой проходит игра.
     * @param diskColor цвет фишки, которой нужно совершить ход.
     * @return объект-решение, которое было принято.
     * @see Decision объект-решение
     */
    Decision makeDecision(Board board, DiskColor diskColor);
}

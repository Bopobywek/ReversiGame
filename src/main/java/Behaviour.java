public interface Behaviour {
    /**
     *
     * @param board
     * @param diskColor
     * @return
     */
    Decision makeDecision(Board board, DiskColor diskColor);
}

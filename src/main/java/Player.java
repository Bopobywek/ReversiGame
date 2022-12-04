public class Player {
    /**
     * Поле для описания поведения игрока.
     * @see Behaviour
     */
    private Behaviour behaviour;
    private DiskColor diskColor;

    /**
     * Конструктор игрока.
     * @param behaviour поведение игрока.
     * @param diskColor цвет фишек, которыми играет игрок.
     */
    public Player(Behaviour behaviour, DiskColor diskColor) {
        this.behaviour = behaviour;
        this.diskColor = diskColor;
    }

    /**
     * Делает ход на переданной доске.
     * @param board доска, на которой игрок должен сделать ход.
     * @return объект-решение, которое принял игрок на данном ходу.
     */
    public Decision makeStep(Board board) {
        Decision decision = behaviour.makeDecision(board, diskColor);
        if (decision.getAction() == Action.STEP) {
            board.makeStep(decision.getCoordinateX(), decision.getCoordinateY(), diskColor);
        }

        return decision;
    }

    /**
     * Проверяет, является ли игрок ботом.
     * @return {@code true}, если является, иначе {@code false}
     */
    public boolean isBot() {
        return !(behaviour instanceof HumanBehaviour);
    }

    /**
     * Возвращает цвет фишек, которыми играет пользователь.
     * @return цвет фишки.
     */
    public DiskColor getDiskColor() {
        return diskColor;
    }
}

/**
 * Класс, описывающий решение, принимаемое некоторым поведением.
 */
public class Decision {
    private final Action action;
    private int coordinateX = 0;
    private int coordinateY = 0;

    public Decision(Action action) {
        this.action = action;
    }

    public Decision(Action action, int coordinateX, int coordinateY) {
        this.action = action;
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
    }

    public Action getAction() {
        return action;
    }

    public int getCoordinateX() {
        return coordinateX;
    }

    public int getCoordinateY() {
        return coordinateY;
    }
}

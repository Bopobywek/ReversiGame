public class Decision {
    Action action;
    int coordinateX = 0;
    int coordinateY = 0;

    public Decision(Action action) {
        this.action = action;
    }

    public Decision(Action action, int coordinateX, int coordinateY) {
        this.action = action;
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
    }
}

import java.util.ArrayList;

/**
 * Класс-снимок доски.
 */
public class BoardSnapshot {
    public ArrayList<ArrayList<Cell>> board;
    public BoardSnapshot(ArrayList<ArrayList<Cell>> board) {
        this.board = board;
    }
}

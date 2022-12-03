public class Main {
    public static void main(String[] args) {
        Board board = new Board(8);
        board.initialize();
        Player player1 = new Player(new HumanBehaviour(), Disk.Black);
        Player player2 = new Player(new HumanBehaviour(), Disk.White);
        Player currentPlayer = player1;

        while (!isGameFinished(board)) {
            board.displayPossibleSteps(true, currentPlayer.getDisk());
            System.out.println(board);
            printScore(board);
            currentPlayer.makeStep(board);

            if (currentPlayer == player1) {
                currentPlayer = player2;
            } else {
                currentPlayer = player1;
            }
        }
    }

    public static boolean isGameFinished(Board board) {
        int black = 0;
        int white = 0;
        for (int i = 0; i < board.getSize(); ++i) {
            for (int j = 0; j < board.getSize(); ++j) {
                switch (board.getCell(j, i).getDisk()) {
                    case Black -> ++black;
                    case White -> ++white;
                }
            }
        }

        return black + white == 64 || Math.min(black, white) == 0
                || (board.getAvailableSteps(Disk.Black).isEmpty() && board.getAvailableSteps(Disk.White).isEmpty());
    }

    public static void printScore(Board board) {
        int black = 0;
        int white = 0;
        for (int i = 0; i < board.getSize(); ++i) {
            for (int j = 0; j < board.getSize(); ++j) {
                switch (board.getCell(j, i).getDisk()) {
                    case Black -> ++black;
                    case White -> ++white;
                }
            }
        }

        System.out.println(" ".repeat(board.getSize() + 1) + "black: " + black + " | white: " + white);
    }
}

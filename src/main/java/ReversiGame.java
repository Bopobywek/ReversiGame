import java.util.Scanner;

public class ReversiGame {
    private Board board;
    private GameMode gameMode;
    private Player currentPlayer;
    private Player player1;
    private Player player2;
    public ReversiGame() {
        board = new Board(8, true);
        board.initialize();
    }

    public void run() {
        gameMode = getGameMode();
        player1 = new Player(new HumanBehaviour(), DiskColor.BLACK);
        currentPlayer = player1;
        if (gameMode == GameMode.EASY_BOT) {
            player2 = new Player(new EasyBotBehaviour(), DiskColor.WHITE);
        } else {
            player2 = new Player(new HumanBehaviour(), DiskColor.WHITE);
        }

        while (!isGameFinished()) {
            board.displayPossibleSteps(!currentPlayer.isBot(), currentPlayer.getDisk());
            System.out.println(board);
            printScore();
            if (board.getAvailableSteps(currentPlayer.getDisk()).isEmpty()) {
                System.out.println("Player " + (currentPlayer == player1 ? "1" : "2") + " because no steps available, you skip a step. Press enter to continue");
                Scanner scanner = new Scanner(System.in);
                scanner.nextLine();
                if (currentPlayer == player1) {
                    currentPlayer = player2;
                } else {
                    currentPlayer = player1;
                }
                continue;
            }

            Decision decision = new Decision(Action.SKIP);
            while (decision.action != Action.STEP) {
                printStepMessage();
                decision = currentPlayer.makeStep(board);
                if (decision.action == Action.UNDO) {
                    board.restorePreviousStep();
                    board.restorePreviousStep();
                    System.out.println(board);
                    printScore();
                }
            }

            if (currentPlayer == player1) {
                currentPlayer = player2;
            } else {
                currentPlayer = player1;
            }
        }

        System.out.println("GAME OVER");
    }


    private boolean isGameFinished() {
        int blackScore = board.getDiskAmount(DiskColor.BLACK);
        int whiteScore = board.getDiskAmount(DiskColor.WHITE);
        return blackScore + whiteScore == 64 || Math.min(blackScore, whiteScore) == 0
                || (board.getAvailableSteps(DiskColor.BLACK).isEmpty() && board.getAvailableSteps(DiskColor.WHITE).isEmpty());
    }

    private void printStepMessage() {
        System.out.println("Step for a player " + (currentPlayer == player1 ? "1" : "2"));
    }
    private void printFinalMessage() {

    }

    private void printScore() {
        int blackScore = board.getDiskAmount(DiskColor.BLACK);
        int whiteScore = board.getDiskAmount(DiskColor.WHITE);
        System.out.println(" ".repeat(board.getSize() + 1) + "black: " + blackScore + " | white: " + whiteScore);
    }

    public GameMode getGameMode() {
        return GameMode.EASY_BOT;
    }
}

import java.util.NoSuchElementException;
import java.util.Scanner;

public class ReversiGame {
    private Board board;
    private Player currentPlayer;
    private Player player1;
    private Player player2;
    private int bestScorePlayer1 = -1;
    private int bestScorePlayer2 = -1;

    public ReversiGame() {
        board = new Board(8, true);
        board.initialize();
    }

    /**
     * Цикл игры.
     */
    public void run() {
        MenuAction menuAction = getMenuAction();
        while (menuAction != MenuAction.EXIT) {

            if (menuAction == MenuAction.PRINT_BEST_SCORE) {
                printBestScore();
                menuAction = getMenuAction();
                continue;
            }

            initialize(menuAction);
            while (!isGameFinished()) {
                update();
            }
            printFinalMessage();
            updateBestScore();
            menuAction = getMenuAction();
            board = new Board(8, true);
            board.initialize();
        }
    }

    /**
     * Инициализирует игроков в зависимости от выбранного пользователем пункта в меню.
     * @param menuAction выбранный пользователем пункт в меню.
     */
    private void initialize(MenuAction menuAction) {
        player1 = new Player(new HumanBehaviour(), DiskColor.BLACK);
        currentPlayer = player1;
        if (menuAction == MenuAction.PLAY_EASY_BOT) {
            player2 = new Player(new EasyBotBehaviour(), DiskColor.WHITE);
        } else if (menuAction == MenuAction.PLAY_HARD_BOT) {
            player2 = new Player(new HardBotBehaviour(), DiskColor.WHITE);
        } else {
            player2 = new Player(new HumanBehaviour(), DiskColor.WHITE);
            board.setStepBackAllowed(false);
        }
    }

    /**
     * Ожидает ввода Enter.
     */
    private void ReadEnter() {
        try {
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
        } catch (Exception ignored) {
        }
    }

    /**
     * Ход игры.
     */
    private void update() {
        board.setDisplayPossibleSteps(!currentPlayer.isBot(), currentPlayer.getDiskColor());

        System.out.println(board);
        printScore();
        if (currentPlayer.isBot()) {
            printMessageCenteredOnBoard("Bot makes a step now\n");
        }

        if (board.getAvailableSteps(currentPlayer.getDiskColor()).isEmpty()) {
            if (!currentPlayer.isBot()) {
                System.out.println("Player " + (currentPlayer == player1 ? "1" : "2") + " skip a step," +
                        " because no steps available. Press enter to continue.");
                ReadEnter();
            }
            changeCurrentPlayer();
        }

        Decision decision = new Decision(Action.SKIP);
        while (decision.getAction() != Action.STEP) {
            if (!currentPlayer.isBot()) {
                printStepMessage();
            }

            decision = currentPlayer.makeStep(board);
            if (decision.getAction() == Action.UNDO) {
                board.restorePreviousStep();
                board.restorePreviousStep();

                System.out.println(board);
                printScore();
            }
        }

        changeCurrentPlayer();
    }

    /**
     * Метод для смены игрока между ходами.
     */
    private void changeCurrentPlayer() {
        if (currentPlayer == player1) {
            currentPlayer = player2;
        } else {
            currentPlayer = player1;
        }
    }

    /**
     * Выводит на экран сообщение центрированное относительно игровой доски.
     * @param message сообщение для вывода.
     */
    private void printMessageCenteredOnBoard(String message) {
        int boardDisplaySize = 4 * board.getSize() + 1;
        int messageHalfLength = message.length() / 2;
        System.out.println(" ".repeat(boardDisplaySize / 2 - messageHalfLength + 3) + message);
    }

    /**
     * Проверяет, закончилась ли игра.
     * @return {@code true}, если игра закончилась, {@code false} иначе.
     */
    private boolean isGameFinished() {
        int blackScore = board.getDiskAmount(DiskColor.BLACK);
        int whiteScore = board.getDiskAmount(DiskColor.WHITE);
        return blackScore + whiteScore == 64 || Math.min(blackScore, whiteScore) == 0
                || (board.getAvailableSteps(DiskColor.BLACK).isEmpty()
                && board.getAvailableSteps(DiskColor.WHITE).isEmpty());
    }

    /**
     * Выводит сообщение перед шагом игрока.
     */
    private void printStepMessage() {
        System.out.println("Now player " + (currentPlayer == player1 ? "1" : "2") + " is making a move");
    }

    /**
     * Выводит заключительное сообщение.
     */
    private void printFinalMessage() {
        printMessageCenteredOnBoard("GAME OVER");
        System.out.println(board);
        printScore();
        int blackScore = board.getDiskAmount(DiskColor.BLACK);
        int whiteScore = board.getDiskAmount(DiskColor.WHITE);
        if (blackScore > whiteScore) {
            printMessageCenteredOnBoard("Player 1 (Black disks) win!!!\n");
        } else if (whiteScore > blackScore) {
            printMessageCenteredOnBoard("Player 2 (White disks) win!!!\n");
        } else {
            printMessageCenteredOnBoard("Draw: both players have the same score\n");
        }
    }

    /**
     * Обновляет лучший счёт игроков.
     */
    private void updateBestScore() {
        if (!player1.isBot()) {
            bestScorePlayer1 = Math.max(bestScorePlayer1, board.getDiskAmount(DiskColor.BLACK));
        }

        if (!player2.isBot()) {
            bestScorePlayer2 = Math.max(bestScorePlayer2, board.getDiskAmount(DiskColor.WHITE));
        }
    }

    /**
     * Печатает текущий счёт.
     */
    private void printScore() {
        int blackScore = board.getDiskAmount(DiskColor.BLACK);
        int whiteScore = board.getDiskAmount(DiskColor.WHITE);
        printMessageCenteredOnBoard("black: " + blackScore + " | white: " + whiteScore + "\n");
    }

    /**
     * Отображает лучший счёт игрока в рамках сессии.
     */
    private void printBestScore() {
        if (bestScorePlayer1 < 0 && bestScorePlayer2 < 0) {
            System.out.println("There are no completed games yet, so there is no best score\n");
        } else if (bestScorePlayer1 < 0) {
            System.out.println("Player 1 was only a bot, so the score is not determined\n"
                    + "Best score for player 2: " + bestScorePlayer2 + "\n\n");
        } else if (bestScorePlayer2 < 0) {
            System.out.println("Best score for player 1: " + bestScorePlayer1 + "\nPlayer 2 was only a bot," +
                    " so the score is not determined\n");
        } else {
            System.out.println("Best score for player 1: " + bestScorePlayer1 +
                    "\nBest score for player 2: " + bestScorePlayer2 + "\n");
        }
    }

    /**
     * Запрашивает у пользователя выбор опции из меню.
     * @return выбранная опция.
     */
    private MenuAction getMenuAction() {
        System.out.println("""
                Select an action to continue:
                1. Play with easy bot
                2. Play with hard bot
                3. Play with another person
                4. Print best score
                5. Exit from game
                Please, enter selected action.""");

        boolean isCorrectInput = false;
        String input = "";
        do {
            System.out.print(">> ");
            Scanner scanner = new Scanner(System.in);
            String pattern = "[1-5]";
            try {
                input = scanner.next(pattern);
                isCorrectInput = true;
            } catch (NoSuchElementException exception) {
                System.out.println("No such action. Please, try to specify correct action.");
            }
        } while (!isCorrectInput);

        if ("1".equals(input)) {
            return MenuAction.PLAY_EASY_BOT;
        } else if ("2".equals(input)) {
            return MenuAction.PLAY_HARD_BOT;
        } else if ("3".equals(input)){
            return MenuAction.PLAY_HUMAN;
        } else if ("4".equals(input)){
            return MenuAction.PRINT_BEST_SCORE;
        } else {
            return MenuAction.EXIT;
        }
    }
}

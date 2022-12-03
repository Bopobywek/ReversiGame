import java.util.NoSuchElementException;
import java.util.Scanner;

public class HumanBehaviour implements Behaviour {

    @Override
    public Decision makeDecision(Board board, Disk disk) {
        var possibleSteps = board.getAvailableSteps(disk);

        boolean isCorrectStep = false;
        int x = 0;
        int y = 0;
        do {
            System.out.print(">> ");
            String input;
            Scanner scanner = new Scanner(System.in);
            try {
                input = scanner.next("\\d[A-Ha-h]").toLowerCase();
            } catch (NoSuchElementException ex) {
                System.out.println("The cell was entered in the wrong format. Try again.");
                continue;
            }

            x = input.charAt(1) - 97;
            y = 8 - (input.charAt(0) - 48);
            for (var cell : possibleSteps) {
                if (cell.getPositionX() == x && cell.getPositionY() == y) {
                    isCorrectStep = true;
                    break;
                }
            }

            if (!isCorrectStep) {
                System.out.println("You cannot place a disk in this cell. Try specifying a different one.");
            }
        } while (!isCorrectStep);

        return new Decision(Action.Step, x, y);
    }
}

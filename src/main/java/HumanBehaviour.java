import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Класс, который описывает поведение человека.
 */
public class HumanBehaviour implements Behaviour {
    /**
     * Метод для принятия решения о следующем ходе для пользователя.
     * @param board доска, на которой проходит игра.
     * @param diskColor цвет фишки, которой нужно совершить ход.
     * @return объект-решение, которое было принято.
     * @see Decision объект-решение
     */
    @Override
    public Decision makeDecision(Board board, DiskColor diskColor) {
        var possibleSteps = board.getAvailableSteps(diskColor);

        if (possibleSteps.isEmpty()) {
            return new Decision(Action.SKIP);
        }

        boolean isCorrectStep = false;
        int x = 0;
        int y = 0;
        do {
            printAvailableSteps(possibleSteps);
            if (board.isRestoreAllowed()) {
                System.out.println("To undo a step enter \"R\". To specify the cell," +
                        " in which you want to put the disk, enter cell coordinates. Example: 5H.");
            }
            else {
                System.out.println("Specify the cell in which you want to put the disk. Example: 5H.");
            }
            System.out.print(">> ");
            String input;
            Scanner scanner = new Scanner(System.in);

            // Запрашиваем ввод следующего шага.
            String pattern = board.isRestoreAllowed() ? "(?:[Rr]|[1-9][A-Ha-h])" : "[1-9][A-Ha-h]";
            try {
                input = scanner.next(pattern).toLowerCase();
            } catch (NoSuchElementException ex) {
                System.out.println("The cell was entered in the wrong format. Try again.");
                continue;
            } catch (IllegalStateException ex) {
                System.out.println("Something goes wrong. Please, try again.");
                continue;
            }

            if (input.toLowerCase().startsWith("r")) {
                return new Decision(Action.UNDO);
            }

            // Переводим кооридинаты из символьного представления в числовое.
            x = input.charAt(1) - 97;
            y = 8 - (input.charAt(0) - 48);

            // Проверяем, можно ли поставить фишку на указанную клетку.
            for (var cell : possibleSteps) {
                if (cell.getPositionX() == x && cell.getPositionY() == y) {
                    isCorrectStep = true;
                    break;
                }
            }

            if (!isCorrectStep) {
                System.out.println("You cannot place a disk in this cell. Try to specify a different one.");
            }
        } while (!isCorrectStep);

        return new Decision(Action.STEP, x, y);
    }

    /**
     * Метод, который печатает возможные ходы.
     * @param steps возможные ходы.
     */
    private void printAvailableSteps(ArrayList<Cell> steps) {
        StringBuilder sb = new StringBuilder("Possible steps: ");
        ArrayList<String> representations = new ArrayList<>();
        for (var step : steps) {
            char x = (char)(step.getPositionX() + 65);
            char y = (char)((8 - step.getPositionY()) + 48);
            representations.add(String.format("%c%c", y, x));
        }
        sb.append(String.join(", ", representations));
        System.out.println(sb);
    }
}

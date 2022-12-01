public class Main {
    public static void main(String[] args) {
        Board board = new Board(8);
        board.initializeBoard();
        Player player1 = new Player(new Human(), Disk.Black);
        Player player2 = new Player(new Human(), Disk.White);
        Player currentPlayer = player1;

        var res = board.getAvailableSteps(Disk.Black);
        res.get(1).setDisk(Disk.Black);
        board.update();

        res = board.getAvailableSteps(Disk.White);
//        res.get(4).setDisk(Disk.White);

//        res = board.getAvailableSteps(Disk.Black);
        for (var cell : res) {
            cell.setDisk(Disk.PossibleWhite);
        }

        System.out.println(board);
    }
}

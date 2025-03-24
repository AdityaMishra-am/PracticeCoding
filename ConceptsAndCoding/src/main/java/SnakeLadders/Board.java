package SnakeLadders;

import java.util.concurrent.ThreadLocalRandom;

public class Board {
  Cell[][] board;

  public Board(int m, int n, int numberOfSnakes, int numberOfLadders){
    board = new Cell[m][n];
    for(int i=0;i<m;i++) {
      for(int j=0; j<n;j++) {
        Cell cellObj = new Cell();
        board[i][j] = cellObj;
      }
    }
    createSnakeAndLadders(numberOfSnakes,numberOfLadders);

  }
  private void createSnakeAndLadders(int numberOfSnakes, int numberOfLadders) {
    while (numberOfSnakes>0)
    {
      int snakeHead = ThreadLocalRandom.current().nextInt(1, board.length * board[0].length - 1);
      int snakeTail = ThreadLocalRandom.current().nextInt(1, board.length * board[0].length - 1);
      if (snakeTail >= snakeHead) {
        continue;
      }
      Cell cell= getCell(snakeHead);
      Jump jump = new Jump(snakeHead,snakeTail);
      cell.setJump(jump);
      numberOfSnakes--;
    }
    while (numberOfLadders>0)
    {
      int ladderHead = ThreadLocalRandom.current().nextInt(1, board.length * board[0].length - 1);
      int ladderTail = ThreadLocalRandom.current().nextInt(1, board.length * board[0].length - 1);
      if (ladderTail <= ladderHead) {
        continue;
      }
      Cell cell= getCell(ladderHead);
      Jump jump = new Jump(ladderHead,ladderTail);
      cell.setJump(jump);
      numberOfLadders--;
    }
  }
  Cell getCell(int playerPosition) {
    int boardRow = playerPosition / board.length;
    int boardColumn = board.length - (playerPosition % board.length) - 1;
    return board[boardRow][boardColumn];
  }
}

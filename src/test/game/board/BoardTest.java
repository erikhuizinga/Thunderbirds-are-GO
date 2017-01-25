package test.game.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import game.board.Board;
import game.board.content.Stone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Created by erik.huizinga on 25-1-17.
 */
class BoardTest {

  Board board;
  Board boardCopy;
  int dim = 5;
  Stone blackStone;

  @BeforeEach
  void setUp() {
    board = new Board(dim);
    blackStone = new Stone(Stone.BLACK);
  }

  @Test
  void putGet() {
    int x = 0;
    int y = 0;
    board.put(x, y, blackStone);
    assertEquals(blackStone, board.get(x, y));
  }

  @Test
  void copy() {
    boardCopy = new Board(board);
    assertNotEquals(board, boardCopy);
    assertEquals(board.get(0, 0), boardCopy.get(0, 0));
    boardCopy.put(0, 0, blackStone);
    assertNotEquals(board.get(0, 0), boardCopy.get(0, 0));
  }
}
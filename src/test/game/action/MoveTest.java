package test.game.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import game.action.Move;
import game.material.board.Board;
import game.material.Stone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Created by erik.huizinga on 25-1-17.
 */
class MoveTest {

  int dim = 5;
  Board board;
  Board movedBoard;
  int x;
  int y;
  Stone blackStone;
  Move move;

  @BeforeEach
  void setUp() {
    board = new Board(5);
    x = 0;
    y = 0;
    blackStone = Stone.BLACK;
    move = new Move(x, y, blackStone);
  }

  @Test
  void apply() {
    movedBoard = move.apply(board);
    assertEquals(blackStone, movedBoard.get(x, y));
    assertNotEquals(blackStone, board.get(x, y));
  }
}
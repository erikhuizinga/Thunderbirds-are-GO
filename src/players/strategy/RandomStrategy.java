package players.strategy;

import game.Rules;
import game.action.Move;
import game.material.Stone;
import game.material.board.Board;

/** Created by erik.huizinga on 24-1-17. */
public class RandomStrategy implements Strategy {

  private static int maxInvalidMoves = 3; //TODO set 25 or so? Maybe set depending on board dim?
  private int invalidMoveCounter;

  @Override
  public String getName() {
    return "random";
  }

  @Override
  public Move nextMove(Board board, Stone stone) {
    Move move;
    int playableRow;
    int playableCol;
    do {
      playableRow = (int) (Math.random() * board.getDim());
      playableCol = (int) (Math.random() * board.getDim());
      move = new Move(playableRow, playableCol, stone);
      if (!Rules.isTechnicallyValid(board, move)) {
        invalidMoveCounter++;
      } else {
        invalidMoveCounter = 0;
        break;
      }
    } while (invalidMoveCounter < maxInvalidMoves);
    return Rules.isTechnicallyValid(board, move) ? move : null;
  }
}

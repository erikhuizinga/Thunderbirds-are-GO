package game;

import game.action.Move;
import game.material.board.Board;
import java.util.Observable;
import players.Player;

/**
 * A Go game with a board and two players. Created by erik.huizinga on 24-1-17.
 */
public class Go extends Observable implements Runnable {

  /**
   * The Go game {@code Board}.
   */
  private final Board board;

  /**
   * The players in a {@code Player} array, the first element being the first {@code Player}, which
   * plays as black.
   */
  private final Player[] players;

  /**
   * Equal to 0 or 1, indicating the first (black) or second (white) player's turn respectively.
   */
  private int currentPlayer = 0;

  /**
   * Instantiates a game of Go with specified {@code Board} dimension and
   *
   * @param dim the single-side dimension of the {@code Board}.
   * @param blackPlayer the first (black) {@code Player}.
   * @param whitePlayer the second (white) {@code Player}.
   */
  Go(int dim, Player blackPlayer, Player whitePlayer) {
    board = new Board(dim);
    players = new Player[]{blackPlayer, whitePlayer};
    setChanged();
    notifyObservers(board);
  }

  @Override
  public void run() {
    while (!isFinished()) {
      // Get move from player
      Move move = players[getCurrentPlayer()].nextMove();

      // Check move validity


      // Play move


      // Notify observers of the new board
      setChanged();
      notifyObservers(getBoard());

      // Set next player's turn
      nextPlayer();
    }
  }

  /**
   * @return the {@code Board}.
   */
  public Board getBoard() {
    return board;
  }

  /**
   * @return the {@code Player} array.
   */
  public Player[] getPlayers() {
    return players;
  }

  /**
   * @return the current {@code Player}.
   */
  public int getCurrentPlayer() {
    return currentPlayer;
  }

  public void setCurrentPlayer(int currentPlayer) {
    this.currentPlayer = currentPlayer;
  }

  private void nextPlayer() {
    setCurrentPlayer(getCurrentPlayer() ^ 1);
  }

  /**
   * Determine the finished state of the {@code Go} game.
   *
   * @return {@code true} if the game is finished; {@code false} otherwise.
   */
  public boolean isFinished() {
    return false;
  }
}

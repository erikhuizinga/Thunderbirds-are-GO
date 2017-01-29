package game;

import game.action.Move;
import game.material.board.Board;
import java.util.Collection;
import java.util.HashSet;
import java.util.Observable;
import players.Player;

/** A Go game with a board and two players. Created by erik.huizinga on 24-1-17. */
public class Go extends Observable implements Runnable {

  /** The Go game {@code Board}. */
  private Board board;

  /**
   * The players in a {@code Player} array, the first element being the first {@code Player}, which
   * plays as black.
   */
  private final Player[] players;

  /** Equal to 0 or 1, indicating the first (black) or second (white) player's turn respectively. */
  private int currentPlayerIndex = 0;

  /**
   * The board history as a {@code Collection} of the {@code hashCode} values of all previous {@code
   * Board} layouts.
   */
  private final Collection<Integer> boardHistory = new HashSet<Integer>();

  /**
   * Instantiates a game of Go with specified {@code Board} dimension and
   *
   * @param dim the single-side dimension of the {@code Board}.
   * @param blackPlayer the first (black) {@code Player}.
   * @param whitePlayer the second (white) {@code Player}.
   */
  public Go(int dim, Player blackPlayer, Player whitePlayer) {
    board = new Board(dim);
    players = new Player[] {blackPlayer, whitePlayer};
    setChanged();
    notifyObservers(board);
  }

  @Override
  public void run() {
    Move move;
    Board board;
    do {
      do {
        // Get next move from current player
        move = getPlayers()[getCurrentPlayerIndex()].nextMove();

        // Ensure move validity
      } while (!Rules.isValidMove(this, move));

      // Play move
      board = move.apply(getBoard());

      // Handle changes in dynamical validity
      //TODO

      // Add the old board to the history and set the new board as the current
      getBoardHistory().add(getBoard().hashCode());
      setBoard(board);

      // Notify observers of the new board
      setChanged();
      notifyObservers(getBoard());

      // Set next player's turn
      nextPlayer();
    } while (!Rules.isFinished(this));
  }

  /** @param board the {@code Board} to set. */
  public void setBoard(Board board) {
    this.board = board;
  }

  /** @return the {@code Board}. */
  public Board getBoard() {
    return board;
  }

  /** @return the board history */
  public Collection<Integer> getBoardHistory() {
    return boardHistory;
  }

  /** @return the {@code Player} array. */
  public Player[] getPlayers() {
    return players;
  }

  /** @return the current {@code Player}. */
  public int getCurrentPlayerIndex() {
    return currentPlayerIndex;
  }

  /**
   * Set the current player index into {@code getPlayers()}.
   *
   * @param currentPlayerIndex the index.
   */
  public void setCurrentPlayerIndex(int currentPlayerIndex) {
    this.currentPlayerIndex = currentPlayerIndex;
  }

  /** Set the currrent player index to the next player. */
  private void nextPlayer() {
    setCurrentPlayerIndex(getCurrentPlayerIndex() ^ 1);
  }

  /** @return the current {@code Player}. */
  private Player getCurrentPlayer() {
    return getPlayers()[getCurrentPlayerIndex()];
  }
}

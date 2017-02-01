package game;

import game.action.Move;
import game.material.Stone;
import game.material.board.Board;
import java.util.Collection;
import java.util.HashSet;
import java.util.Observable;
import players.Player;

/** A Go game with a board and two players. Created by erik.huizinga on 24-1-17. */
public class Go extends Observable implements Runnable {

  /** The {@code Player} that plays with {@code Stone.BLACK}. */
  private final Player blackPlayer;

  /** The {@code Player} that plays with {@code Stone.WHITE}. */
  private final Player whitePlayer;

  /**
   * The players in a {@code Player} array, the first element being the first {@code Player}, which
   * plays as black.
   */
  private final Player[] players;
  /**
   * The board history as a {@code Collection} of the {@code hashCode} values of all previous {@code
   * Board} layouts.
   */
  private final Collection<Integer> boardHistory = new HashSet<Integer>();
  /** The Go game {@code Board}. */
  private Board board;
  /**
   * Equal to 0 or 1, indicating the first (black) or second (white) player's turn respectively. The
   * value is initialised on the white player, because playing a turn involves changing to the next
   * player.
   */
  private int currentPlayerIndex = 1;

  /**
   * Instantiates a game of Go with specified {@code Board} dimension and
   *
   * @param dim the single-side dimension of the {@code Board}.
   * @param blackPlayer the first (black) {@code Player}.
   * @param whitePlayer the second (white) {@code Player}.
   */
  public Go(int dim, Player blackPlayer, Player whitePlayer) throws AssertionError {
    if (blackPlayer.getStone() != Stone.BLACK) {
      throw new AssertionError("black player's stone must be Stone.BLACK");
    }
    this.blackPlayer = blackPlayer;
    if (whitePlayer.getStone() != Stone.WHITE) {
      throw new AssertionError("white player's stone must be Stone.WHITE");
    }
    this.whitePlayer = whitePlayer;
    players = new Player[] {blackPlayer, whitePlayer};
    board = new Board(dim);
    addHistoryRecord(board);
  }

  /** @return the {@code Player} that plays with {@code Stone.BLACK} */
  public Player getBlackPlayer() {
    return blackPlayer;
  }

  /** @return the {@code Player} that plays with {@code Stone.WHITE} */
  public Player getWhitePlayer() {
    return whitePlayer;
  }

  @Override
  public void run() {
    do {
      playTurn();
    } while (!Rules.isFinished(this));
  }

  void playTurn() {
    Move move;
    Board nextBoard;
    Player currentPlayer = nextPlayer();
    Board currentBoard = getBoard();

    // Update observers
    setChanged();
    notifyObservers(currentBoard);

    turnLoopLabel:
    do {
      do {
        // Get next move from current player
        move = currentPlayer.nextMove(currentBoard);

        // Determine if the next move is a move
        if (move == null) {
          nextBoard = currentBoard;
          break turnLoopLabel;
        }

        // Ensure technical validity of the move
      } while (!Rules.isTechnicallyValid(currentBoard, move));

      // Play move while considering dynamical validation
      nextBoard = Rules.playWithDynamicalValidation(currentBoard, move);

      // Ensure historical validity
    } while (!Rules.isHistoricallyValid(this, nextBoard));

    // Add the current board to the history and set the new board as the current
    addHistoryRecord(currentBoard);
    setBoard(nextBoard);
  }

  /**
   * Add the specified {@code Board} to the board history.
   *
   * @param currentBoard the {@code Board}.
   */
  void addHistoryRecord(Board currentBoard) {
    getBoardHistory().add(currentBoard.hashCode());
  }

  /** @return the {@code Board}. */
  public Board getBoard() {
    return board;
  }

  /** @param board the {@code Board} to set. */
  public void setBoard(Board board) {
    this.board = board;
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

  /** Get the next {@code Player}. */
  private Player nextPlayer() {
    setCurrentPlayerIndex(getCurrentPlayerIndex() ^ 1);
    return getCurrentPlayer();
  }

  /** @return the current {@code Player}. */
  Player getCurrentPlayer() {
    return getPlayers()[getCurrentPlayerIndex()];
  }
}

package game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import game.action.Move;
import game.material.Stone;
import game.material.board.Board;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Created by erik.huizinga on 31-1-17. */
class RulesTest {

  private final int dim = 6;
  private Board board;
  private Move move;
  private Go go;
  private List<Integer> whiteIndices =
      Arrays.asList(9, 34, 35, 41, 50, 44, 46, 54, 12, 13, 14, 19, 20, 22, 28, 29, 30);
  private List<List<Integer>> blackIndices1 =
      Arrays.asList(
          Arrays.asList(0, 1),
          Arrays.asList(1, 0),
          Arrays.asList(3, 3),
          Arrays.asList(4, 2),
          Arrays.asList(5, 3),
          Arrays.asList(4, 4),
          Arrays.asList(5, 4),
          Arrays.asList(3, 5),
          Arrays.asList(5, 2),
          Arrays.asList(4, 1),
          Arrays.asList(3, 0),
          Arrays.asList(5, 0),
          Arrays.asList(2, 1),
          Arrays.asList(2, 2));
  private List<List<Integer>> blackIndices2 =
      Arrays.asList(
          Arrays.asList(2, 2),
          Arrays.asList(1, 1),
          Arrays.asList(0, 2),
          Arrays.asList(1, 4), // Suicide of one stone: normally not allowed due to historical
          // validity
          Arrays.asList(3, 4),
          Arrays.asList(3, 3),
          Arrays.asList(3, 5),
          Arrays.asList(1, 4));
  private List<Move> blackKoMoves =
      Arrays.asList(
          new Move(5, 0, Stone.BLACK), // Suicide of one stone: normally not
          // allowed due to historical validity
          new Move(5, 2, Stone.BLACK),
          new Move(4, 1, Stone.BLACK),
          new Move(5, 0, Stone.BLACK));
  private Move whiteIllegalKoMove = new Move(5, 1, Stone.WHITE);
  private List<Move> legalMovesAfterKo =
      Arrays.asList(
          new Move(1, 0, Stone.WHITE), new Move(0, 1, Stone.BLACK), new Move(5, 1, Stone.WHITE));

  @BeforeEach
  void setUp() {
    // Set up a board with some played stones
    board = new Board(dim);
    for (int whiteIndex : whiteIndices) {
      board.put(whiteIndex, Stone.WHITE);
    }

    // Set up a Go game with an empty board
    go = new Go(dim, null, null);
  }

  @Test
  void testIsTechnicallyValid() {
    // Play a stone on top of another
    move = new Move(0, 0, Stone.BLACK);
    assertFalse(Rules.isTechnicallyValid(board, move));

    // Play a stone outside the playable grid
    move = new Move(-10, -10, Stone.BLACK);
    assertFalse(Rules.isTechnicallyValid(board, move));

    // Play a stone on the playable grid
    move = new Move(1, 0, Stone.BLACK);
    assertTrue(Rules.isTechnicallyValid(board, move));
  }

  @Test
  void testHandleDynamicalValidity1() {
    System.out.println("Before:");
    System.out.println(board);
    System.out.println();

    // Play stones so that all current stones are removed
    List<Integer> blackIndex;
    String boardPath;
    for (int i = 0; i < blackIndices1.size(); i++) {
      blackIndex = blackIndices1.get(i);
      move = new Move(blackIndex.get(0), blackIndex.get(1), Stone.BLACK);
      board = move.apply(board);
      System.out.println("After applying move:");
      System.out.println(board);
      Rules.handleDynamicalValidity(board, move);
      System.out.println("After enforcing dynamical validity:");
      System.out.println(board);

      boardPath = "src/game/RulesTestOutput/board1_" + i + ".txt";
      /*
      // Write some boards to text files
      try {
        Writer writer = new PrintWriter(new FileOutputStream(boardPath));
        writer.write(board.toString());
        writer.flush();

      } catch (FileNotFoundException e) {
        e.printStackTrace();
        System.exit(1);
      } catch (IOException e) {
        e.printStackTrace();
        System.exit(2);
      }
            */

      // Read reference board
      String boardString = "";
      try (Scanner scanner = new Scanner(new FileReader(boardPath))) {
        while (scanner.hasNextLine()) {
          boardString += scanner.nextLine() + "\n";
        }
      } catch (FileNotFoundException e) {
        e.printStackTrace();
        System.exit(1);
      }
      boardString = boardString.substring(0, boardString.length() - 1);

      // Validate with reference
      assertEquals(boardString, board.toString());
    }
  }

  @Test
  void testHandleDynamicalValidity2() {
    System.out.println("Before:");
    System.out.println(board);
    System.out.println();

    // Play stones so that all current stones are removed
    List<Integer> blackIndex;
    String boardPath;
    for (int i = 0; i < blackIndices2.size(); i++) {
      blackIndex = blackIndices2.get(i);
      move = new Move(blackIndex.get(0), blackIndex.get(1), Stone.BLACK);
      board = move.apply(board);
      System.out.println("After applying move:");
      System.out.println(board);
      Rules.handleDynamicalValidity(board, move);
      System.out.println("After enforcing dynamical validity:");
      System.out.println(board);

      boardPath = "src/game/RulesTestOutput/board2_" + i + ".txt";
      /*
      // Write some boards to text files
      try {
        Writer writer = new PrintWriter(new FileOutputStream(boardPath));
        writer.write(board.toString());
        writer.flush();

      } catch (FileNotFoundException e) {
        e.printStackTrace();
        System.exit(1);
      } catch (IOException e) {
        e.printStackTrace();
        System.exit(2);
      }
       */

      // Read reference board
      String boardString = "";
      try (Scanner scanner = new Scanner(new FileReader(boardPath))) {
        while (scanner.hasNextLine()) {
          boardString += scanner.nextLine() + "\n";
        }
      } catch (FileNotFoundException e) {
        e.printStackTrace();
        System.exit(1);
      }
      boardString = boardString.substring(0, boardString.length() - 1);

      // Validate with reference
      assertEquals(boardString, board.toString());
    }
  }

  @Test
  void testPlayWithDynamicalValidation() {
    fail("test not yet implemented");
  }

  @Test
  void testIsHistoricallyValid() {
    // Test that the initial board is in the history
    assertFalse(Rules.isHistoricallyValid(go, go.getBoard()));
    // Test that another board is not in the history
    assertTrue(Rules.isHistoricallyValid(go, board));
    // Test that adding another board to history will make it historically invalid
    go.addHistoryRecord(board);
    assertFalse(Rules.isHistoricallyValid(go, board));

    // Ko
    go = new Go(dim, null, null);
    for (Move blackKoMove : blackKoMoves) {
      board = blackKoMove.apply(board);
      Rules.handleDynamicalValidity(board, blackKoMove);
      assertTrue(Rules.isHistoricallyValid(go, board));
      go.addHistoryRecord(board);
      System.out.println("After playing a black move towards Ko and ensuring validity:");
      System.out.println(board);
    }

    // Play an illegal move and assert that it is historically invalid
    Board illegalBoard = whiteIllegalKoMove.apply(board);
    Rules.handleDynamicalValidity(illegalBoard, whiteIllegalKoMove);
    assertFalse(Rules.isHistoricallyValid(go, illegalBoard));
    System.out.println("Illegal Ko move applied:");
    System.out.println(illegalBoard);

    // Play legal different moves and assert that this is historically valid
    for (Move legalMoveAfterKo : legalMovesAfterKo) {
      board = legalMoveAfterKo.apply(board);
      Rules.handleDynamicalValidity(board, legalMoveAfterKo);
      assertTrue(Rules.isHistoricallyValid(go, board));
      go.addHistoryRecord(board);
      System.out.println("After playing moves after Ko and ensuring validity:");
      System.out.println(board);
    }
  }

  @Test
  void testIsFinished() {
    fail("test not yet implemented");
  }
}

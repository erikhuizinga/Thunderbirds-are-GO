package test;

import game.action.MoveTest;
import game.material.board.BoardTest;
import game.material.board.GridTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  MoveTest.class,
  GridTest.class,
  BoardTest.class,
  HelloTest.class,
})
public class TestSuite {}

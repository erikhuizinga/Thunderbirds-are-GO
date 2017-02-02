package ui.gui;

import com.nedap.go.gui.GoGUIIntegrator;
import game.Go;
import game.action.Move;
import game.action.Remove;
import game.material.Stone;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/** Created by erik.huizinga on 2-2-17. */
public class GUI extends GoGUIIntegrator implements Observer {

  public GUI(boolean showStartupAnimation, boolean mode3D, int boardSize) {
    super(showStartupAnimation, mode3D, boardSize);
    startGUI();
  }

  public GUI(int boardSize) {
    this(false, false, boardSize);
  }

  @Override
  public void update(Observable o, Object arg) {
    if (o instanceof Go) {
      if (arg instanceof List && ((List) arg).size() > 0 && ((List) arg).get(0) instanceof Remove) {
        List removeList = (List) arg;
        for (Object item : removeList) {
          Remove remove = (Remove) item;
          this.removeStone(remove.getPlayableY(), remove.getPlayableX());
        }

      } else if (arg instanceof Move) {
        Move move = (Move) arg;
        this.addStone(move.getPlayableY(), move.getPlayableX(), move.getMaterial() == Stone.WHITE);
      }
    }
  }
}

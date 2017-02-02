package ui.gui;

import com.nedap.go.gui.GoGUIIntegrator;
import java.util.Observable;
import java.util.Observer;

/** Created by erik.huizinga on 2-2-17. */
public class GUI extends GoGUIIntegrator implements Observer {

  public GUI(boolean showStartupAnimation, boolean mode3D, int boardSize) {
    super(showStartupAnimation, mode3D, boardSize);
  }

  public GUI(int boardSize) {
    this(false, false, boardSize);
  }

  @Override
  public void update(Observable o, Object arg) {
    
  }
}

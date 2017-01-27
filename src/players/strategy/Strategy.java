package players.strategy;

/** Created by erik.huizinga on 24-1-17. */
public interface Strategy {

  public static final String name = "strategy";

  /**
   * @return the name of the {@code Strategy}.
   */
  default String getName() {
    return name;
  }
}

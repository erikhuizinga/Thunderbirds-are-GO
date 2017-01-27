package util;

import java.util.List;

/** Created by erik.huizinga on 27-1-17. */
public class ListTools {

  /**
   * Get a random element from a list.
   *
   * @param list
   * @return
   */
  public static <E> E random(List<E> list) {
    return list.get((int) (Math.random() * list.size()));
  }
}

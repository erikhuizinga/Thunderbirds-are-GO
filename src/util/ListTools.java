package util;

import java.util.List;

/** Created by erik.huizinga on 27-1-17. */
public class ListTools {

  /**
   * Get a random element from the specified {@code List<E>}.
   *
   * @param <E> the type parameter.
   * @param list the {@code List<E>}.
   * @return a random {@code E} element from the {@code List<E>}.
   */
  public static <E> E random(List<E> list) {
    return list.get((int) (Math.random() * list.size()));
  }
}

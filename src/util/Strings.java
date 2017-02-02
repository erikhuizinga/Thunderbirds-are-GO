package util;

/** Created by erik.huizinga on 26-1-17. */
public class StringTools {

  /**
   * Repeat a {@code String} a number of times.
   *
   * @param string the {@code String} to repeat.
   * @param number the number of times to repeat.
   * @return the repeated {@code String}.
   */
  public static String repeat(String string, int number) {
    String result = "";
    for (int i = 0; i < number; i++) {
      result += string;
    }
    return result;
  }
}

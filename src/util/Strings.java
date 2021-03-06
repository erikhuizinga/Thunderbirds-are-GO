package util;

import java.util.Scanner;

/** Created by erik.huizinga on 26-1-17. */
public class Strings {

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

  /**
   * Read a line from the system's input stream with the specified prompt.
   *
   * @param prompt the prompt.
   * @return the line as a {@code String}.
   */
  public static String readString(String prompt) {
    System.out.print(prompt);
    Scanner in = new Scanner(System.in);
    String line = null;
    if (in.hasNextLine()) {
      line = in.nextLine();
    }
    return line;
  }
}

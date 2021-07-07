package io.deki.dsdn.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Deki on 18.06.2021
 * @project dsdn
 **/
public class ProcessUtil {

  /**
   * Executes a command with javas Runtime libraries.
   *
   * @param command Command to execute
   * @return Exit code of the resulting process
   */
  public static int execute(String command) {
    try {
      Process process = Runtime.getRuntime().exec(command);
      return process.waitFor();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return -1;
  }

  /**
   * Takes the InputStream of a process and prints incoming lines until there are none left.
   *
   * @param identifier An identifier that will be prepended to the line printed by the process
   * @param stream     InputStream of a process, can be ErrorStream or InputStream
   * @throws IOException
   */
  private static void printProcessStream(String identifier, InputStream stream) throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(stream));
    String line;
    while ((line = in.readLine()) != null) {
      System.out.printf("[%s] %s%n", identifier, line);
    }
  }

}

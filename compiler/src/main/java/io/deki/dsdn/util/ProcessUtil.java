package io.deki.dsdn.util;

import java.io.IOException;
import java.util.concurrent.Executors;

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
      Executors.newSingleThreadExecutor()
          .execute(new StreamGobbler("DSDN Error", process.getErrorStream()));
      Executors.newSingleThreadExecutor()
          .execute(new StreamGobbler("DSDN Info", process.getInputStream()));
      return process.waitFor();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return -1;
  }

}

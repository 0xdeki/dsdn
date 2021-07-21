package io.deki.dsdn.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * The purpose of the stream gobbler is to consume a process' error/input streams
 *
 * @author Deki on 22.07.2021
 * @project dsdn
 **/
public class StreamGobbler extends Thread {

  /**
   * An identifier that will be prepended to the line printed by the process
   */
  private String identifier;

  /**
   * InputStream of a process, can be ErrorStream or InputStream
   */
  private InputStream stream;

  public StreamGobbler(String identifier, InputStream stream) {
    this.identifier = identifier;
    this.stream = stream;
  }

  /**
   * Takes the InputStream of a process and prints incoming lines until there are none left.
   */
  @Override
  public void run() {
    BufferedReader in = new BufferedReader(new InputStreamReader(stream));
    try {
      String line;
      while ((line = in.readLine()) != null) {
        System.out.printf("[%s] %s%n", identifier, line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}

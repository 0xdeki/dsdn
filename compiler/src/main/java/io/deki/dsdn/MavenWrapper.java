package io.deki.dsdn;

import io.deki.dsdn.util.ProcessUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

/**
 * @author Deki on 21.07.2021
 * @project dsdn
 **/
public class MavenWrapper {

  /**
   * Base folder for dsdn files
   */
  private static final String DSDN_HOME = System.getProperty("user.home") + File.separator + "DSDN";

  /**
   * Base folder for maven binaries
   */
  private static final String MAVEN_LOCATION = DSDN_HOME + File.separator + "maven-binaries";

  /**
   * Parent of the maven files, used for command line arguments
   */
  private static final String MAVEN_HOME_LOCATION =
      MAVEN_LOCATION + File.separator + "apache-maven-3.8.1";

  /**
   * Parent of the maven binary file
   */
  private static final String MAVEN_BINARY_PARENT_LOCATION =
      MAVEN_HOME_LOCATION + File.separator + "bin";

  /**
   * Parent of the maven plexus file
   */
  private static final String MAVEN_BOOT_PARENT_LOCATION =
      MAVEN_HOME_LOCATION + File.separator + "boot";

  /**
   * Full path of the maven binaries after they have been downloaded and unzipped
   */
  private static final String MAVEN_BINARY_LOCATION =
      MAVEN_BINARY_PARENT_LOCATION + File.separator + "mvn";

  /**
   * Location of the downloaded zip file containing maven
   */
  private static final String MAVEN_ZIP_LOCATION = DSDN_HOME + File.separator + "mvn.zip";

  /**
   * URL from which maven binaries are downloaded from
   */
  private static final String MAVEN_DOWNLOAD_URL =
      "https://downloads.apache.org/maven/maven-3/3.8.1/binaries/apache-maven-3.8.1-bin.zip";

  /**
   * Make sure maven is installed before any maven related method is called
   */
  static {
    if (!isMavenInstalled()) {
      if (!downloadMaven()) {
        System.out.println("Failed to download maven binaries");
      } else if (!unzipMaven()) {
        System.out.println("Failed to unzip maven binaries");
      } else {
        cleanupInstallation();
      }
    }
  }

  /**
   * Execute a maven plugin on a directory
   *
   * @param dir    The directory of the project(s) to build - can contain one or multiple modules,
   *               with one or multiple pom files
   * @param plugin The maven plugin to run, like "clean", "install" and "package"
   * @return Whether or not the command was executed without errors
   */
  public static boolean executeMavenCommand(File dir, String plugin) {
    String command = buildMavenBase() + "-f " + dir.getAbsolutePath() + " " + plugin;
    int exitCode = ProcessUtil.execute(command);
    return exitCode == 0;
  }

  /**
   * Helper method to generate the base cli command to run maven. Simply explained, it's a very
   * verbose way of writing "mvn" (which doesn't work in this context, hence why we do this)
   *
   * @return Maven base command
   */
  private static String buildMavenBase() {
    return "java"
        + " -Dmaven.multiModuleProjectDirectory="
        + MAVEN_HOME_LOCATION
        + " -Dmaven.home="
        + MAVEN_HOME_LOCATION
        + " -Dclassworlds.conf="
        + MAVEN_BINARY_PARENT_LOCATION + File.separator + "m2.conf"
        + " -classpath "
        + MAVEN_BOOT_PARENT_LOCATION + File.separator + "plexus-classworlds-2.6.0.jar;"
        + MAVEN_BOOT_PARENT_LOCATION + File.separator + "plexus-classworlds.license"
        + " org.codehaus.classworlds.Launcher ";
  }

  /**
   * First checks whether or not the dsdn home folder exists and creates it if it doesn't. Then
   * checks if the maven binary file exists
   *
   * @return Returns whether or not the maven binaries are installed by dsdn.
   */
  private static boolean isMavenInstalled() {
    File folder = new File(DSDN_HOME);
    if (!folder.exists() || !folder.isDirectory()) {
      folder.mkdirs();
      return false;
    }
    File mvn = new File(MAVEN_BINARY_LOCATION);
    return mvn.exists();
  }

  /**
   * Downloads maven from apaches servers
   *
   * @return Whether or not the download was successful
   */
  private static boolean downloadMaven() {
    System.out.println("Downloading maven binaries from " + MAVEN_DOWNLOAD_URL + "...");
    try {
      InputStream in = new URL(MAVEN_DOWNLOAD_URL).openStream();
      Files.copy(in, new File(MAVEN_ZIP_LOCATION).toPath(),
          StandardCopyOption.REPLACE_EXISTING);
      System.out.println("Downloaded maven binaries to " + MAVEN_ZIP_LOCATION);
      return true;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * Unzips the recently downloaded maven zip file
   *
   * @return Whether or not the file was unzipped
   */
  private static boolean unzipMaven() {
    System.out.println("Unpacking maven binaries...");
    ZipFile zipFile = new ZipFile(MAVEN_ZIP_LOCATION);
    try {
      File folder = new File(MAVEN_LOCATION);
      if (!folder.exists() || !folder.isDirectory()) {
        folder.mkdirs();
      }
      zipFile.extractAll(MAVEN_LOCATION);
      System.out.println("Extracted zip contents to " + MAVEN_LOCATION);
      return true;
    } catch (ZipException e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * Deletes the downloaded maven zip file
   */
  private static void cleanupInstallation() {
    System.out.println("Cleaning up...");
    File zip = new File(MAVEN_ZIP_LOCATION);
    if (zip.exists()) {
      zip.delete();
    }
  }

}

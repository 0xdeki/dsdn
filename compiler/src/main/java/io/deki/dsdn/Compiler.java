package io.deki.dsdn;

import io.deki.dsdn.util.ProcessUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;

/**
 * @author Deki on 18.06.2021
 * @project dsdn
 **/
public class Compiler {

  /**
   * Compiles the source code of an entire directory using the javac command line tool.
   *
   * @param dir       Directory of source code to compile
   * @param output    Directory where compiled .class files will be saved
   * @param libraries List of libraries to add to classpath when compiling
   * @return Returns whether or not the operation was successful
   */
  public static boolean compile(File dir, File output, File... libraries) {
    cleanDirectory(output);
    output.mkdirs();
    StringBuilder classpath = new StringBuilder();
    for (File library : libraries) {
      classpath.append(library).append(";");
    }
    File indexFile = new File(dir + "-index");
    indexSourceFiles(dir, indexFile);
    String command = String.format("javac @%s -d %s", indexFile, output);
    if (classpath.length() > 0) {
      command += " -cp " + classpath;
    }
    int exitCode = ProcessUtil.execute(command);
    indexFile.delete();
    return exitCode == 0;
  }

  /**
   * Packages class files in a directory to a jar file.
   *
   * @param dir    Directory of class files to package
   * @param output File to package classes to
   * @return Returns whether or not the operation was successful
   */
  public static boolean createJar(File dir, File output) {
    if (output.exists()) {
      output.delete();
    }
    //use the -C argument to make sure the directory structure inside the jar file is
    //the same as source package structure
    int exitCode = ProcessUtil.execute(String.format("jar cf %s -C %s .", output, dir, dir));
    return exitCode == 0;
  }

  /**
   * Traverses a directory and saves the full path of every .java file it finds. The paths are saved
   * to a file that we can pass to javac later to compile the source code of the whole directory.
   *
   * @param dir    Directory to traverse
   * @param output File to which the paths will be saved. If the file already exists it will be
   *               overwritten.
   */
  private static void indexSourceFiles(File dir, File output) {
    try {
      if (output.exists()) {
        output.delete();
      }
      output.createNewFile();
      StringBuilder builder = new StringBuilder();
      List<String> files = Files.walk(dir.toPath()).map(Path::toString)
          .filter(file -> file.endsWith(".java")).collect(Collectors.toList());
      files.forEach(file -> builder.append(file).append(" "));
      Files.write(output.toPath(), builder.toString().getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Fully deletes a directory, if it exists.
   *
   * @param dir Directory to delete
   */
  private static void cleanDirectory(File dir) {
    if (dir.exists()) {
      if (!dir.isDirectory()) {
        dir.delete();
      } else {
        try {
          FileUtils.deleteDirectory(dir);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

}

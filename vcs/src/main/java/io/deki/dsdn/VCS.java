package io.deki.dsdn;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class VCS {

  /**
   * Cleanly clones a git repository by deleting the repository folder if it already exists.
   *
   * @param url HTTP(S) url to clone. Should end with .git
   * @param dir Directory to clone repository to
   */
  public static void cleanCloneRepo(String url, File dir) {
    cleanDirectory(dir);
    cloneRepo(url, dir);
  }

  /**
   * Cleanly clones a git repository by deleting the repository folder if it already exists. Uses
   * username/password authentication to authenticate.
   *
   * @param url      HTTP(S) url to clone. Should end with .git
   * @param dir      Directory to clone repository to
   * @param username Username to use for authentication
   * @param password Password to use for authentication
   */
  public static void cleanCloneAuthedRepo(String url, File dir, String username, String password) {
    cleanDirectory(dir);
    cloneAuthedRepo(url, dir, username, password);
  }

  /**
   * Clones a git repository over HTTP(S).
   *
   * @param url HTTP(S) url to clone. Should end with .git
   * @param dir Directory to clone repository to
   */
  public static void cloneRepo(String url, File dir) {
    if (!dir.exists()) {
      dir.mkdirs();
    }
    try {
      Git git = Git.cloneRepository()
          .setURI(url)
          .setDirectory(dir)
          .call();
    } catch (GitAPIException e) {
      e.printStackTrace();
    }
  }

  /**
   * Clones a git repository over HTTP(S) using username and password authentication.
   *
   * @param url      HTTP(S) url to clone. Should end with .git
   * @param dir      Directory to clone repository to
   * @param username Username to use for authentication
   * @param password Password to use for authentication
   */
  public static void cloneAuthedRepo(String url, File dir, String username, String password) {
    if (!dir.exists()) {
      dir.mkdirs();
    }
    try {
      Git git = Git.cloneRepository()
          .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
          .setURI(url)
          .setDirectory(dir)
          .call();
    } catch (GitAPIException e) {
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

package io.deki.dsdn.gitlab;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.deki.dsdn.dto.AddMember;
import io.deki.dsdn.dto.Group;
import io.deki.dsdn.dto.Member;
import io.deki.dsdn.dto.MemberAccessLevel;
import io.deki.dsdn.dto.NewSubgroup;
import io.deki.dsdn.dto.Project;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class GitLabAPI {

  /**
   * Base endpoint for GitLab's v4 API.
   */
  private static final String BASE_URL = "https://gitlab.com/api/v4/";

  /**
   * Access token used to authenticate GitLab requests.
   */
  private static String accessToken;

  /**
   * Sets the access token to be used by GitLab for all requests that require authentication.
   *
   * @param accessToken
   */
  public static void setAccessToken(String accessToken) {
    GitLabAPI.accessToken = accessToken;
  }

  /**
   * Fetches a list of groups from GitLab. Will return accessible groups if authenticated, or a list
   * of public groups if not authenticated.
   *
   * @return A list of groups
   */
  public static List<Group> getGroups() {
    try {
      String url = BASE_URL + "groups";
      return extractGroups(sendAuthenticated(new HttpGet(url)));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new ArrayList<>();
  }

  /**
   * Fetches a list of accessible subgroups of a group. If the group is not publicly accessible
   * authentication is required.
   *
   * @param groupId ID of the parent group
   * @return A list of groups
   */
  public static List<Group> getSubgroups(String groupId) {
    try {
      String url = BASE_URL + "groups/" + groupId + "/subgroups";
      return extractGroups(sendAuthenticated(new HttpGet(url)));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new ArrayList<>();
  }

  /**
   * Fetches a list of accessible projects from a group. If the group is not publicly accessible
   * authentication is required.
   *
   * @param groupId ID of the parent group
   * @return A list of projects
   */
  public static List<Project> getProjects(String groupId) {
    try {
      String url = BASE_URL + "groups/" + groupId + "/projects";
      return extractProjects(sendAuthenticated(new HttpGet(url)));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new ArrayList<>();
  }

  /**
   * Fetches a list of accessible members from a group. If the group is not publicly accessible
   * authentication is required.
   *
   * @param groupId ID of the group
   * @return A list of members
   */
  public static List<Member> getGroupMembers(String groupId) {
    try {
      String url = BASE_URL + "groups/" + groupId + "/members";
      return extractMembers(sendAuthenticated(new HttpGet(url)));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new ArrayList<>();
  }

  /**
   * Fetches a list of accessible members from a project. If the group is not publicly accessible
   * authentication is required.
   *
   * @param projectId ID of the project
   * @return a list of members
   */
  public static List<Member> getProjectMembers(String projectId) {
    try {
      String url = BASE_URL + "projects/" + projectId + "/members";
      return extractMembers(sendAuthenticated(new HttpGet(url)));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new ArrayList<>();
  }

  /**
   * Creates a subgroup within an existing group.
   *
   * @param name     Name of the new subgroup
   * @param path     Path of the new subgroup
   * @param parentId ID of the parent group
   * @return Whether or not the subgroup was successfully created
   */
  public static boolean createSubgroup(String name, String path, String parentId) {
    String url = BASE_URL + "groups";
    HttpPost post = new HttpPost(url);
    Gson gson = new Gson();
    NewSubgroup group = new NewSubgroup(name, path, parentId);
    setPostJson(post, gson.toJson(group));
    try {
      HttpResponse response = sendAuthenticated(post);
      return response.getStatusLine().getStatusCode() == 201;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * Adds a member to a group and grants them a certain level of access.
   *
   * @param groupId     ID of the group to add a member to
   * @param memberId    ID of the member being added
   * @param accessLevel Access level to grant the member being added
   * @return Whether or not the member was added
   */
  public static boolean addMemberToGroup(String groupId, String memberId,
      MemberAccessLevel accessLevel) {
    String url = BASE_URL + "groups/" + groupId + "/members";
    return addMemberToGroupOrProject(url, memberId, accessLevel.getId());
  }

  /**
   * Adds a member to a project and grants them a certain level of access.
   *
   * @param projectId   ID of the project to add a member to
   * @param memberId    ID of the member being added
   * @param accessLevel Access level to grant the member being added
   * @return Whether or not the member was added
   */
  public static boolean addMemberToProject(String projectId, String memberId,
      MemberAccessLevel accessLevel) {
    String url = BASE_URL + "projects/" + projectId + "/members";
    return addMemberToGroupOrProject(url, memberId, accessLevel.getId());
  }

  /**
   * Adding a member to a group or a project works pretty much the same way. This internal method
   * handles the generic part of "add member to x".
   *
   * @param url         URL to post the request to
   * @param memberId    ID of the member being added
   * @param accessLevel Access level to grant the member being added
   * @return Whether or not the member was added
   */
  private static boolean addMemberToGroupOrProject(String url, String memberId, int accessLevel) {
    HttpPost post = new HttpPost(url);
    Gson gson = new Gson();
    AddMember member = new AddMember(memberId, accessLevel);
    setPostJson(post, gson.toJson(member));
    try {
      HttpResponse response = sendAuthenticated(post);
      return response.getStatusLine().getStatusCode() == 201;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * Helper method to parse Group objects from server responses. Uses GSON.
   *
   * @param response Response to parse groups from
   * @return A list of groups, or an empty list if parsing failed
   */
  private static List<Group> extractGroups(HttpResponse response) {
    List<Group> groups = new ArrayList<>();
    try {
      Gson gson = new Gson();
      Type listType = new TypeToken<ArrayList<Group>>() {
      }.getType();
      groups = gson.fromJson(EntityUtils.toString(response.getEntity()), listType);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return groups;
  }

  /**
   * Helper method to parse Project objects from server responses. Uses GSON.
   *
   * @param response Response to parse projects from
   * @return A list of projects, or an empty list if parsing failed
   */
  private static List<Project> extractProjects(HttpResponse response) {
    List<Project> projects = new ArrayList<>();
    try {
      Gson gson = new Gson();
      Type listType = new TypeToken<ArrayList<Project>>() {
      }.getType();
      projects = gson.fromJson(EntityUtils.toString(response.getEntity()), listType);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return projects;
  }

  /**
   * Helper method to parse Member objects from server responses. Uses GSON.
   *
   * @param response Response to parse members from
   * @return A list of members, or an empty list if parsing failed
   */
  private static List<Member> extractMembers(HttpResponse response) {
    List<Member> members = new ArrayList<>();
    try {
      Gson gson = new Gson();
      Type listType = new TypeToken<ArrayList<Member>>() {
      }.getType();
      members = gson.fromJson(EntityUtils.toString(response.getEntity()), listType);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return members;
  }

  /**
   * Appends a GitLab authentication header to a request before sending it with a new minimal
   * HttpClient created with createClient().
   *
   * @param request Request to send to server
   * @return Response entity returned by server
   * @throws Exception If the GitLab access token hasn't been set with setAccessToken()
   */
  private static HttpResponse sendAuthenticated(HttpUriRequest request) throws Exception {
    if (accessToken == null) {
      throw new Exception("GitLab access token not set");
    }
    request.addHeader("PRIVATE-TOKEN", accessToken);
    return createClient().execute(request);
  }

  /**
   * Appends a JSON entity as request body to a POST request. Also sets the appropriate header so
   * the server identifies the entity as JSON
   *
   * @param post POST request to add JSON to
   * @param json JSON payload in string format
   */
  private static void setPostJson(HttpPost post, String json) {
    post.addHeader("Content-Type", "application/json;charset=utf-8");
    post.setEntity(new StringEntity(json, "UTF-8"));
  }

  /**
   * Creates a minimal HttpClient.
   *
   * @return
   */
  private static HttpClient createClient() {
    return HttpClients.createMinimal();
  }

}

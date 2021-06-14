package io.deki.dsdn.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Project {

  private String id;

  private String name;

  @SerializedName("default_branch")
  private String defaultBranch;

  @SerializedName("ssh_url_to_repo")
  private String gitUrlSsh;

  @SerializedName("http_url_to_repo")
  private String gitUrlHttp;

  @SerializedName("web_url")
  private String webUrl;

  @SerializedName("created_at")
  private String createdAt;

}

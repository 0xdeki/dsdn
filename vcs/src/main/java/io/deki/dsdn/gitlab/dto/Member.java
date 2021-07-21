package io.deki.dsdn.gitlab.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Member {

  private int id;

  private String username;

  private String name;

  private String state;

  @SerializedName("web_url")
  private String webUrl;

  @SerializedName("access_level")
  private int accessLevel;

}

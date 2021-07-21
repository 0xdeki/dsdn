package io.deki.dsdn.gitlab.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Group {

  private String id;

  private String name;

  private String description;

  private String visibility;

  @SerializedName("web_url")
  private String webUrl;

  @SerializedName("created_at")
  private String createdAt;

}

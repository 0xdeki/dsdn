package io.deki.dsdn.dto;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NewSubgroup {

  private String name;

  private String path;

  @SerializedName("parent_id")
  private String parentId;
}

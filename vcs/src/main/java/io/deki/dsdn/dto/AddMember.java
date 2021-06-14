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
public class AddMember {

  @SerializedName("user_id")
  private String userId;

  @SerializedName("access_level")
  private int accessLevel;

}

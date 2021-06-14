package io.deki.dsdn.dto;

public enum MemberAccessLevel {

  NO_ACCESS(0),
  MINIMAL_ACCESS(5),
  GUEST(10),
  REPORTER(20),
  DEVELOPER(30),
  MAINTAINER(40),
  OWNER(50)
  ;

  private final int id;

  MemberAccessLevel(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }
}

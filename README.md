# Deki SDN

## What is it?
The purpose of software-defined networking (SDN) is, in simple terms, to centralize parts of the otherwise decentralized internet. 
The primary use case of this library is to help serve java code to end-users from multiple independent developers through git and GitLab. 
This library does not serve as an SDN controller in itself, but provides the tools to create one. 

## How do I use it?
This library is meant to be used with GitLab as it provides tools that lets us fully automate the developer side of the process. 
Here is a quick overview of how this library is meant to be used:

Setup:
The program defines a GitLab access token with `GitLabAPI.setAccessToken`. GitLab access tokens can be generated [here](https://gitlab.com/-/profile/personal_access_tokens). Make sure the token has the "api" scope.

1. A developer requests a repository to push their code to. The program uses `GitLabAPI.createSubgroup()` to create a subgroup inside a bigger, SDN-wide group on GitLab.
2. The program uses `GitLab.addMemberToGroup()` to add the developer to the newly created subgroup. 
3. The developer creates a new project in their subgroup, and pushes their code to it.
4. The developer requests their code to be compiled. This can either happen automatically or after an SDN maintainer has reviewed the code.
5. After code review, or on-demand by developer, the repository is cloned by the program with `VCS.cleanCloneAuthedRepo()` (assuming the repository requires authentication, if not use `VCS.cleanCloneRepo()`)
6. The program compiles the repository code with `todo`.
7. The code is further processed if necessary and distributed by the program to end-users.

## Features
### GitLab
* Get accessible groups and projects
* Get members of a group or project
* Create subgroups (a subgroup is a group within a group)
* Add members to groups and projects


Note: a subgroup is effectively a group, (almost) all group-related API also works on subgroups.

### Git
* Clone a repository to a directory over HTTP(S)

## Todo
* Compiler that compiles java code in a directory (to compile repositories)
* More GitLab actions

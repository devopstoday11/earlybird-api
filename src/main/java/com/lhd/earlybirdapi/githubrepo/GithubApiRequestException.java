package com.lhd.earlybirdapi.githubrepo;

class GithubApiRequestException extends RuntimeException {

  GithubApiRequestException(String message) {
    super("Failed to fetch new issues from Github's Public API: " + message);
  }

}

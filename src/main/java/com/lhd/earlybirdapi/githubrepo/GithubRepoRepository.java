package com.lhd.earlybirdapi.githubrepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GithubRepoRepository extends JpaRepository<GithubRepo, String> {

}

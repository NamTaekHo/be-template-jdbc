package com.springboot.member.repository;

import com.springboot.member.entity.Member;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MemberRepository extends CrudRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByEmailAndName(String email, String name);
}

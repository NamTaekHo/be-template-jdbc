package com.springboot.member.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * V2
 *  - 메서드 구현
 *  - DI 적용
 */
@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    public Member createMember(Member member) {
        verifyExistsEmail(member.getEmail());

        return memberRepository.save(member);
    }

    public Member updateMember(Member member) {
        Member foundMember = findVerifiedMember(member.getMemberId());

        Optional.ofNullable(member.getName())
                .ifPresent(name -> foundMember.setName(name));

        Optional.ofNullable(member.getPhone())
                .ifPresent(phone -> foundMember.setPhone(phone));

        return memberRepository.save(foundMember);
    }

    public Member findMember(long memberId) {
        return findVerifiedMember(memberId);
    }

    public List<Member> findMembers() {
        return (List<Member>)memberRepository.findAll();
    }

    public void deleteMember(long memberId) {
        Member foundMember = findVerifiedMember(memberId);
        memberRepository.delete(foundMember);
    }

    // 검증 예외처리 로직 분리
    private void verifyExistsEmail(String email){
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if(optionalMember.isPresent()){
            throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS);
        }
    }

    private Member findVerifiedMember(long memberId){
        Optional<Member> optionalMember = memberRepository.findById(memberId);

        return optionalMember.orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }

    public void verifyExistsMemberId(long memberId){
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if(!optionalMember.isPresent()){
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
        }
    }
}

/**
 *
 */
package com.crossover.techtrial.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.crossover.techtrial.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.crossover.techtrial.model.Member;
import com.crossover.techtrial.repositories.MemberRepository;

/**
 * @author crossover
 *
 */
@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    MemberRepository memberRepository;

    public List<Member> findAll() {
        List<Member> memberList = new ArrayList<>();
        memberRepository.findAll().forEach(memberList::add);
        return memberList;
    }

}

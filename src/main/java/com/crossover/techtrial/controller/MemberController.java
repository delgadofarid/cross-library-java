/**
 *
 */
package com.crossover.techtrial.controller;

import com.crossover.techtrial.dto.TopMemberDTO;
import com.crossover.techtrial.model.Member;
import com.crossover.techtrial.repositories.MemberRepository;
import com.crossover.techtrial.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author crossover
 */

@RestController
public class MemberController {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    /*
     * PLEASE DO NOT CHANGE SIGNATURE OR METHOD TYPE OF END POINTS
     */
    @PostMapping(path = "/api/member")
    public ResponseEntity<Member> registerMember(@Valid @RequestBody Member p) {
        return ResponseEntity.ok(memberRepository.save(p));
    }

    /*
     * PLEASE DO NOT CHANGE API SIGNATURE OR METHOD TYPE OF END POINTS
     */
    @GetMapping(path = "/api/member")
    public ResponseEntity<List<Member>> getAll() {
        return ResponseEntity.ok(memberService.findAll());
    }

    /*
     * PLEASE DO NOT CHANGE API SIGNATURE OR METHOD TYPE OF END POINTS
     */
    @GetMapping(path = "/api/member/{member-id}")
    public ResponseEntity<Member> getMemberById(@PathVariable(name = "member-id", required = true) Long memberId) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if (optionalMember.isPresent()) {
            return ResponseEntity.ok(optionalMember.get());
        }
        return ResponseEntity.notFound().build();
    }


    /**
     * This API returns the top 5 members who issued the most books within the search duration.
     * Only books that have dateOfIssue and dateOfReturn within the mentioned duration should be counted.
     * Any issued book where dateOfIssue or dateOfReturn is outside the search, should not be considered.
     *
     * DONT CHANGE METHOD SIGNATURE AND RETURN TYPES
     * @return
     */
    @GetMapping(path = "/api/member/top-member")
    public ResponseEntity<List<TopMemberDTO>> getTopMembers(
            @RequestParam(value = "startTime", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime startTime,
            @RequestParam(value = "endTime", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime endTime) {
        // checks if startDate is >= than endTime
        if (startTime.compareTo(endTime) >= 0) {
            return ResponseEntity.badRequest().build();
        }
        List<TopMemberDTO> topDrivers = memberRepository.findTop5MemberList(startTime, endTime, PageRequest.of(0,5));
        return ResponseEntity.ok(topDrivers);

    }

}

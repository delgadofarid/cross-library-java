/**
 *
 */
package com.crossover.techtrial.repositories;

import com.crossover.techtrial.dto.TopMemberDTO;
import com.crossover.techtrial.model.Member;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RestResource;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Person repository for basic operations on Member entity.
 * @author crossover
 */
@RestResource(exported = false)
public interface MemberRepository extends CrudRepository<Member, Long> {

    @Query("SELECT " +
            "    new com.crossover.techtrial.dto.TopMemberDTO(t.member.id, t.member.name, t.member.email, COUNT(t.member.id)) " +
            "FROM " +
            "    Transaction t " +
            "WHERE " +
            "    t.dateOfIssue BETWEEN ?1 AND ?2 AND " +
            "    t.dateOfReturn BETWEEN ?1 AND ?2 " +
            "GROUP BY " +
            "    t.member.id, t.member.name, t.member.email " +
            "ORDER BY " +
            "    COUNT(t.member.id) DESC")
    List<TopMemberDTO> findTop5MemberList(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

}

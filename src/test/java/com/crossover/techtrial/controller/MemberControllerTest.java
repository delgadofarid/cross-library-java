/**
 *
 */
package com.crossover.techtrial.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.crossover.techtrial.model.Member;
import com.crossover.techtrial.repositories.MemberRepository;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author kshah
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MemberControllerTest {

    MockMvc mockMvc;

    @Mock
    private MemberController memberController;

    @Autowired
    private TestRestTemplate template;

    @Autowired
    MemberRepository memberRepository;

    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();
    }

    @Test
    public void testMemberRegistrationSuccessful() throws Exception {
        HttpEntity<Object> member = getHttpEntity(
                "{\"name\": \"Elon Musk\", \"email\": \"emuskmember@gmail.com\","
                        + " \"membershipStatus\": \"ACTIVE\",\"membershipStartDate\":\"2018-12-15T12:12:12\" }");

        ResponseEntity<Member> response = template.postForEntity(
                "/api/member", member, Member.class);

        Assert.assertEquals("Elon Musk", response.getBody().getName());
        Assert.assertEquals(200, response.getStatusCode().value());

        //cleanup the user
        memberRepository.deleteById(response.getBody().getId());
    }

    @Test
    public void testMemberDuplicatedEmailOnRegistration() throws Exception {
        HttpEntity<Object> member1 = getHttpEntity(
                "{\"name\": \"Elon Musk\", \"email\": \"emuskmember@gmail.com\","
                        + " \"membershipStatus\": \"ACTIVE\",\"membershipStartDate\":\"2018-12-15T12:12:12\" }");

        HttpEntity<Object> member2 = getHttpEntity(
                "{\"name\": \"Larry Page\", \"email\": \"emuskmember@gmail.com\","
                        + " \"membershipStatus\": \"ACTIVE\",\"membershipStartDate\":\"2018-12-15T12:12:12\" }");

        ResponseEntity<Member> response1= template.postForEntity(
                "/api/member", member1, Member.class);

        Assert.assertEquals("Elon Musk", response1.getBody().getName());
        Assert.assertEquals(200, response1.getStatusCode().value());

        ResponseEntity<Member> response2 = template.postForEntity(
                "/api/member", member2, Member.class);

        Assert.assertEquals(400, response2.getStatusCode().value());

        //cleanup the user
        memberRepository.deleteById(response1.getBody().getId());
    }

    @Test
    public void testWrongNameStartOnRegistration() throws Exception {
        HttpEntity<Object> member = getHttpEntity(
                "{\"name\": \"1Elon Musk\", \"email\": \"emuskmember@gmail.com\","
                        + " \"membershipStatus\": \"ACTIVE\",\"membershipStartDate\":\"2018-12-15T12:12:12\" }");

        ResponseEntity<Member> response = template.postForEntity(
                "/api/member", member, Member.class);

        Assert.assertEquals(400, response.getStatusCode().value());
    }

    @Test
    public void testTooShortNameOnRegistration() throws Exception {
        HttpEntity<Object> member = getHttpEntity(
                "{\"name\": \"a\", \"email\": \"emuskmember@gmail.com\","
                        + " \"membershipStatus\": \"ACTIVE\",\"membershipStartDate\":\"2018-12-15T12:12:12\" }");

        ResponseEntity<Member> response = template.postForEntity(
                "/api/member", member, Member.class);

        Assert.assertEquals(400, response.getStatusCode().value());
    }

    @Test
    public void testTooLongNameOnRegistration() throws Exception {
        HttpEntity<Object> member = getHttpEntity(
                "{\"name\": \"asdasdasd asdas dasda sdas dasdasd asdasdadasdasdasd" +
                        " asd as dasdadadasdas asdasdasdas asdasdasdasdaax\", \"email\": \"emuskmember@gmail.com\","
                        + " \"membershipStatus\": \"ACTIVE\",\"membershipStartDate\":\"2018-12-15T12:12:12\" }");

        ResponseEntity<Member> response = template.postForEntity(
                "/api/member", member, Member.class);

        Assert.assertEquals(400, response.getStatusCode().value());
    }

    @Test
    public void getMemberByIdSuccessfully() throws Exception {

        HttpEntity<Object> member = getHttpEntity(
                "{\"name\": \"Morgan Freeman\", \"email\": \"mfreeman@gmail.com\","
                        + " \"membershipStatus\": \"ACTIVE\",\"membershipStartDate\":\"2018-12-15T12:12:12\" }");

        ResponseEntity<Member> response = template.postForEntity(
                "/api/member", member, Member.class);

        Assert.assertEquals("Morgan Freeman", response.getBody().getName());
        Assert.assertEquals(200, response.getStatusCode().value());

        Member m = template.getForObject("/api/member/"+response.getBody().getId(), Member.class);

        Assert.assertEquals("Morgan Freeman", m.getName());

        //cleanup the user
        memberRepository.deleteById(response.getBody().getId());

    }

    @Test
    public void getAllMembersSuccessfully() throws Exception {

        HttpEntity<Object> member = getHttpEntity(
                "{\"name\": \"Morgan Freeman\", \"email\": \"mfreeman@gmail.com\","
                        + " \"membershipStatus\": \"ACTIVE\",\"membershipStartDate\":\"2018-12-15T12:12:12\" }");

        ResponseEntity<Member> response = template.postForEntity(
                "/api/member", member, Member.class);

        Assert.assertEquals("Morgan Freeman", response.getBody().getName());
        Assert.assertEquals(200, response.getStatusCode().value());

        ResponseEntity<Member[]> responseEntity = template.getForEntity("/api/member", Member[].class);

        Assert.assertTrue((responseEntity.getBody().length > 0));

        //cleanup the user
        memberRepository.deleteById(response.getBody().getId());

    }

    private HttpEntity<Object> getHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<Object>(body, headers);
    }

}

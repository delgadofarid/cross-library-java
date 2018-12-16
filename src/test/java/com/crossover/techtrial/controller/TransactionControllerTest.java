/**
 *
 */
package com.crossover.techtrial.controller;

import com.crossover.techtrial.model.Book;
import com.crossover.techtrial.model.Transaction;
import com.crossover.techtrial.repositories.BookRepository;
import com.crossover.techtrial.repositories.MemberRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.crossover.techtrial.model.Member;
import com.crossover.techtrial.repositories.TransactionRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author kshah
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TransactionControllerTest {

    MockMvc mockMvc;

    @Mock
    private TransactionController transactionController;

    @Autowired
    private TestRestTemplate template;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    MemberRepository memberRepository;

    Book book;
    Member member;

    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
        HttpEntity<Object> httpBook = getHttpEntity(
                "{\"title\": \"Book X\" }");
        book = template.postForEntity(
                "/api/book", httpBook, Book.class).getBody();

        HttpEntity<Object> httpMember = getHttpEntity(
                "{\"name\": \"Larry Ellison\", \"email\": \"lellison@gmail.com\","
                        + " \"membershipStatus\": \"ACTIVE\",\"membershipStartDate\":\"2018-12-15T12:12:12\" }");

        member = template.postForEntity(
                "/api/member", httpMember, Member.class).getBody();
    }

    @Test
    public void testIssueBookToMemberSuccessful() throws Exception {

        HttpEntity<Object> transaction = getHttpEntity(
                "{\"bookId\": " + book.getId() + ", \"memberId\": " + member.getId() + " }");

        ResponseEntity<Transaction> response = template.postForEntity(
                "/api/transaction", transaction, Transaction.class);

        Assert.assertEquals(book.getTitle(), response.getBody().getBook().getTitle());
        Assert.assertEquals(member.getName(), response.getBody().getMember().getName());
        Assert.assertEquals(200, response.getStatusCode().value());

        //cleanup the transaction
        transactionRepository.deleteById(response.getBody().getId());
    }

    @Test
    public void testNotExistingBookOnTransactionRegistration() throws Exception {
        HttpEntity<Object> transaction = getHttpEntity(
                "{\"bookId\": 1010101010101, \"memberId\": " + member.getId() + " }");
        ResponseEntity<Transaction> response = template.postForEntity(
                "/api/transaction", transaction, Transaction.class);

        Assert.assertEquals(404, response.getStatusCode().value());
    }

    @Test
    public void testBookAlreadyBookedOnTransactionRegistration() throws Exception {
        HttpEntity<Object> transaction = getHttpEntity(
                "{\"bookId\": " + book.getId() + ", \"memberId\": " + member.getId() + " }");

        ResponseEntity<Transaction> response1 = template.postForEntity(
                "/api/transaction", transaction, Transaction.class);

        Assert.assertEquals(book.getTitle(), response1.getBody().getBook().getTitle());
        Assert.assertEquals(member.getName(), response1.getBody().getMember().getName());
        Assert.assertEquals(200, response1.getStatusCode().value());

        ResponseEntity<Transaction> response2 = template.postForEntity(
                "/api/transaction", transaction, Transaction.class);

        Assert.assertEquals(403, response2.getStatusCode().value());

        //cleanup the transaction
        transactionRepository.deleteById(response1.getBody().getId());
    }

    @Test
    public void testBookedBooksLimitReachedByUserOnTransactionRegistration() throws Exception {
        Book book1 = template.postForEntity(
                "/api/book", getHttpEntity(
                        "{\"title\": \"Book Limit Test 1\" }"), Book.class).getBody();

        HttpEntity<Object> transaction = getHttpEntity(
                "{\"bookId\": " + book1.getId() + ", \"memberId\": " + member.getId() + " }");
        ResponseEntity<Transaction> response1 = template.postForEntity(
                "/api/transaction", transaction, Transaction.class);

        Assert.assertEquals(book1.getTitle(), response1.getBody().getBook().getTitle());
        Assert.assertEquals(member.getName(), response1.getBody().getMember().getName());
        Assert.assertEquals(200, response1.getStatusCode().value());

        Book book2 = template.postForEntity(
                "/api/book", getHttpEntity(
                        "{\"title\": \"Book Limit Test 2\" }"), Book.class).getBody();

        transaction = getHttpEntity(
                "{\"bookId\": " + book2.getId() + ", \"memberId\": " + member.getId() + " }");
        ResponseEntity<Transaction> response2 = template.postForEntity(
                "/api/transaction", transaction, Transaction.class);

        Assert.assertEquals(book2.getTitle(), response2.getBody().getBook().getTitle());
        Assert.assertEquals(member.getName(), response2.getBody().getMember().getName());
        Assert.assertEquals(200, response2.getStatusCode().value());

        Book book3 = template.postForEntity(
                "/api/book", getHttpEntity(
                        "{\"title\": \"Book Limit Test 3\" }"), Book.class).getBody();

        transaction = getHttpEntity(
                "{\"bookId\": " + book3.getId() + ", \"memberId\": " + member.getId() + " }");
        ResponseEntity<Transaction> response3 = template.postForEntity(
                "/api/transaction", transaction, Transaction.class);

        Assert.assertEquals(book3.getTitle(), response3.getBody().getBook().getTitle());
        Assert.assertEquals(member.getName(), response3.getBody().getMember().getName());
        Assert.assertEquals(200, response3.getStatusCode().value());

        Book book4 = template.postForEntity(
                "/api/book", getHttpEntity(
                        "{\"title\": \"Book Limit Test 4\" }"), Book.class).getBody();

        transaction = getHttpEntity(
                "{\"bookId\": " + book4.getId() + ", \"memberId\": " + member.getId() + " }");
        ResponseEntity<Transaction> response4 = template.postForEntity(
                "/api/transaction", transaction, Transaction.class);

        Assert.assertEquals(book4.getTitle(), response4.getBody().getBook().getTitle());
        Assert.assertEquals(member.getName(), response4.getBody().getMember().getName());
        Assert.assertEquals(200, response4.getStatusCode().value());

        Book book5 = template.postForEntity(
                "/api/book", getHttpEntity(
                        "{\"title\": \"Book Limit Test 5\" }"), Book.class).getBody();

        transaction = getHttpEntity(
                "{\"bookId\": " + book5.getId() + ", \"memberId\": " + member.getId() + " }");
        ResponseEntity<Transaction> response5 = template.postForEntity(
                "/api/transaction", transaction, Transaction.class);

        Assert.assertEquals(book5.getTitle(), response5.getBody().getBook().getTitle());
        Assert.assertEquals(member.getName(), response5.getBody().getMember().getName());
        Assert.assertEquals(200, response5.getStatusCode().value());

        Book book6 = template.postForEntity(
                "/api/book", getHttpEntity(
                        "{\"title\": \"Book Limit Test 6\" }"), Book.class).getBody();

        transaction = getHttpEntity(
                "{\"bookId\": " + book6.getId() + ", \"memberId\": " + member.getId() + " }");
        ResponseEntity<Transaction> response6 = template.postForEntity(
                "/api/transaction", transaction, Transaction.class);

        Assert.assertEquals(403, response6.getStatusCode().value());

        //cleanup the transaction
        transactionRepository.deleteById(response1.getBody().getId());
        //cleanup the transaction
        transactionRepository.deleteById(response2.getBody().getId());
        //cleanup the transaction
        transactionRepository.deleteById(response3.getBody().getId());
        //cleanup the transaction
        transactionRepository.deleteById(response4.getBody().getId());
        //cleanup the transaction
        transactionRepository.deleteById(response5.getBody().getId());

    }

    @Test
    public void testMembershipProblemOnRegistration () {
        HttpEntity<Object> httpMember = getHttpEntity(
                "{\"name\": \"Larry Inactive Ellison\", \"email\": \"lellisoninactive@gmail.com\","
                        + " \"membershipStatus\": \"INACTIVE\",\"membershipStartDate\":\"2018-12-15T12:12:12\" }");

        Member memberInactive = template.postForEntity(
                "/api/member", httpMember, Member.class).getBody();

        HttpEntity<Object> transaction = getHttpEntity(
                "{\"bookId\": " + book.getId() + ", \"memberId\": " + memberInactive.getId() + " }");

        ResponseEntity<Transaction> response = template.postForEntity(
                "/api/transaction", transaction, Transaction.class);

        Assert.assertEquals(403, response.getStatusCode().value());

        //cleanup the user
        memberRepository.deleteById(memberInactive.getId());
    }

    @Test
    public void registerBookReturnSuccessfully () throws Exception {

        HttpEntity<Object> transaction = getHttpEntity(
                "{\"bookId\": " + book.getId() + ", \"memberId\": " + member.getId() + " }");

        ResponseEntity<Transaction> response = template.postForEntity(
                "/api/transaction", transaction, Transaction.class);

        Assert.assertEquals(book.getTitle(), response.getBody().getBook().getTitle());
        Assert.assertEquals(member.getName(), response.getBody().getMember().getName());
        Assert.assertNotEquals(null, response.getBody().getDateOfIssue());
        Assert.assertEquals(200, response.getStatusCode().value());

        String uri = "/api/transaction/" + response.getBody().getId() + "/return";

        mockMvc.perform(patch(uri)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //cleanup the transaction
        transactionRepository.deleteById(response.getBody().getId());
    }

    private HttpEntity<Object> getHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<Object>(body, headers);
    }

    @After
    public void beforeEnd () {
        //cleanup the book
        bookRepository.deleteById(book.getId());
        //cleanup the user
        memberRepository.deleteById(member.getId());
    }

}

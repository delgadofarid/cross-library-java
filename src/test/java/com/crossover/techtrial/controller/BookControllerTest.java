package com.crossover.techtrial.controller;

import com.crossover.techtrial.model.Book;
import com.crossover.techtrial.repositories.BookRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {

    MockMvc mockMvc;

    @Mock
    private BookController bookController;

    @Autowired
    private TestRestTemplate template;

    @Autowired
    BookRepository bookRepository;

    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    public void testBookRegistrationSuccessful() throws Exception {
        HttpEntity<Object> book = getHttpEntity(
                "{\"title\": \"Book X\" }");

        ResponseEntity<Book> response = template.postForEntity(
                "/api/book", book, Book.class);

        Assert.assertEquals("Book X", response.getBody().getTitle());
        Assert.assertEquals(200, response.getStatusCode().value());

        //cleanup the book
        bookRepository.deleteById(response.getBody().getId());
    }

    @Test
    public void getBookByIdSuccessfully() throws Exception {

        HttpEntity<Object> book = getHttpEntity(
                "{\"title\": \"Book X\" }");

        ResponseEntity<Book> response = template.postForEntity(
                "/api/book", book, Book.class);

        Assert.assertEquals("Book X", response.getBody().getTitle());
        Assert.assertEquals(200, response.getStatusCode().value());


        Book m = template.getForObject("/api/book/"+response.getBody().getId(), Book.class);

        Assert.assertEquals("Book X", m.getTitle());

        //cleanup the book
        bookRepository.deleteById(response.getBody().getId());

    }

    @Test
    public void getAllBooksSuccessfully() throws Exception {

        HttpEntity<Object> book = getHttpEntity(
                "{\"title\": \"Book X\" }");

        ResponseEntity<Book> response = template.postForEntity(
                "/api/book", book, Book.class);

        Assert.assertEquals("Book X", response.getBody().getTitle());
        Assert.assertEquals(200, response.getStatusCode().value());

        ResponseEntity<Book[]> responseEntity = template.getForEntity("/api/book", Book[].class);

        Assert.assertTrue((responseEntity.getBody().length > 0));

        //cleanup the book
        bookRepository.deleteById(response.getBody().getId());

    }

    private HttpEntity<Object> getHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<Object>(body, headers);
    }

}

package br.com.phoebus.livraria.controller;

import br.com.phoebus.livraria.exception.AuthorNotFoundException;
import br.com.phoebus.livraria.exception.BookNotFoundException;
import br.com.phoebus.livraria.model.Author;
import br.com.phoebus.livraria.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HttpServletBean;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.Collection;

@RestController
@RequestMapping("/books")
public class BookRestController {

    private static Logger log = LoggerFactory.getLogger(BookRestController.class);
    private AuthorRepository authorRepository;
    private BookRepository bookRepository;

    public BookRestController(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public Iterable<Book> getBooks() {
        return this.bookRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> addBook(@RequestBody Book book) {
        Book result = bookRepository.save(book);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(result.getId()).toUri();

        result.setUri(location.toString());

        bookRepository.save(result);

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{bookId}")
    public Book getBook(@PathVariable Long bookId) {
        return this.bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
    }

    @PostMapping("{bookId}/authors")
    public ResponseEntity<?> addAuthorToBook(@PathVariable Long bookId, @RequestParam(value = "authorId") Long authorId) {
        Author author = validateAuthor(authorId);
        Book book = validateBook(bookId);

        book.getAuthors().add(author);

        this.bookRepository.save(book);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri().path("/{id}")
                .buildAndExpand(author.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<?> updateBook(@PathVariable Long bookId, @RequestBody Book book) {
        try {
            this.bookRepository.findById(bookId).ifPresent(result -> {
                book.setId(bookId);
                book.setUri(result.getUri());
                this.bookRepository.save(book);
            });
            return ResponseEntity.ok().build();
        } catch(EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{bookId}")
    public ResponseEntity<?> deleteBook(@PathVariable Long bookId) {
        try{
            this.bookRepository.deleteById(bookId);
            return ResponseEntity.ok().build();
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private Author validateAuthor(Long authorId) {
        return this.authorRepository.findById(authorId)
                .orElseThrow(() -> new AuthorNotFoundException(authorId));
    }

    private Book validateBook(Long bookId) {
        return this.bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
    }

}

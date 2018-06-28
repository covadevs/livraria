package br.com.phoebus.livraria.controller;

import br.com.phoebus.livraria.exception.AuthorNotFoundException;
import br.com.phoebus.livraria.model.Author;
import br.com.phoebus.livraria.model.Body;
import br.com.phoebus.livraria.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/authors")
public class AuthorRestController {
    public static Logger log = LoggerFactory.getLogger(AuthorRestController.class);

    private AuthorRepository authorRepository;
    private BookRepository bookRepository;
    private Body body;

    public AuthorRestController(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.body = new Body();
    }

    @GetMapping
    public Iterable getAuthors() {
        return this.authorRepository.findAll();
    }

    @GetMapping("/{authorId}")
    public Author getAuthor(@PathVariable Long authorId) {
        return getAuthorResult(authorId);
    }

    //Duvida
    //Como dar um get nos livros do author apenas com seu id sem usar o find author
    //e passar o objeto

    @GetMapping("/{authorId}/books")
    public Collection<Book> getBooksFromAuthor(@PathVariable Long authorId) {
        Author result = getAuthorResult(authorId);
        return this.bookRepository.findBooksByAuthorsContaining(result);
    }

    @GetMapping("/books")
    public Collection<Book> getBooksNotContainsAuthor(@RequestParam(value = "authorId") Long authorId) {
        Author result = getAuthorResult(authorId);
        return  this.bookRepository.findBooksByAuthorsNotContaining(result);
    }

    @PostMapping
    public ResponseEntity<?> addAuthor(@RequestBody Author author) {
        body.getBody().clear();

        boolean authorExists = this.authorRepository.findByName(author.getName()).isPresent();
        if (authorExists) {
            body.getBody().put("message", "author name exists");
            return ResponseEntity.ok(body.getBody());
        }

        Author result = this.authorRepository.save(author);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(result.getId()).toUri();

        result.setUri(location.toString());

        this.authorRepository.save(result);

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{authorId}")
    public ResponseEntity<?> updateAuthor(@RequestBody Author author, @PathVariable Long authorId) {
        try {
            body.getBody().clear();

            boolean authorsExists = this.authorRepository.findByName(author.getName()).isPresent();
            if (authorsExists) {
                body.getBody().put("message", "author name exists");
                return ResponseEntity.ok(body.getBody());
            }

            this.authorRepository.findById(authorId).ifPresent(result -> {
                author.setId(result.getId());
                author.setUri(result.getUri());
                this.authorRepository.save(author);
            });

            return ResponseEntity.ok(body.getBody());
        } catch(EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{authorId}")
    public ResponseEntity<?> deleteAuthor(@PathVariable Long authorId) {
        try {
            Author result = getAuthorResult(authorId);
            this.bookRepository.findBooksByAuthorsContaining(result).forEach(book -> {
                book.getAuthors().remove(result);
                this.bookRepository.save(book);
            });
            this.authorRepository.deleteById(authorId);
            return ResponseEntity.ok().build();
        } catch(EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{authorId}/books/{bookId}")
    public ResponseEntity<?> removeAuthorFromBook(@PathVariable Long authorId, @PathVariable Long bookId) {
        try {
            Book result = getBookResult(bookId);
            Author author = getAuthorResult(authorId);

            result.getAuthors().remove(author);

            this.bookRepository.save(result);
            return ResponseEntity.ok().build();
        } catch(EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private Author getAuthorResult(@PathVariable Long authorId) {
        return this.authorRepository.findById(authorId)
                .orElseThrow(() -> new AuthorNotFoundException(authorId));
    }

    private Book getBookResult(@PathVariable Long bookId) {
        return this.bookRepository.findById(bookId)
                .orElseThrow(() -> new AuthorNotFoundException(bookId));
    }
}

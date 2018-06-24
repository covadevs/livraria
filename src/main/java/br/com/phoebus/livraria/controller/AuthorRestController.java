package br.com.phoebus.livraria.controller;

import br.com.phoebus.livraria.model.Author;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/authors")
public class AuthorRestController {
    public static Logger log = LoggerFactory.getLogger(AuthorRestController.class);

    private AuthorRepository authorRepository;
    private BookRepository bookRepository;

    public AuthorRestController(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public Iterable getAuthors() {
        return this.authorRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> addAuthor(@RequestBody Author author) {
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
            this.authorRepository.findById(authorId).ifPresent(result -> {
                author.setId(result.getId());
                author.setUri(result.getUri());
                this.authorRepository.save(author);
            });

            return ResponseEntity.ok().build();
        } catch(EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{authorId}")
    public ResponseEntity deleteAuthor(@PathVariable Long authorId) {
        try {
            this.authorRepository.deleteById(authorId);
            return ResponseEntity.ok().build();
        } catch(EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

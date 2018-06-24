package br.com.phoebus.livraria.controller;

import br.com.phoebus.livraria.exception.AuthorNotFoundException;
import br.com.phoebus.livraria.model.Author;
import br.com.phoebus.livraria.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;

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

    @DeleteMapping("/{authorId}/books/{bookId}")
    public ResponseEntity<?> removeAuthorFromBook(@PathVariable Long authorId, @PathVariable Long bookId) {
        Author author = getAuthorResult(authorId);
        Book book = getBookResult(bookId);
        book.getAuthors().remove(author);

        this.bookRepository.save(book);

        return ResponseEntity.ok().build();
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

    private Author getAuthorResult(@PathVariable Long authorId) {
        return this.authorRepository.findById(authorId)
                .orElseThrow(() -> new AuthorNotFoundException(authorId));
    }

    private Book getBookResult(@PathVariable Long bookId) {
        return this.bookRepository.findById(bookId)
                .orElseThrow(() -> new AuthorNotFoundException(bookId));
    }
}

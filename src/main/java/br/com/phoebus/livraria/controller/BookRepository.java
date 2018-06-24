package br.com.phoebus.livraria.controller;

import br.com.phoebus.livraria.model.Book;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.Optional;

public interface BookRepository extends CrudRepository<Book, Long> {

    Optional<Book> findBookByTitle(String title);
    Optional<Book> findBookByDescription(String description);
    Collection<Book> findAllByTitleContaining(String title);
    Collection<Book> findBooksByAuthors(String name);
}

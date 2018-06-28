package br.com.phoebus.livraria.controller;

import br.com.phoebus.livraria.model.Author;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.Optional;

public interface AuthorRepository extends CrudRepository<Author, Long> {

    Optional<Author> findByName(String name);
    boolean existsByName(String name);
    Collection<Author> findAllByNameContaining(String name);
}

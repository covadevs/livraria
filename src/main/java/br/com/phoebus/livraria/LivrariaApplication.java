package br.com.phoebus.livraria;

import br.com.phoebus.livraria.controller.AuthorRepository;
import br.com.phoebus.livraria.model.Author;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Arrays;

@SpringBootApplication
public class LivrariaApplication {

    public static void main(String[] args) {
        SpringApplication.run(LivrariaApplication.class, args);
    }

    @Bean
    public CommandLineRunner init(AuthorRepository authorRepository) {
        return (evt) -> Arrays.asList("leonardo,rafaela,sam,joao,jose".split(","))
                .forEach(element -> {
                    try {
                        Author author = authorRepository.save(new Author(element));
                    } catch(DataIntegrityViolationException exception) {
                        exception.getMessage();
                    }
                });
    }
}

package br.com.phoebus.livraria;

import br.com.phoebus.livraria.controller.AuthorRepository;
import br.com.phoebus.livraria.model.Author;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Arrays;
import java.util.concurrent.Executor;

@SpringBootApplication
public class LivrariaApplication {

    public static void main(String[] args) {
        SpringApplication.run(LivrariaApplication.class, args);
    }
}

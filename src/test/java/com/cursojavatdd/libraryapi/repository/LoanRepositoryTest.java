package com.cursojavatdd.libraryapi.repository;

import com.cursojavatdd.libraryapi.entity.Book;
import com.cursojavatdd.libraryapi.entity.Loan;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

    @Autowired
    LoanRepository loanRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    @DisplayName("Deve verificar se existe empréstimo ativo para o livro")
    public void findLoansTest() {
        Book book = Book.builder().build();
        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).loanDate(LocalDate.now()).build();
        entityManager.persist(loan);

        boolean exists = loanRepository.existsByBookAndNotReturned(book);

        Assertions.assertThat(exists).isTrue();
    }
}

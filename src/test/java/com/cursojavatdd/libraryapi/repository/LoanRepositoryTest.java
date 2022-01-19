package com.cursojavatdd.libraryapi.repository;

import com.cursojavatdd.libraryapi.entity.Book;
import com.cursojavatdd.libraryapi.entity.Loan;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

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
        Book book = new Book();
        entityManager.persist(book);

        Loan loan = new Loan(null, "User", "user_email@mail.com", LocalDate.now(), null, book);
        entityManager.persist(loan);

        boolean exists = loanRepository.existsByBookAndNotReturned(book);

        Assertions.assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve obter empréstimo por isbn")
    public void findByBookIsbnTest () {
        Book book = new Book();
        book.setIsbn("001");
        entityManager.persist(book);

        Loan loan = new Loan(null, "User", "user_email@mail.com", LocalDate.now(), null, book);
        entityManager.persist(loan);

        Page<Loan> result = loanRepository.findByBookIsbnOrCustomer("001", Mockito.anyString(), PageRequest.of(0, 10));

        Assertions.assertThat(result.getContent()).hasSize(1);
        Assertions.assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        Assertions.assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        Assertions.assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve obter empréstimo por customer")
    public void findByCustomerTest () {
        Book book = new Book();
        book.setIsbn("001");
        entityManager.persist(book);

        Loan loan = new Loan(null, "User", "user_email@mail.com", LocalDate.now(), null, book);
        entityManager.persist(loan);

        Page<Loan> result = loanRepository.findByBookIsbnOrCustomer(Mockito.anyString(), "User" , PageRequest.of(0, 10));

        Assertions.assertThat(result.getContent()).hasSize(1);
        Assertions.assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        Assertions.assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        Assertions.assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve obter empréstimos cuja data seja menor ou igual a 7 dias atrás e não retornados")
    public void findByLoanDateLessThanAndNotReturned() {
        Book book = new Book();
        book.setIsbn("001");
        entityManager.persist(book);

        Loan loan = new Loan(null, "User", "user_email@mail.com", LocalDate.now().minusDays(8), null, book);
        entityManager.persist(loan);

        List<Loan> result = this.loanRepository.findByLoanDateLessThanNotReturned(LocalDate.now().minusDays(7));

        Assertions.assertThat(result).hasSize(1).contains(loan);
    }
    @Test
    @DisplayName("Não deve obter empréstimos cuja data seja menor ou igual a 7 dias atrás e não retornados")
    public void notFindByLoanDateLessThanAndNotReturned() {
        Book book = new Book();
        book.setIsbn("001");
        entityManager.persist(book);

        Loan loan = new Loan(null, "User", "user_email@mail.com", LocalDate.now().minusDays(1), null, book);
        entityManager.persist(loan);

        List<Loan> result = this.loanRepository.findByLoanDateLessThanNotReturned(LocalDate.now().minusDays(7));

        Assertions.assertThat(result).isEmpty();
    }
}

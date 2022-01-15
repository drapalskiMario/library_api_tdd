package com.cursojavatdd.libraryapi.service;

import com.cursojavatdd.libraryapi.entity.Book;
import com.cursojavatdd.libraryapi.entity.Loan;
import com.cursojavatdd.libraryapi.exception.BusinessException;
import com.cursojavatdd.libraryapi.repository.LoanRepository;
import com.cursojavatdd.libraryapi.service.impl.LoanServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    LoanService loanService;

    @MockBean
    LoanRepository loanRepository;

    private Loan createMockLoan() {
        return Loan
                .builder()
                .book(Book.builder().id(1L).build())
                .customer("User")
                .loanDate(LocalDate.now())
                .build();
    }

    @BeforeEach
    public void setUp() {
        this.loanService = new LoanServiceImpl(loanRepository);
    }

    @Test
    @DisplayName("Deve salvar um empréstimo")
    public void saveLoanTest() {
        Loan savingLoan = createMockLoan();

        Loan savedLoan = savingLoan;
        savedLoan.setId(101L);

        Mockito.when(loanRepository.existsByBookAndNotReturned(savingLoan.getBook())).thenReturn(false);
        Mockito.when(loanRepository.save(savingLoan)).thenReturn(savedLoan);

        Loan loan = loanService.save(savingLoan);

        Assertions.assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        Assertions.assertThat(loan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
        Assertions.assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
    }

    @Test
    @DisplayName("Deve lançar erro de negócio ao salvar empréstimo com livro já emprestado")
    public void loanedBookSaveTest() {
        Loan savingLoan = createMockLoan();

        Mockito.when(loanRepository.existsByBookAndNotReturned(savingLoan.getBook())).thenReturn(true);
        Throwable exception = Assertions.catchThrowable(() -> loanService.save(savingLoan));
        Assertions
                .assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Livro já emprestado");
        Mockito.verify(loanRepository, Mockito.never()).save(savingLoan);
    }

    @Test
    @DisplayName("Deve obter as informações de um empréstimo por id")
    public void getLoanByIdDetailsTest() {
        Long id = 101L;
        Loan loan = createMockLoan();
        loan.setId(id);

        Mockito.when(loanRepository.findById(id)).thenReturn(Optional.of(loan));

        Optional<Loan> result = loanService.getById(id);

        Assertions.assertThat(result.isPresent()).isTrue();
        Assertions.assertThat(result.get().getId()).isEqualTo(id);
        Mockito.verify(loanRepository, Mockito.times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve atualizar um empréstimo")
    public void updateLoanTest() {
        Long id = 101L;
        Loan loan = createMockLoan();
        loan.setId(id);

        Mockito.when(loanRepository.save(loan)).thenReturn(loan);

        Loan updatedLoan = loanService.update(loan);
        loan.setReturned(true);

        Assertions.assertThat(updatedLoan.getReturned()).isTrue();
        Mockito.verify(loanRepository, Mockito.times(1)).save(loan);
    }
}

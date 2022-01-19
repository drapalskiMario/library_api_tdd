package com.cursojavatdd.libraryapi.service.impl;

import com.cursojavatdd.libraryapi.api.dto.LoanFilterDTO;
import com.cursojavatdd.libraryapi.entity.Book;
import com.cursojavatdd.libraryapi.entity.Loan;
import com.cursojavatdd.libraryapi.exception.BusinessException;
import com.cursojavatdd.libraryapi.repository.LoanRepository;
import com.cursojavatdd.libraryapi.service.LoanService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LoanServiceImpl implements LoanService {

    private LoanRepository loanRepository;

    public LoanServiceImpl(LoanRepository loanRepository) { this.loanRepository = loanRepository; }

    @Override
    public Loan save(Loan loan) {
        if(this.loanRepository.existsByBookAndNotReturned(loan.getBook())) {
            throw new BusinessException("Livro já emprestado");
        }
        return this.loanRepository.save(loan);
    }

    @Override
    public Optional<Loan> getById(Long id) {
        return this.loanRepository.findById(id);
    }

    @Override
    public Loan update(Loan loan) {
        return this.loanRepository.save(loan);
    }

    @Override
    public Page<Loan> find(LoanFilterDTO filter, Pageable pageable) {
        return this.loanRepository.findByBookIsbnOrCustomer(filter.getIsbn(), filter.getCustomer(), pageable);
    }

    @Override
    public Page<Loan> getLoansByBook(Book book, Pageable pageable) {
        return this.loanRepository.findByBook(book, pageable);
    }

    @Override
    public List<Loan> getAllLateLoans() {
        Integer loansDays = 7;
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(loansDays);
        return this.loanRepository.findByLoanDateLessThanNotReturned(sevenDaysAgo);
    }
}

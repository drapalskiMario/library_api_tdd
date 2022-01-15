package com.cursojavatdd.libraryapi.service;

import com.cursojavatdd.libraryapi.api.dto.LoanFilterDTO;
import com.cursojavatdd.libraryapi.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface LoanService {
    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);

    Page<Loan> find(LoanFilterDTO filter, Pageable pageable);
}

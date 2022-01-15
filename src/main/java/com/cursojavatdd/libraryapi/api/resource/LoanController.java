package com.cursojavatdd.libraryapi.api.resource;

import com.cursojavatdd.libraryapi.api.dto.LoanDTO;
import com.cursojavatdd.libraryapi.api.service.LoanService;
import com.cursojavatdd.libraryapi.entity.Book;
import com.cursojavatdd.libraryapi.entity.Loan;
import com.cursojavatdd.libraryapi.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private BookService bookService;
    private LoanService loanService;

    public LoanController(BookService bookService, LoanService loanService) {
        this.bookService = bookService;
        this.loanService = loanService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDTO loanDTO) {
        Optional<Book> book = this.bookService.getByIsbn(loanDTO.getIsbn());
        if (book.isPresent()) {
            Loan entity = Loan
                    .builder()
                    .customer(loanDTO.getCustomer())
                    .loanDate(LocalDate.now())
                    .book(book.get())
                    .build();

            entity = this.loanService.save(entity);
            return entity.getId();
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Livro não encontrado para o isbn informado");
    }
}

package com.cursojavatdd.libraryapi.api.resource;

import com.cursojavatdd.libraryapi.api.dto.BookDTO;
import com.cursojavatdd.libraryapi.api.dto.LoanDTO;
import com.cursojavatdd.libraryapi.api.dto.LoanFilterDTO;
import com.cursojavatdd.libraryapi.api.dto.ReturnedLoanDTO;
import com.cursojavatdd.libraryapi.service.LoanService;
import com.cursojavatdd.libraryapi.entity.Book;
import com.cursojavatdd.libraryapi.entity.Loan;
import com.cursojavatdd.libraryapi.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public Long create(@RequestBody @Valid LoanDTO loanDTO) {
        Optional<Book> book = this.bookService.getByIsbn(loanDTO.getIsbn());
        if (book.isPresent()) {
            Loan entity = new Loan(loanDTO.getId(), loanDTO.getCustomer(), loanDTO.getEmail(), LocalDate.now(), null, book.get());
            entity = this.loanService.save(entity);
            return entity.getId();
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Livro não encontrado para o isbn informado");
    }

    @GetMapping
    public Page<LoanDTO> find(LoanFilterDTO loanFilterDTO, Pageable pageRequest) {
        Page<Loan> result = this.loanService.find(loanFilterDTO, pageRequest);
        List<LoanDTO> loans = result
                .getContent()
                .stream()
                .map(loan -> {
                    BookDTO bookDTO = new BookDTO(loan.getBook().getId(), loan.getBook().getTitle(), loan.getBook().getAuthor(), loan.getBook().getIsbn());
                    LoanDTO loanDTO = new LoanDTO(loan.getId(), loan.getBook().getIsbn(), loan.getCustomer(), loan.getCustomerEmail(), bookDTO);
                    return loanDTO;
                }).collect(Collectors.toList());
        return new PageImpl<LoanDTO>(loans, pageRequest, result.getTotalElements());
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void returnedBook(@PathVariable Long id, @RequestBody ReturnedLoanDTO dto){
        Loan loan = this.loanService.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        loan.setReturned(dto.getReturned());
        this.loanService.update(loan);
    }

}

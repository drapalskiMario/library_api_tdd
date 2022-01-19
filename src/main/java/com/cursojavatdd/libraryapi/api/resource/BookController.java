package com.cursojavatdd.libraryapi.api.resource;

import com.cursojavatdd.libraryapi.api.dto.BookDTO;
import com.cursojavatdd.libraryapi.api.dto.LoanDTO;
import com.cursojavatdd.libraryapi.entity.Book;
import com.cursojavatdd.libraryapi.entity.Loan;
import com.cursojavatdd.libraryapi.service.BookService;
import com.cursojavatdd.libraryapi.service.LoanService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private BookService bookService;
    private LoanService loanService;

    public BookController(BookService bookService, LoanService loanService) {
        this.bookService = bookService;
        this.loanService = loanService;
    }

    @GetMapping("/{id}")
    public BookDTO getBookById(@PathVariable Long id) {
        return bookService
                .getById(id)
                .map(book -> {
                    return new BookDTO(book.getId(), book.getTitle(), book.getAuthor(), book.getIsbn());
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

    @GetMapping
    public Page<BookDTO> find(BookDTO bookDTO, Pageable pageRequest) {
        Book filter = new Book();
        filter.setTitle(bookDTO.getTitle());
        filter.setAuthor(bookDTO.getTitle());

        Page<Book> result = this.bookService.find(filter, pageRequest);

        List<BookDTO> list = result
                .getContent()
                .stream()
                .map(entity ->
                    new BookDTO(entity.getId(), entity.getTitle(), entity.getAuthor(), entity.getIsbn()))
                .collect(Collectors.toList());

        return new PageImpl<BookDTO>(list, pageRequest, result.getTotalElements());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody @Valid BookDTO bookDTO) {

        Book entity = new Book();
        entity.setAuthor(bookDTO.getAuthor());
        entity.setTitle(bookDTO.getTitle());
        entity.setIsbn(bookDTO.getIsbn());

        Book savedEntity = this.bookService.save(entity);

        return new BookDTO(savedEntity.getId(), savedEntity.getTitle(), savedEntity.getAuthor(), savedEntity.getIsbn());
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable Long id, @RequestBody @Valid BookDTO bookDTO) {
        Book book =  this.bookService
                .getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        this.bookService.update(book, bookDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Book book =  this.bookService
                .getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        this.bookService.delete(book);
    }


    @GetMapping("/{id}/loans")
    public Page<LoanDTO> loansByBook (@PathVariable Long id, Pageable pageable) {
        Book book = this.bookService
                .getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Page<Loan> result = this.loanService.getLoansByBook(book, pageable);

        List<LoanDTO> list = result
                .getContent()
                .stream()
                .map(entity -> {
                    BookDTO bookDTO = new BookDTO(entity.getBook().getId(), entity.getBook().getTitle(), entity.getBook().getAuthor(), entity.getBook().getIsbn());
                    return new LoanDTO( entity.getId(), entity.getBook().getIsbn(), entity.getCustomer(), entity.getCustomerEmail(), bookDTO);
                }).collect(Collectors.toList());

        return new PageImpl<LoanDTO>(list, pageable, result.getTotalElements());
    }

}

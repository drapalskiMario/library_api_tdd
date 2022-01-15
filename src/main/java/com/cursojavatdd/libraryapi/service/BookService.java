package com.cursojavatdd.libraryapi.service;

import com.cursojavatdd.libraryapi.api.dto.BookDTO;
import com.cursojavatdd.libraryapi.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BookService {

    Book save(Book book);

    Optional<Book> getById(Long id);

    void delete(Book book);

    void update(Book book, BookDTO bookDTO);

    Page<Book> find(Book filter, Pageable pageRequest);

    Optional<Book> getByIsbn(String isbn);
}

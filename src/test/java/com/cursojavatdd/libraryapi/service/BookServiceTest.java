package com.cursojavatdd.libraryapi.service;

import com.cursojavatdd.libraryapi.api.dto.BookDTO;
import com.cursojavatdd.libraryapi.entity.Book;
import com.cursojavatdd.libraryapi.exception.BusinessException;
import com.cursojavatdd.libraryapi.repository.BookRepository;
import com.cursojavatdd.libraryapi.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService bookService;

    @MockBean
    BookRepository bookRepository;

    @BeforeEach
    public void setUp() {
        this.bookService = new BookServiceImpl(bookRepository);
    }



    private Book createSavedBook() {
        return new Book(1L, "As aventuras do Rei", "Arthur", "001", null);
    }


    private Book createMockBook() {
        return new Book(null, "As aventuras do Rei", "Arthur", "001", null);
    }

    private BookDTO createMockBookDTO() {
        return new BookDTO(null, "As aventuras do Rei", "Arthur", "001");
    }


    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest() {
        Book book = this.createSavedBook();

        Mockito.when(bookRepository.save(book)).thenReturn(this.createSavedBook());
        Mockito.when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(false);

        Book savedBook = bookService.save(book);

        Assertions.assertThat(savedBook.getId()).isNotNull();
        Assertions.assertThat(savedBook.getIsbn()).isEqualTo("001");
        Assertions.assertThat(savedBook.getTitle()).isEqualTo("As aventuras do Rei");
        Assertions.assertThat(savedBook.getAuthor()).isEqualTo("Arthur");
    }

    @Test
    @DisplayName("Deve lançar erro de negócio ao tentar salvar um livro com isbn duplicado")
    public void shouldNotSaveBookWithDuplicatedISBN() {
        Book book = this.createMockBook();
        Mockito.when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        Throwable exception = Assertions.catchThrowable(() -> bookService.save(book));

        Assertions.assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn já cadastrado.");

        Mockito.verify(bookRepository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Deve obter um livro por id")
    public void getBookByIdTest() {
        Long id = 1L;
        Book bookSaved = this.createSavedBook();
        Mockito.when(this.bookRepository.findById(id)).thenReturn(Optional.of(bookSaved));

        Optional<Book> foundBook = this.bookService.getById(id);

        Assertions.assertThat(foundBook.isPresent()).isTrue();
        Assertions.assertThat(foundBook.get().getId()).isEqualTo(bookSaved.getId());
        Assertions.assertThat(foundBook.get().getAuthor()).isEqualTo(bookSaved.getAuthor());
        Assertions.assertThat(foundBook.get().getTitle()).isEqualTo(bookSaved.getTitle());
        Assertions.assertThat(foundBook.get().getIsbn()).isEqualTo(bookSaved.getIsbn());
    }

    @Test
    @DisplayName("Deve retornar vazio quando não existir um livro com o Id informado na base")
    public void getInexistentBookByIdTest() {
        Long id = 1L;
        Mockito.when(this.bookRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Book> foundBook = this.bookService.getById(id);

        Assertions.assertThat(foundBook.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest() {
        Book book = this.createSavedBook();

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> this.bookService.delete(book));
        Mockito.verify(bookRepository, Mockito.times(1)).delete(book);
    }

    @Test
    @DisplayName("Deve ocorrer ao deletar um livro inexistente")
    public void deleteInexistentBookTest() {
        Book book = new Book();

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> this.bookService.delete(book));

        Mockito.verify(bookRepository, Mockito.never()).delete(book);
    }

    @Test
    @DisplayName("Deve ocorrer erro ao atualizar um livro inexistente")
    public void updateInexistentBookTest() {
        Book book = new Book();
        BookDTO bookDTO = this.createMockBookDTO();

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> this.bookService.update(book, bookDTO));

        Mockito.verify(bookRepository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateBookTest() {
        String bookTitle = "Alterado";
        Book book = this.createSavedBook();
        BookDTO bookDTO = this.createMockBookDTO();

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> this.bookService.update(book, bookDTO));

        Mockito.verify(bookRepository, Mockito.times(1)).save(book);
    }

    @Test
    @DisplayName("Deve filtrar livros pelas propriedades")
    public void findBookTest() {
        Book book = this.createSavedBook();
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Book> lista = Arrays.asList(book);
        Page<Book> page = new PageImpl<Book>(Arrays.asList(book), pageRequest, 1);

        Mockito.when(bookRepository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        Page<Book> result = bookService.find(book, pageRequest);

        Assertions.assertThat(result.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(result.getContent()).isEqualTo(lista);
        Assertions.assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        Assertions.assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("Deve obter um livro por Isbn")
    public void getBookByIsbnTest() {
        String isbn = "001";
        Mockito.when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(this.createSavedBook()));

        Optional<Book> book = bookService.getByIsbn(isbn);

        Assertions.assertThat(book.isPresent()).isTrue();
        Assertions.assertThat(book.get().getId()).isEqualTo(createSavedBook().getId());
        Assertions.assertThat(book.get().getIsbn()).isEqualTo(isbn);

        Mockito.verify(bookRepository, Mockito.times(1)).findByIsbn(isbn);
    }
}

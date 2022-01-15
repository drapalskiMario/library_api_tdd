package com.cursojavatdd.libraryapi.repository;

import com.cursojavatdd.libraryapi.entity.Book;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository bookRepository;

    private Book createMockBook() {
        return Book
                .builder()
                .author("Arthur")
                .title("As aventuras do Rei")
                .isbn("123")
                .build();
    }

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um livro na base com o isbn informado")
    public void returnTrueWhenIsbnExists() {
        String isbn = "123";
        Book book = this.createMockBook();
        entityManager.persist(book);

        boolean exists = bookRepository.existsByIsbn(isbn);
        Assertions.assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar falso quando não existir um livro na base com o isbn informado")
    public void returnFalseWhenIsbnDoesntExist() {
        String isbn = "123";
        boolean exists = bookRepository.existsByIsbn(isbn);
        Assertions.assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Deve obter um livro por id")
    public void findByIdTest() {
        Book book = this.createMockBook();
        entityManager.persist(book);

        Optional<Book> bookFound = bookRepository.findById(book.getId());

        Assertions.assertThat(bookFound.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest() {
        Book book = createMockBook();
        Book savedBook = bookRepository.save(book);

        Assertions.assertThat(savedBook.getId()).isNotNull();
    }

    @Test
    @DisplayName("Deve excluir um livro")
    public void deleteBook() {
        Book book = createMockBook();
        entityManager.persist(book);

        Book foundBook = entityManager.find(Book.class, book.getId());
        Assertions.assertThat(foundBook).isNotNull();

        bookRepository.delete(foundBook);

        Book deletedBook = entityManager.find(Book.class, book.getId());
        Assertions.assertThat(deletedBook).isNull();
    }
}

package com.cursojavatdd.libraryapi.api.resource;


import com.cursojavatdd.libraryapi.api.dto.LoanDTO;
import com.cursojavatdd.libraryapi.api.dto.LoanFilterDTO;
import com.cursojavatdd.libraryapi.api.dto.ReturnedLoanDTO;
import com.cursojavatdd.libraryapi.entity.Loan;
import com.cursojavatdd.libraryapi.service.LoanService;
import com.cursojavatdd.libraryapi.entity.Book;
import com.cursojavatdd.libraryapi.exception.BusinessException;
import com.cursojavatdd.libraryapi.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)
@AutoConfigureMockMvc
public class LoanControllerTest {

    static final String LOAN_API = "/api/loans";
    
    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookService bookService;

    @MockBean
    LoanService loanService;

    private LoanDTO createLoanDTO() {
        LoanDTO loanDTO = new LoanDTO();
        loanDTO.setIsbn("001");
        loanDTO.setCustomer("User");
        loanDTO.setEmail("user@mail.com");

        return loanDTO;
    }

    private Book createSavedBook() {
        return new Book(101L, "Arthur", "As aventuras do Rei", "001", null);
    }

    private Loan createSavedLoan() {
        return new Loan(1L, "User", "user_email@mail.com", LocalDate.now(), null, this.createSavedBook());

    }

    @Test
    @DisplayName("Deve realizar um empréstimo")
    public void createLoanTest() throws Exception {
        LoanDTO dto = this.createLoanDTO();
        String json = new ObjectMapper().writeValueAsString(dto);

        Book book = this.createSavedBook();
        BDDMockito.given(bookService.getByIsbn("001")).willReturn(Optional.of(book));

        Loan loan = this.createSavedLoan();
        BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("1"));
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar realizar empréstimo de um livro inexistente")
    public void invalidIsbnCreateLoanTest() throws Exception {
        LoanDTO dto = this.createLoanDTO();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(bookService.getByIsbn("001")).willReturn(Optional.empty());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]")
                        .value("Livro não encontrado para o isbn informado"));
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar realizar empréstimo de um livro emprestado")
    public void loanedBookErrorOnCreateLoanTest() throws Exception {
        LoanDTO dto = this.createLoanDTO();
        String json = new ObjectMapper().writeValueAsString(dto);

        Book book = this.createSavedBook();
        BDDMockito.given(bookService.getByIsbn("001")).willReturn(Optional.of(book));

        BDDMockito.given(loanService.save(Mockito.any(Loan.class)))
                .willThrow(new BusinessException("Livro já emprestado"));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]")
                        .value("Livro já emprestado"));
    }

    @Test
    @DisplayName("Deve devolver um livro")
    public void returnBookTest() throws Exception {
        Loan loan = createSavedLoan();
        ReturnedLoanDTO returnedLoanDTO = new ReturnedLoanDTO(true);

        BDDMockito.when(loanService.getById(Mockito.anyLong())).thenReturn(Optional.of(loan));

        String json = new ObjectMapper().writeValueAsString(returnedLoanDTO);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .patch(LOAN_API.concat("/1"))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(MockMvcResultMatchers.status().isNoContent());
        Mockito.verify(loanService, Mockito.times(1)).update(loan);
    }

    @Test
    @DisplayName("Deve retornar 404 quando tentar devolver um livro inexistente")
    public void returnInexistentBookTest() throws Exception {
        ReturnedLoanDTO returnedLoanDTO = new ReturnedLoanDTO(true);

        BDDMockito.when(loanService.getById(Mockito.anyLong())).thenReturn(Optional.empty());

        String json = new ObjectMapper().writeValueAsString(returnedLoanDTO);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .patch(LOAN_API.concat("/1"))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("Deve filtrar empréstimos")
    public void findLoansTest() throws Exception{
        Long id = 101L;
        Book book = this.createSavedBook();
        Loan loan = this.createSavedLoan();
        loan.setId(id);

        BDDMockito.given(loanService.find(Mockito.any(LoanFilterDTO.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Loan>(Arrays.asList(loan), PageRequest.of(0, 100), 1));

        String queryString = String.format("?isbn=%s&customer=%s&page=0&size=100", loan.getBook().getIsbn(), loan.getCustomer());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(LOAN_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("content", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("totalElements").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageSize").value(100))
                .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageNumber").value(0));
    }
}

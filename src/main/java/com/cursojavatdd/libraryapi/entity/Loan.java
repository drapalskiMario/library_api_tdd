package com.cursojavatdd.libraryapi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String customer;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column
    private LocalDate loanDate;

    @Column
    private Boolean returned;

    @JoinColumn(name = "id_book")
    @ManyToOne
    private Book book;
}

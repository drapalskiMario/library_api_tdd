package com.cursojavatdd.libraryapi.api.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnedLoanDTO {

    private boolean returned;

    public Boolean getReturned() {
        return this.returned;
    }
}

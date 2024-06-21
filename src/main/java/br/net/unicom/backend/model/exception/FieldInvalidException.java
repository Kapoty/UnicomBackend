package br.net.unicom.backend.model.exception;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FieldInvalidException extends Exception {

    private String field;
    private String error;

    public FieldInvalidException(String field, String error) {
        super(field + ": " + error);
        this.field = field;
        this.error = error;
    }

}

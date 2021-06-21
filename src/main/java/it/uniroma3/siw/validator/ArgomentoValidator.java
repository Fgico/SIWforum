package it.uniroma3.siw.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Argomento;

@Component
public class ArgomentoValidator implements Validator {

    final Integer MAX_NICKNAME_LENGTH = 50;
    final Integer MIN_NICKNAME_LENGTH = 4;

    @Override
    public void validate(Object o, Errors errors) {
        Argomento u = (Argomento) o;
        String nome = u.getNome().trim();

        if (nome.isEmpty())
            errors.rejectValue("nome", "required");
        else if (nome.length() < MIN_NICKNAME_LENGTH || nome.length() > MAX_NICKNAME_LENGTH)
            errors.rejectValue("nome", "size");
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Argomento.class.equals(clazz);
    }
}
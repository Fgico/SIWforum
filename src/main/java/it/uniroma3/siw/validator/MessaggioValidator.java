package it.uniroma3.siw.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Messaggio;

@Component
public class MessaggioValidator implements Validator {
	
    final Integer MAX_LENGTH = 251;
    final Integer MIN_LENGTH = 2;

    @Override
    public void validate(Object o, Errors errors) {
        Messaggio u = (Messaggio) o;
        String testo = u.getTesto().trim();

        if (testo.isEmpty())
            errors.rejectValue("testo", "required");
        else if (testo.length() < MIN_LENGTH || testo.length() > MAX_LENGTH)
            errors.rejectValue("testo", "size");
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Messaggio.class.equals(clazz);
    }
}
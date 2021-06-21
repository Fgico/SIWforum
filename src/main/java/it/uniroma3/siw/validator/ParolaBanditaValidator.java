package it.uniroma3.siw.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.ParolaBandita;

@Component
public class ParolaBanditaValidator implements Validator {

    final Integer MAX_LENGTH = 20;
    final Integer MIN_LENGTH = 3;

    @Override
    public void validate(Object o, Errors errors) {
    	ParolaBandita u = (ParolaBandita) o;
        String parola = u.getParola().trim();

        if (parola.isEmpty())
            errors.rejectValue("parola", "required");
        else if (parola.length() < MIN_LENGTH || parola.length() > MAX_LENGTH)
            errors.rejectValue("parola", "size");
        else if (!parola.matches("[a-zA-Z]+"))
        		errors.rejectValue("parola", "alphanumeric");

    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ParolaBandita.class.equals(clazz);
    }
}
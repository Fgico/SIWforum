package it.uniroma3.siw.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.TextCleaner;
import it.uniroma3.siw.model.Discussione;

@Component
public class DiscussioneValidator implements Validator {

	@Autowired
	private TextCleaner parolaBan;

    final Integer MAX_LENGTH = 50;
    final Integer MIN_LENGTH = 4;

    @Override
    public void validate(Object o, Errors errors) {
        Discussione u = (Discussione) o;
        String nome = u.getNome().trim();

        if (nome.isEmpty())
            errors.rejectValue("nome", "required");
        else if (nome.length() < MIN_LENGTH || nome.length() > MAX_LENGTH)
            errors.rejectValue("nome", "size");
        else if ( parolaBan.contieneParolacce(nome))
        	errors.rejectValue("nome", "parolaccia");
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Discussione.class.equals(clazz);
    }
}
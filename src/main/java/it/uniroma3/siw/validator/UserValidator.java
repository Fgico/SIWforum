package it.uniroma3.siw.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.TextCleaner;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;

@Component
public class UserValidator implements Validator {

    @Autowired
    private UserService userv;
    
    @Autowired
    private TextCleaner parolaBan;

    final Integer MAX_NICKNAME_LENGTH = 20;
    final Integer MIN_NICKNAME_LENGTH = 4;

    @Override
    public void validate(Object o, Errors errors) {
        User u = (User) o;
        String nickname = u.getNickName().trim();

        if (nickname.isEmpty())
            errors.rejectValue("nickName", "required");
        else if (nickname.length() < MIN_NICKNAME_LENGTH || nickname.length() > MAX_NICKNAME_LENGTH)
            errors.rejectValue("nickName", "size");
        else if (!nickname.matches("[a-zA-Z0-9]+"))
        		errors.rejectValue("nickName", "alphanumeric");
        else if (this.userv.getUserByNick(nickname) != null)
            errors.rejectValue("nickName", "duplicate");
        else if(parolaBan.contieneParolacce(nickname))
        	errors.rejectValue("nickName", "parolaccia");
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }
}
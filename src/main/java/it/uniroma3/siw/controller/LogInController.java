package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.ModelAndView;

import it.uniroma3.siw.auth.UserHelper;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.MyOauthUser;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.MyOauthUserService;
import it.uniroma3.siw.validator.CredentialsValidator;
import it.uniroma3.siw.validator.*;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;

@Controller
public class LogInController {

	@Autowired
	private UserHelper userInfo;
	
	@Autowired
	private CredentialsService credentialsService;
	@Autowired
	private MyOauthUserService oserv;
	@Autowired
	private UserValidator userValidator;
	@Autowired
	private CredentialsValidator credentialsValidator;
	
	
	@RequestMapping(value = "/login", method = RequestMethod.GET) 
	public String login (Model model) {
		model.addAttribute("utenteCorrente", userInfo.getCurrentUser());
		return "loginForm";
	}
	
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET) 
	public String logout(Model model) {
		return "home";
	}
	
	@RequestMapping(value = "/register", method = RequestMethod.GET) 
	public String showRegisterForm (Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("credentials", new Credentials());
		return "registra";
	}
	
	@RequestMapping(value = { "/register" }, method = RequestMethod.POST)
    public String registerUser(@ModelAttribute("user") User user,
                 BindingResult userBindingResult,
                 @ModelAttribute("credentials") Credentials credentials,
                 BindingResult credentialsBindingResult,
                 Model model) {

        // validate user and credentials fields
        this.userValidator.validate(user, userBindingResult);
        this.credentialsValidator.validate(credentials, credentialsBindingResult);

        // if neither of them had invalid contents, store the User and the Credentials into the DB
        if(!userBindingResult.hasErrors() && ! credentialsBindingResult.hasErrors()) {
            // set the user and store the credentials;
            // this also stores the User, thanks to Cascade.ALL policy
            credentials.setUser(user);
            credentials.setRole(credentials.DEFAULT_ROLE);
            credentialsService.saveCredentials(credentials);
            model.addAttribute("user",user);
            model.addAttribute("utenteCorrente", userInfo.getCurrentUser());
            return "user";
        }
        return "registra";
    }
	
    
    //Managing Oauth
    
    //punto reindirizzamento dopo login
    @RequestMapping(value ="/registraOauth", method = RequestMethod.GET)
    public ModelAndView benvenutoOauth(Model model) {
    	User currentUser = userInfo.getCurrentUser();
    	if(currentUser == null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
    		return new ModelAndView("redirect:/finalizzaRegistrazione");
    	}
    	return new ModelAndView("redirect:/"); 
    }
    
    @RequestMapping(value ="/finalizzaRegistrazione", method= RequestMethod.GET)
    public String chiediNickNameOauth (Model model) {
    	model.addAttribute("user", new User());
    	return "chiediNick";
    }
    
    @RequestMapping(value = { "/finalizzaRegistrazione" }, method = RequestMethod.POST)
    public String creaUtenteOauth(@ModelAttribute("user") User user,
                 BindingResult userBindingResult,
                 Model model) {
        this.userValidator.validate(user, userBindingResult);
        
        Object auth = SecurityContextHolder.getContext().getAuthentication();

        if(!userBindingResult.hasErrors() && userInfo.getCurrentUser() == null && (auth instanceof OAuth2AuthenticationToken)) {
            MyOauthUser oau = new MyOauthUser();
            OAuth2AuthenticationToken ouser= (OAuth2AuthenticationToken)auth;
            Integer oidint = ouser.getPrincipal().getAttribute("id");
            Long oid = oidint.longValue();
            oau.setId(oid);
        	oau.setUser(user);
        	oserv.salva(oau);
        	model.addAttribute("user",user);
        	model.addAttribute("utenteCorrente", userInfo.getCurrentUser());
            return "user";
        }
        return "chiediNick";
    }
}

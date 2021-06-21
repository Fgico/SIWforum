package it.uniroma3.siw.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

import it.uniroma3.siw.model.MyOauthUser;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.MyOauthUserService;
import it.uniroma3.siw.service.UserService;

@Component("userInfo")
public class UserHelper {
	
	@Autowired
	CredentialsService cserv;
	
	@Autowired
	MyOauthUserService oserv;
	
	@Autowired
	UserService userv;
	
	User DeletedUser = null;

	public User getCurrentUser() {
		SecurityContext context = SecurityContextHolder.getContext();
		if (context.getAuthentication().isAuthenticated()) {
			Object auth = context.getAuthentication();
			
			if (auth instanceof AnonymousAuthenticationToken)
				return null;
			if (auth instanceof OAuth2AuthenticationToken) {
				OAuth2AuthenticationToken oUser = (OAuth2AuthenticationToken) auth;
				Integer idint = oUser.getPrincipal().getAttribute("id");
				Long id = idint.longValue();
				MyOauthUser u = oserv.prendi(id);
				if(u != null)
					return u.getUser();
				else
					return null;
			}
			UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    	return cserv.getCredentials(userDetails.getUsername()).getUser();
		}
		return null;	
	}
	
	public User DeletedUser() {
		User res = userv.getUserByNick("deletedUser");
		if(res == null) {
			res= new User();
			res.setNickName("DeletedUser");
			res.setBanned(true);
			userv.saveUser(res);
			}
		return res;
	}
}

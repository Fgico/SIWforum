package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


import it.uniroma3.siw.auth.UserHelper;
import it.uniroma3.siw.model.Messaggio;
import it.uniroma3.siw.model.ParolaBandita;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.MessaggioService;
import it.uniroma3.siw.service.ParolaBanditaService;
import it.uniroma3.siw.service.UserService;
import it.uniroma3.siw.uploadHandle.FileUploadUtil;

@Controller
public class UserController {

	@Autowired
	private UserService userv;
	
	@Autowired
	private MessaggioService mserv;
	
	@Autowired
	private ParolaBanditaService pserv;
	
	@Autowired
	private UserHelper userInfo;
	
	@RequestMapping(value = "/myUser", method = RequestMethod.GET)
	public String mioProfilo(Model model) {
		User u = userInfo.getCurrentUser();
		model.addAttribute("utenteCorrente", u);
		model.addAttribute("user", u);
		model.addAttribute("paroleBandite",pserv.tutti());
		return "user";
	}
	
	@RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
	public String vediProfilo(@PathVariable("id") Long id, Model model) {
		User u = userInfo.getCurrentUser();
		model.addAttribute("utenteCorrente", u);
		model.addAttribute("user", userv.getUser(id));
		model.addAttribute("paroleBandite",pserv.tutti());
		return "user";
	}
	
	
	@RequestMapping(value="/user/addimage/{id}",method= RequestMethod.GET)
	public String preparaFormImmagine(@PathVariable("id") Long id, Model model) {
		model.addAttribute("id",id);
		model.addAttribute("utenteCorrente", userInfo.getCurrentUser());
	return "imageForm";
	}
	
	@RequestMapping(value="/user/addimage/{id}",method= RequestMethod.POST)
	public String caricaImmagine(@PathVariable("id") Long id, Model model,@RequestParam("image") MultipartFile multipartFile){
		User curruser= userInfo.getCurrentUser();
		User u = userv.getUser(id);
		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        String uploadDir = "userImages/" + id;
        String res = "imageForm";
        if( curruser!= null && !curruser.isBanned() && (curruser.getId().equals(id)) || (curruser.isAdmin()))
	        try {
				FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
				res="user";
				String finalPath = "/userImages/" + id +'/' +fileName;
				u.setImageUrl(finalPath);
				userv.saveUser(u);
				model.addAttribute("user", u);
				model.addAttribute("paroleBandite",pserv.tutti());
				model.addAttribute("utenteCorrente", curruser);
			} catch (IOException e) {
			
			}
         
        return res;
	}
	
	@RequestMapping(value="/admin/resetimage/{id}",method= RequestMethod.GET)
	public String resettaImmagine(@PathVariable("id") Long id, Model model){
			User u = userv.getUser(id);
			u.setImageUrl("/images/defaultPic.png");
			userv.saveUser(u);
			model.addAttribute("user", u);
			model.addAttribute("paroleBandite",pserv.tutti());
			model.addAttribute("utenteCorrente", userInfo.getCurrentUser());
			return "user";
	}
	
	
	@RequestMapping(value="/user/ban/{id}",method= RequestMethod.GET)
	public String bandisciUtente(@PathVariable("id") Long id, Model model){
			
			User curruser= userInfo.getCurrentUser();
			User u = userv.getUser(id);
			if( curruser != null &&  !curruser.isBanned() && (curruser.getId().equals(id) || curruser.isAdmin())) {
				u.setImageUrl("/images/defaultPic.png");
				u.setAdmin(false);
				u.setBanned(true);
				userv.saveUser(u);
				User deluser = userInfo.DeletedUser();
				List<Messaggio> oldmsg = mserv.creatiDa(u);
				for ( Messaggio msg : oldmsg) {
					msg.setCreatore(deluser);
					msg.setTesto("---L'utente che ha scritto questo messaggio si e' cancellato o e' stato bandito---");
					mserv.metti(msg);
				}
			}
			model.addAttribute("user", u);
			model.addAttribute("paroleBandite",pserv.tutti());
			model.addAttribute("utenteCorrente", curruser);
			return "user";
	}
	
	@RequestMapping(value="/admin/nuovaParolaBandita",method= RequestMethod.GET)
	public String aggiungiParolaBandit(Model model){
			
			User curruser= userInfo.getCurrentUser();
			model.addAttribute("parolaBandita", new ParolaBandita());
			model.addAttribute("paroleBandite",pserv.tutti());
			model.addAttribute("utenteCorrente", curruser);
			return "parolaBanditaForm";
	}
	
	@RequestMapping(value="/admin/nuovaParolaBandita",method= RequestMethod.POST)
	public String aggiungiParolaBanditaFinalizza( @ModelAttribute("parolaBandita") ParolaBandita parola,
								Model model
								){
			
			User curruser= userInfo.getCurrentUser();
			if(curruser!=null && curruser.isAdmin()) {
				pserv.metti(parola);
			}
			
			model.addAttribute("paroleBandite",pserv.tutti());
			model.addAttribute("utenteCorrente", curruser);
			model.addAttribute("user", curruser);
			return "user";
	}
	
	@RequestMapping(value="/admin/eliminaParolaBandita/{id}", method= RequestMethod.GET)
	public String rimuoviParolaBanditaFinalizza(Model model,
								@PathVariable("id") Long id ){
			
			User curruser= userInfo.getCurrentUser();
			if(curruser!=null && curruser.isAdmin()) {
				pserv.eliminaById(id);
			}
			model.addAttribute("parola", new ParolaBandita());
			model.addAttribute("paroleBandite",pserv.tutti());
			model.addAttribute("utenteCorrente", curruser);
			model.addAttribute("user", curruser);
			return "user";
	}
	
}

package it.uniroma3.siw.controller;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.Page;
import it.uniroma3.siw.TextCleaner;
import it.uniroma3.siw.auth.UserHelper;
import it.uniroma3.siw.model.Argomento;
import it.uniroma3.siw.model.Discussione;
import it.uniroma3.siw.model.Messaggio;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.ArgomentoService;
import it.uniroma3.siw.service.DiscussioneService;
import it.uniroma3.siw.service.UserService;
import it.uniroma3.siw.validator.ArgomentoValidator;
import it.uniroma3.siw.validator.DiscussioneValidator;
import it.uniroma3.siw.validator.MessaggioValidator;

@Controller
public class ArgomentoController {
	
	@Autowired
	private ArgomentoService aserv;
	@Autowired 
	private DiscussioneService dserv;
	
	@Autowired
	private ArgomentoValidator argvalid;
	@Autowired
	private DiscussioneValidator dscvalid;
	@Autowired
	private MessaggioValidator msgvalid;
	
	@Autowired 
	private TextCleaner txtclean;
	
	@Autowired
	private UserHelper userInfo;
	
	@Autowired 
	private UserService userv;
	
	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String goHome(Model model) {
		model.addAttribute("utenteCorrente", userInfo.getCurrentUser());
		model.addAttribute("argomenti", aserv.tutti());
		return "argomenti";
	}
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String goHomeNoLabel(Model model) {
		model.addAttribute("utenteCorrente", userInfo.getCurrentUser());
		model.addAttribute("argomenti", aserv.tutti());
		return "argomenti";
	}
	
	@RequestMapping(value = "/argomento/{id}/{page}" , method = RequestMethod.GET)
	public String mostraArgomento(@PathVariable("id") Long id,
									@PathVariable("page") int page,
									Model model) {
		
		model.addAttribute("utenteCorrente",userInfo.getCurrentUser());
		Argomento arg = aserv.prendi(id);
		List<Discussione> threads = arg.getThreads();
		Collections.sort(threads, new Comparator<Discussione>() {
			@Override
			public int compare (Discussione a, Discussione b) {
				return b.getLastReplyTime().compareTo(a.getLastReplyTime());
			}
		});
		model.addAttribute("pagina", new Page<Discussione>(threads, page));
		model.addAttribute("argomento", arg);
		return "argomento";
	}
	
	
	@RequestMapping(value = "/admin/addArgomento", method = RequestMethod.GET)
	public String creaNuovoArgomento(Model model) {
		model.addAttribute("utenteCorrente", userInfo.getCurrentUser());
		model.addAttribute("argomento", new Argomento());
		return "argomentoForm";
	}
	
	@RequestMapping(value = "/admin/addArgomento", method = RequestMethod.POST)
	public String creaNuovoArgomentoFinalizza(@ModelAttribute("argomento") Argomento arg,
            BindingResult argomentoBindingResult,Model model) {
		model.addAttribute("utenteCorrente", userInfo.getCurrentUser());
		
		arg.setNome(arg.getNome().replace('<', ' '));
		arg.setNome(arg.getNome().replace('>', ' '));
		this.argvalid.validate(arg, argomentoBindingResult);
		
		if(!argomentoBindingResult.hasErrors()) {
			aserv.metti(arg);
			model.addAttribute("argomenti", aserv.tutti());
			return "argomenti";
		}
		return "argomentoForm";
	}
	
	@RequestMapping(value = "/admin/eliminaArgomento/{id}", method = RequestMethod.GET)
	public String eliminaArgomento(@PathVariable("id") Long id, Model model) {
		aserv.elimina(id);
		model.addAttribute("utenteCorrente", userInfo.getCurrentUser());
		model.addAttribute("argomenti", aserv.tutti());
		return "argomenti";
	}
	
	//creazione nuove discussioni, utente qualsiasi
	@RequestMapping(value = "/argomento/{id}/nuovaDiscussione" , method = RequestMethod.GET)
	public String nuovaDiscussione(@PathVariable("id") Long id,
									Model model) {
		
		model.addAttribute("utenteCorrente",userInfo.getCurrentUser());
		if(userInfo.getCurrentUser()!= null) {
			Argomento arg = aserv.prendi(id);
			model.addAttribute("argomento", arg);
			model.addAttribute("discussione", new Discussione());
			model.addAttribute("messaggio", new Messaggio());
			return "discussioneForm";
		}
		model.addAttribute("argomenti",aserv.tutti());
		return "argomenti";
	}
	
	@RequestMapping(value = "/argomento/{id}/nuovaDiscussione" , method = RequestMethod.POST)
	public String nuovaDiscussioneFinalizza(@PathVariable("id") Long id,
											@ModelAttribute ("discussione") Discussione dsc,
											BindingResult dscBindingResult,
											@ModelAttribute ("messaggio") Messaggio msg,
											BindingResult msgBindingResult,
											Model model) {
		
		User curruser = userInfo.getCurrentUser();
		model.addAttribute("utenteCorrente",curruser);
		Argomento arg = aserv.prendi(id);
		model.addAttribute("argomento",arg);
		
		msg.setTesto(txtclean.pulisciSegniHtml(msg.getTesto()));
		msg.setTesto(txtclean.sostituisciParolacce(msg.getTesto()));
		
		dsc.setNome(txtclean.pulisciSegniHtml(dsc.getNome()));
		
		msgvalid.validate(msg, msgBindingResult);
		dscvalid.validate(dsc, dscBindingResult);
		
		if(curruser != null && !dscBindingResult.hasErrors() && !msgBindingResult.hasErrors() && !curruser.isBanned()) {
			msg.setCreatore(curruser);
			dsc.rispondi(msg);
			arg.creaThread(dsc);
			aserv.metti(arg);
			model.addAttribute("pagina",new Page<>(arg.getThreads(), 1));
			return "argomento";
		}
		return "discussioneForm";
	}
	
	@RequestMapping(value = "/admin/argomento/{id}/eliminaDiscussione/{did}", method = RequestMethod.GET)
	public String eliminaDiscussione(@PathVariable("id") Long id,
									@PathVariable("did") Long did,
									Model model) {
		Argomento arg = aserv.prendi(id);
		if(userInfo.getCurrentUser().isAdmin()) {
			Discussione dsc =dserv.prendi(did);
			arg.getThreads().remove(dsc);
			dserv.elimina(did);
			aserv.metti(arg);
		}
		List<Discussione> threads = arg.getThreads();
		Collections.sort(threads, new Comparator<Discussione>() {
			@Override
			public int compare (Discussione a, Discussione b) {
				return b.getLastReplyTime().compareTo(a.getLastReplyTime());
			}
		});
		model.addAttribute("pagina", new Page<Discussione>(threads, 1));
		model.addAttribute("utenteCorrente", userInfo.getCurrentUser());
		model.addAttribute("argomento", arg);
		return "argomento";
	}
	
	
	@RequestMapping(value="/argomento/cerca", method = RequestMethod.POST)
		public String cercaDiscussione(Model model, @RequestParam("parolaCercata") String parola) {
			model.addAttribute("utenteCorrente",userInfo.getCurrentUser());
			List<Discussione> res = dserv.cercaInNome(parola);
			if(res.size()>20)
				res =res.subList(0, 20);
			model.addAttribute("risultati",res);
			return"risultatiRicerca";
	}
	
}

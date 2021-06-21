package it.uniroma3.siw.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import it.uniroma3.siw.Page;
import it.uniroma3.siw.TextCleaner;
import it.uniroma3.siw.auth.UserHelper;
import it.uniroma3.siw.model.Argomento;
import it.uniroma3.siw.model.Discussione;
import it.uniroma3.siw.model.Messaggio;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.ArgomentoService;
import it.uniroma3.siw.service.DiscussioneService;
import it.uniroma3.siw.service.MessaggioService;
import it.uniroma3.siw.validator.MessaggioValidator;
import it.uniroma3.siw.model.Discussione;

@Controller
public class DiscussioneController {

	@Autowired
	private DiscussioneService dserv;
	@Autowired
	private ArgomentoService aserv;
	@Autowired
	private MessaggioService mserv;
	
	@Autowired
	private MessaggioValidator msgvalid; 
	
	@Autowired
	private TextCleaner txtclean;
	
	@Autowired
	private UserHelper userInfo;
	
	@RequestMapping(value = "/discussione/{id}/{page}" , method = RequestMethod.GET)
	public String mostraDiscussione(@PathVariable("id") Long id,
									@PathVariable("page") int page,
									Model model) {
		
		model.addAttribute("utenteCorrente",userInfo.getCurrentUser());
		Discussione dsc = dserv.prendi(id);
		List<Messaggio> replys = dsc.getRisposte();
		Collections.sort(replys, new Comparator<Messaggio>() {
			@Override
			public int compare (Messaggio a, Messaggio b) {
				return a.getTimePost().compareTo(b.getTimePost());
			}
		});
		model.addAttribute("pagina", new Page<Messaggio>(replys, page));
		model.addAttribute("discussione", dsc);
		return "discussione";
	}
	
	@RequestMapping(value = "/discussione/{id}/rispondiCitando/{qid}" , method = RequestMethod.GET)
	public String nuovaRispostaCitando(@PathVariable("id") Long id,
										@PathVariable("qid") Long qid,
									Model model) {
		
		model.addAttribute("utenteCorrente",userInfo.getCurrentUser());
		if(userInfo.getCurrentUser()!= null) {
			Discussione dsc = dserv.prendi(id);
			Messaggio msg = new Messaggio();
			msg.setQuotedMessage(mserv.prendi(qid));
			model.addAttribute("discussione", dsc);
			model.addAttribute("msg", msg);
			model.addAttribute("qid", qid);
			return "rispostaForm";
		}
		return "argomenti";
	}
	
	@RequestMapping(value = "/discussione/{id}/nuovaRisposta" , method = RequestMethod.GET)
	public String nuovaRisposta(@PathVariable("id") Long id,
									Model model) {
		
		model.addAttribute("utenteCorrente",userInfo.getCurrentUser());
		if(userInfo.getCurrentUser()!= null) {
			Discussione dsc = dserv.prendi(id);
			model.addAttribute("discussione", dsc);
			model.addAttribute("msg", new Messaggio());
			return "rispostaForm";
		}
		return "argomenti";
	}
	
	@RequestMapping(value = "/discussione/{id}/nuovaRisposta" , method = RequestMethod.POST)
	public String nuovaDiscussioneFinalizza(@PathVariable("id") Long id,
											@ModelAttribute ("msg") Messaggio msg,
											BindingResult msgBindingResult,
											Model model) {
		User curruser = userInfo.getCurrentUser();
		model.addAttribute("utenteCorrente",curruser);
		Discussione dsc = dserv.prendi(id);
		
		msg.setTesto(txtclean.pulisciSegniHtml(msg.getTesto()));
		msg.setTesto(txtclean.sostituisciParolacce(msg.getTesto()));
		
		msgvalid.validate(msg, msgBindingResult);
		
		if(curruser!= null && !msgBindingResult.hasErrors() && !curruser.isBanned() ) {
			msg.setCreatore(curruser);
			dsc.rispondi(msg);
			dserv.metti(dsc);
			model.addAttribute(dsc);
			model.addAttribute("pagina",new Page<>(dsc.getRisposte(), 99999));
			return "discussione";
		}
		return "rispostaForm";
	}
	
	@RequestMapping(value = "/discussione/{id}/nuovaRisposta/{qid}" , method = RequestMethod.POST)
	public String nuovaDiscussioneFinalizzaConCitazione(@PathVariable("id") Long id,
											@PathVariable("qid") Long qid,
											@ModelAttribute ("msg") Messaggio msg,
											BindingResult msgBindingResult,
											Model model) {
		
		User curruser = userInfo.getCurrentUser();
		model.addAttribute("utenteCorrente",curruser);
		Discussione dsc = dserv.prendi(id);
		
		msg.setTesto(txtclean.pulisciSegniHtml(msg.getTesto()));
		msg.setTesto(txtclean.sostituisciParolacce(msg.getTesto()));
		
		msgvalid.validate(msg, msgBindingResult);
		
		if(curruser!= null && !msgBindingResult.hasErrors() && !curruser.isBanned()) {
			msg.setCreatore(userInfo.getCurrentUser());
			msg.setQuotedMessage(mserv.prendi(qid));
			dsc.rispondi(msg);
			dserv.metti(dsc);
			model.addAttribute("discussione",dsc);
			List<Messaggio> replys = dsc.getRisposte();
			Collections.sort(replys, new Comparator<Messaggio>() {
				@Override
				public int compare (Messaggio a, Messaggio b) {
					return a.getTimePost().compareTo(b.getTimePost());
				}
			});
			model.addAttribute("pagina",new Page<>(replys, 99999));
			return "discussione";
		}
		return "rispostaForm";
	}
	
	@RequestMapping(value = "/discussione/{id}/eliminaMessaggio/{mid}" , method = RequestMethod.GET)
	public String svuotaMessaggio(@PathVariable("id") Long id,
									@PathVariable("mid") Long mid,
									Model model) {
		
		model.addAttribute("utenteCorrente",userInfo.getCurrentUser());
		Messaggio daSvuotare = mserv.prendi(mid);
		User currUser= userInfo.getCurrentUser();
		if( currUser!= null && (currUser.getId() == daSvuotare.getCreatore().getId() || currUser.isAdmin()) && !currUser.isBanned()) {
			daSvuotare.setTesto("--Questo messaggio e' stato eliminato dall'utente o da un moderatore--");
			mserv.metti(daSvuotare);
		}
		Discussione dsc = dserv.prendi(id);
		List<Messaggio> replys = dsc.getRisposte();
		Collections.sort(replys, new Comparator<Messaggio>() {
			@Override
			public int compare (Messaggio a, Messaggio b) {
				return a.getTimePost().compareTo(b.getTimePost());
			}
		});
		model.addAttribute("pagina", new Page<Messaggio>(replys, 1));
		model.addAttribute("discussione", dsc);
		return "discussione";
	}
	
}

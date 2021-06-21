package it.uniroma3.siw;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.uniroma3.siw.model.ParolaBandita;
import it.uniroma3.siw.service.ParolaBanditaService;

@Component
public class TextCleaner {
	
	@Autowired
	private ParolaBanditaService pserv;
	
	public boolean contieneParolacce(String parola) {
		List<ParolaBandita> parolacce = pserv.tutti();
		boolean trovato = false;
		for(int i = 0; i< parolacce.size() && !trovato; i++) {
			trovato = StringUtils.containsIgnoreCase(parola, parolacce.get(i).getParola());
		}
		return trovato;
	}
	
	public String sostituisciParolacce(String parola) {
		List<ParolaBandita> parolacce = pserv.tutti();
		String res = parola;
		for(int i = 0; i< parolacce.size(); i++) {
			String parolaccia = parolacce.get(i).getParola();
			String substitute = parolaccia.charAt(0) + ("*".repeat(parolaccia.length()-2)) +parolaccia.charAt(parolaccia.length()-1);
			res = StringUtils.replaceIgnoreCase(res , parolaccia, substitute);
		}
		return res;
	}
	
	public String pulisciSegniHtml(String s) {
		s.replace('<', ' ');
		s.replace('>', ' ');
		return s;
	}
	
}

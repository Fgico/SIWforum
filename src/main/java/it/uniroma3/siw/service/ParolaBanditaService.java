package it.uniroma3.siw.service;

import java.util.LinkedList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.ParolaBandita;
import it.uniroma3.siw.repos.ParolaBanditaRepo;

@Service
public class ParolaBanditaService {
	
	@Autowired
	private ParolaBanditaRepo prepo;
	
	@Transactional
	public ParolaBandita prendi(Long id) {
		return prepo.findById(id).orElse(null);
	}
	
	@Transactional
	public ParolaBandita metti(ParolaBandita parola) {
		return prepo.save(parola);
	}
	
	@Transactional
	public void elimina(ParolaBandita parola) {
		prepo.delete(parola);
	}
	
	@Transactional
	public List<ParolaBandita> tutti(){
		Iterable<ParolaBandita> parole = prepo.findAll();
		LinkedList<ParolaBandita> lista = new LinkedList<>();
		for( ParolaBandita p : parole) {
			lista.add(p);
		}
		return lista;
	}

	public void eliminaById(Long id) {
		prepo.deleteById(id);
		
	}
}

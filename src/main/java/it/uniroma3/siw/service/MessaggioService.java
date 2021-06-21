package it.uniroma3.siw.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Messaggio;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repos.MessaggioRepo;

@Service
public class MessaggioService {
	
	@Autowired
	protected MessaggioRepo mRepo;
	
	@Transactional
	public Messaggio prendi(Long id) {
		return mRepo.findById(id).orElse(null);
	}
	
	@Transactional
	public Messaggio metti(Messaggio msg) {
		return mRepo.save(msg);
	}
	
	@Transactional
    public List<Messaggio> tutti() {
        List<Messaggio> result = new ArrayList<>();
        Iterable<Messaggio> iterable = this.mRepo.findAll();
        for(Messaggio msg : iterable)
            result.add(msg);
        return result;
    }
	
	@Transactional
	public List<Messaggio> creatiDa(User u){
		return mRepo.findByCreatore(u);
	}

}

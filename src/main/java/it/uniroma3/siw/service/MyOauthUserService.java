package it.uniroma3.siw.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.MyOauthUser;
import it.uniroma3.siw.repos.MyOauthUserRepo;

@Service
public class MyOauthUserService {
	
	@Autowired
	protected MyOauthUserRepo mrepo;
	
	@Transactional
	public MyOauthUser prendi(Long id) {
		return mrepo.findById(id).orElse(null);
	}
	
	@Transactional
	public MyOauthUser salva(MyOauthUser mou) {
		return mrepo.save(mou);
	}
}

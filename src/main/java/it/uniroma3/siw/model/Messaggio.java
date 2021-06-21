package it.uniroma3.siw.model;

import java.time.LocalDateTime;

import javax.persistence.*;

@Entity
public class Messaggio {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToOne
	private User creatore;
	
	@Column
	private String testo;
	
	@ManyToOne
	private Messaggio quotedMessage;
	
	@Column
	private LocalDateTime timePost;
	
	@ManyToOne
	private Discussione discussione;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getCreatore() {
		return creatore;
	}

	public void setCreatore(User creatore) {
		this.creatore = creatore;
	}

	public String getTesto() {
		return testo;
	}

	public void setTesto(String testo) {
		this.testo = testo;
	}

	public Messaggio getQuotedMessage() {
		return quotedMessage;
	}

	public void setQuotedMessage(Messaggio quotedMessage) {
		this.quotedMessage = quotedMessage;
	}

	public LocalDateTime getTimePost() {
		return timePost;
	}

	public void setTimePost(LocalDateTime timePost) {
		this.timePost = timePost;
	}

	public Discussione getDiscussione() {
		return discussione;
	}

	public void setDiscussione(Discussione discussione) {
		this.discussione = discussione;
	}
	
	
	
	
}

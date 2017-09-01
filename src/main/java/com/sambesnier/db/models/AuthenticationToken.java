package com.sambesnier.db.models;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table (name="authenticationTokens")
@NamedQueries({ 
    @NamedQuery(name = "AuthenticationToken.findByUserName", query = "SELECT u FROM AuthenticationToken u WHERE u.username = :login"),
    @NamedQuery(name = "AuthenticationToken.findByUserNameAndToken", query = "SELECT u FROM AuthenticationToken u WHERE u.username = :login and u.token = :token")})
@SuppressWarnings("unused")
public class AuthenticationToken implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;

	@Column (unique=true)
	String username;
	
	@Column
	String token;
	
	@Column
	Date created_at;

	@PrePersist
	void createdAt() {
		this.created_at = new Date();
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @return the created_at
	 */
	public Date getCreated_at() {
		return created_at;
	}

	/**
	 * @param created_at the created_at to set
	 */
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

		
}

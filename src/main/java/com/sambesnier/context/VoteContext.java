package com.sambesnier.context;

import java.util.ArrayList;
import java.util.List;

import com.sambesnier.models.Vote;


public class VoteContext {
	
	List<Vote> votes;
	
	private static VoteContext INSTANCE = null;
	
	private VoteContext() {
		votes = new ArrayList<Vote>();
	}
	
	public static VoteContext getContext() {
		if (INSTANCE == null) {
			INSTANCE = new VoteContext();
		}
		return INSTANCE;
	}

	/**
	 * @return the votes
	 */
	public List<Vote> getVotes() {
		return votes;
	}

	/**
	 * @param votes the votes to set
	 */
	public void setVotes(List<Vote> votes) {
		this.votes = votes;
	}
	
	public Vote getVoteByName(String nom) {
		for (int i = 0; i < votes.size(); i++) {
			if (votes.get(i).getNom().equals(nom)) {
				return votes.get(i);
			}
		}
		return null;
	}
	
	public void removeVoteDoublons(String nom) {
		for (int i = 0; i < votes.size(); i++) {
			if (votes.get(i).getNom().equals(nom)) {
				votes.remove(i);
			}
		}
	}

}

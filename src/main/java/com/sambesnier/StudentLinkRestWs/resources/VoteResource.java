package com.sambesnier.StudentLinkRestWs.resources;

import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sambesnier.context.VoteContext;
import com.sambesnier.models.Vote;

@Path("votes")
public class VoteResource {

	@SuppressWarnings("unchecked")
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVotes() {
		System.out.println("votes");
		List<Vote> votes = VoteContext.getContext().getVotes();
		
		JSONObject obj = new JSONObject();
		JSONArray array = new JSONArray();
		
		for (Iterator<Vote> iterator = votes.iterator(); iterator.hasNext();) {
			Vote vote = (Vote) iterator.next();
			JSONObject jsonVote = new JSONObject();
			jsonVote.put("question", vote.getQuestion());
			jsonVote.put("votants", vote.getVotants().size());
			jsonVote.put("yes", vote.getYes());
			jsonVote.put("no", vote.getNo());
			jsonVote.put("blur", vote.getBlur());
			jsonVote.put("username", vote.getNom());
			jsonVote.put("hasvoted", vote.getHasVoted().size());
			array.add(jsonVote);
		}
		
		obj.put("votes", array);
		
        return Response.ok(obj.toJSONString()).build();
    }
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response newVote(String payload) {
		try {
			JSONObject json = (JSONObject) new JSONParser().parse(payload);
			Vote vote = new Vote(json.get("question").toString(), json.get("username").toString());
			
			VoteContext.getContext().removeVoteDoublons(json.get("username").toString());
			
			VoteContext.getContext().getVotes().add(vote);
			
			return Response.ok(json.toJSONString()).build();
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return Response.status(Status.BAD_REQUEST).build();
		
	}
	
}

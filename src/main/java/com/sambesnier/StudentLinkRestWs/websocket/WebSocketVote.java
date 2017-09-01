package com.sambesnier.StudentLinkRestWs.websocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sambesnier.context.VoteContext;

@ApplicationScoped
@ServerEndpoint("/vote/{nom}")
public class WebSocketVote {

	private static Set<Session> clients = Collections.synchronizedSet(new HashSet<Session>());

	@OnOpen
	public void open(@PathParam("nom") String nom, Session session) {
		clients.add(session);
		try {
			JSONObject obj = new JSONObject();
			obj.put("header", 1);
			obj.put("question", VoteContext.getContext().getVoteByName(nom).getQuestion());

			session.getBasicRemote().sendText(JSONValue.toJSONString(obj));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@OnClose
	public void close(Session session) {
		clients.remove(session);
	}

	@OnError
	public void onError(Throwable error) {
	}

	@OnMessage
	public void handleMessage(@PathParam("nom") String nom, String message, Session session) {
		try {
			JSONObject obj = (JSONObject) new JSONParser().parse(message);
			traitement(obj, session, nom);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void traitement(JSONObject obj, Session session, String nom) {

		switch (Integer.parseInt(obj.get("header").toString())) {
		case 1: {
			String user = obj.get("user").toString();
			VoteContext.getContext().getVoteByName(nom).getVotants().add(user);
			sendStats(session, nom);
			break;
		}
		case 10: {
			String user = obj.get("user").toString();
			VoteContext.getContext().getVoteByName(nom).increaseYes(user);
			sendStats(session, nom);
			break;
		}
		case 20: {
			String user = obj.get("user").toString();
			VoteContext.getContext().getVoteByName(nom).increaseNo(user);
			sendStats(session, nom);
			break;
		}
		case 30: {
			String user = obj.get("user").toString();
			VoteContext.getContext().getVoteByName(nom).increaseBlur(user);
			sendStats(session, nom);
			break;
		}
		default:
			break;
		}
	}

	public void sendStats(Session session, String nom) {
		JSONObject obj = new JSONObject();
		obj.put("header", 3);
		obj.put("votants", VoteContext.getContext().getVoteByName(nom).getHasVoted().size() + "/"
				+ VoteContext.getContext().getVoteByName(nom).getVotants().size());
		obj.put("yes", VoteContext.getContext().getVoteByName(nom).getYes());
		obj.put("no", VoteContext.getContext().getVoteByName(nom).getNo());
		obj.put("blur", VoteContext.getContext().getVoteByName(nom).getBlur());

		try {
			synchronized (clients) {
				for (Session s : clients) {
					s.getBasicRemote().sendText(JSONValue.toJSONString(obj));
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

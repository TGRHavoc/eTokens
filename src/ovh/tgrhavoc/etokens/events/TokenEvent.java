package ovh.tgrhavoc.etokens.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import ovh.tgrhavoc.etokens.xml.Token;

public class TokenEvent extends Event implements Cancellable {
	private static HandlerList handlers = new HandlerList();
	
	Token tokenForThisEvent;
	Event eventFired;
	Player player;
	
	public Token getTokenForThisEvent() {
		return tokenForThisEvent;
	}

	public void setTokenForThisEvent(Token tokenForThisEvent) {
		this.tokenForThisEvent = tokenForThisEvent;
	}

	public Event getEventFired() {
		return eventFired;
	}

	public void setEventFired(Event eventFired) {
		this.eventFired = eventFired;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public TokenEvent(Player player, Token token){
		this.player = player;
		this.tokenForThisEvent = token;
	}
	
	boolean c = false;
	
	@Override
	public boolean isCancelled() {
		return c;
	}

	@Override
	public void setCancelled(boolean arg0) {		
		c = arg0;
	}

	public static HandlerList getHandlerList() {
        return handlers;
    }

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}

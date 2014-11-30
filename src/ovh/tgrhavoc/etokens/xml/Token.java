package ovh.tgrhavoc.etokens.xml;

public class Token {
	
	int tokenAmount;
	String message;
	String objective;
	String blockType;
	int blockAmount;
	int distance;
	String achievement;
	
	int kills;
	String entityType;
	
	boolean repeatable = false;
	
	public boolean isRepeatable() {
		return repeatable;
	}

	public void setRepeatable(boolean repeatable) {
		this.repeatable = repeatable;
	}

	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public int getTokenAmount() {
		return tokenAmount;
	}

	public void setTokenAmount(int tokenAmount) {
		this.tokenAmount = tokenAmount;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getObjective() {
		return objective;
	}

	public void setObjective(String objective) {
		this.objective = objective;
	}

	public String getBlockType() {
		return blockType;
	}

	public void setBlockType(String blockType) {
		this.blockType = blockType;
	}

	public int getBlockAmount() {
		return blockAmount;
	}

	public void setBlockAmount(int blockAmount) {
		this.blockAmount = blockAmount;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public String getAchievement() {
		return achievement;
	}

	public void setAchievement(String achievement) {
		this.achievement = achievement;
	}

	public Token() {
		// None Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "Token [tokenAmount=" + tokenAmount + ", message=" + message
				+ ", objective=" + objective + ", blockType=" + blockType
				+ ", blockAmount=" + blockAmount + ", distance=" + distance
				+ ", achievement=" + achievement + "]";
	}

}

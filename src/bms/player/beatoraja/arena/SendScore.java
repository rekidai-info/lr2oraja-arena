package bms.player.beatoraja.arena;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown=true)
public class SendScore {
    @JsonProperty("type")
    private String type;
    @JsonProperty("target")
    private String target;
    @JsonProperty("player_id")
    private String playerID;
    @JsonProperty("ex_score")
    private int exScore;

    public SendScore() {
        this.type = "SendScore";
        this.target = null;
        this.playerID = null;
        this.exScore = 0;
    }

    public static SendScore fromJson(final String json) {
        try {
            return new ObjectMapper().readValue(json, new TypeReference<SendScore>() {});
        } catch (final Exception e) {
            return null;
        }
    }

    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getPlayerID() {
        return playerID;
    }

    public void setPlayerID(String playerID) {
        this.playerID = playerID;
    }

    public int getExScore() {
        return exScore;
    }

    public void setExScore(int exScore) {
        this.exScore = exScore;
    }

    @Override
    public String toString() {
        return "SendScore{" +
                "type='" + type + '\'' +
                ", target='" + target + '\'' +
                ", playerID='" + playerID + '\'' +
                ", exScore=" + exScore +
                '}';
    }
}

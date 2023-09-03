package bms.player.beatoraja.arena;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown=true)
public class NewOpponent {
    @JsonProperty("play_mode")
    private String playMode;

    @JsonProperty("arena_class_number")
    private int arenaClassNumber;

    @JsonProperty("player_id")
    private String playerID;

    @JsonProperty("player_name")
    private String playerName;

    @JsonProperty("player_arena_class")
    private String playerArenaClass;

    @JsonProperty("player_skill_class")
    private String playerSkillClass;

    public static NewOpponent fromJson(final String json) {
        try {
            return new ObjectMapper().readValue(json, new TypeReference<NewOpponent>() {});
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

    public String getPlayMode() {
        return playMode;
    }

    public void setPlayMode(String playMode) {
        this.playMode = playMode;
    }

    public int getArenaClassNumber() {
        return arenaClassNumber;
    }

    public void setArenaClassNumber(int arenaClassNumber) {
        this.arenaClassNumber = arenaClassNumber;
    }

    public String getPlayerID() {
        return playerID;
    }

    public void setPlayerID(String playerID) {
        this.playerID = playerID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerArenaClass() {
        return playerArenaClass;
    }

    public void setPlayerArenaClass(String playerArenaClass) {
        this.playerArenaClass = playerArenaClass;
    }

    public String getPlayerSkillClass() {
        return playerSkillClass;
    }

    public void setPlayerSkillClass(String playerSkillClass) {
        this.playerSkillClass = playerSkillClass;
    }

    @Override
    public String toString() {
        return "NewOpponent{" +
                "playMode='" + playMode + '\'' +
                ", arenaClassNumber=" + arenaClassNumber +
                ", playerID='" + playerID + '\'' +
                ", playerName='" + playerName + '\'' +
                ", playerArenaClass='" + playerArenaClass + '\'' +
                ", playerSkillClass='" + playerSkillClass + '\'' +
                '}';
    }
}

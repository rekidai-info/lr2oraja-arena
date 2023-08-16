package bms.player.beatoraja.arena;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ArenaRoom {
    @JsonProperty("error")
    private String error;

    @JsonProperty("id")
    private String id;

	@JsonProperty("player_confirmed")
    private boolean playerConfirmed;

	@JsonProperty("play_mode")
    private String playMode;

	@JsonProperty("arena_class_number")
    private int arenaClassNumber;

	@JsonProperty("player_ip1")
    private String playerIP1;

	@JsonProperty("player_ip2")
    private String playerIP2;

	@JsonProperty("player_ip3")
    private String playerIP3;

	@JsonProperty("player_ip4")
    private String playerIP4;

	@JsonProperty("player_id1")
    private String playerID1;

	@JsonProperty("player_id2")
    private String playerID2;

	@JsonProperty("player_id3")
    private String playerID3;

	@JsonProperty("player_id4")
    private String playerID4;

    @JsonProperty("player_name1")
    private String playerName1;

    @JsonProperty("player_name2")
    private String playerName2;

    @JsonProperty("player_name3")
    private String playerName3;

    @JsonProperty("player_name4")
    private String playerName4;

    @JsonProperty("player_arena_class1")
    private String playerArenaClass1;

    @JsonProperty("player_arena_class2")
    private String playerArenaClass2;

    @JsonProperty("player_arena_class3")
    private String playerArenaClass3;

    @JsonProperty("player_arena_class4")
    private String playerArenaClass4;

	@JsonProperty("player_allow_skip1")
    private Boolean playerAllowSkip1;

	@JsonProperty("player_allow_skip2")
    private Boolean playerAllowSkip2;

	@JsonProperty("player_allow_skip3")
    private Boolean playerAllowSkip3;

	@JsonProperty("player_allow_skip4")
    private Boolean playerAllowSkip4;

    @JsonProperty("player_ready_play_music1")
    private Boolean playerReadyPlayMusic1;

    @JsonProperty("player_ready_play_music2")
    private Boolean playerReadyPlayMusic2;

    @JsonProperty("player_ready_play_music3")
    private Boolean playerReadyPlayMusic3;

    @JsonProperty("player_ready_play_music4")
    private Boolean playerReadyPlayMusic4;

	@JsonProperty("song_hash1")
    private String songHash1;

	@JsonProperty("song_hash2")
    private String songHash2;

	@JsonProperty("song_hash3")
    private String songHash3;

	@JsonProperty("song_hash4")
    private String songHash4;

	@JsonProperty("song_available1")
    private boolean[] songAvailable1;

	@JsonProperty("song_available2")
    private boolean[] songAvailable2;

	@JsonProperty("song_available3")
    private boolean[] songAvailable3;

	@JsonProperty("song_available4")
    private boolean[] songAvailable4;

    @JsonProperty("random_seed1")
    private long randomSeed1;

    @JsonProperty("random_seed2")
    private long randomSeed2;

    @JsonProperty("random_seed3")
    private long randomSeed3;

    @JsonProperty("random_seed4")
    private long randomSeed4;
    
	@JsonProperty("ex_score1")
    private int[] exScore1;

	@JsonProperty("ex_score2")
    private int[] exScore2;

	@JsonProperty("ex_score3")
    private int[] exScore3;

	@JsonProperty("ex_score4")
    private int[] exScore4;

	@JsonProperty("last_update_millis1")
    private Long lastUpdateMillis1;

	@JsonProperty("last_update_millis2")
    private Long lastUpdateMillis2;

	@JsonProperty("last_update_millis3")
    private Long lastUpdateMillis3;

	@JsonProperty("last_update_millis4")
    private Long lastUpdateMillis4;

    @JsonProperty("last_update_millis")
    private long lastUpdateMillis;

    public int getPlayerCount() {
        int count = 0;

        if (getPlayerID1() != null) {
            ++count;
        }
        if (getPlayerID2() != null) {
            ++count;
        }
        if (getPlayerID3() != null) {
            ++count;
        }
        if (getPlayerID4() != null) {
            ++count;
        }

        return count;
    }
    public int isSong1Available() {
        if (getPlayerID1() == null) {
            return +1;
        }
        
        if (getPlayerID1() != null && getSongAvailable1() == null) {
            return -1;
        }
        if (getPlayerID2() != null && getSongAvailable2() == null) {
            return -1;
        }
        if (getPlayerID3() != null && getSongAvailable3() == null) {
            return -1;
        }
        if (getPlayerID4() != null && getSongAvailable4() == null) {
            return -1;
        }

        return  (getPlayerID1() == null || getSongAvailable1()[0]) && 
                (getPlayerID2() == null || getSongAvailable2()[0]) && 
                (getPlayerID3() == null || getSongAvailable3()[0]) && 
                (getPlayerID4() == null || getSongAvailable4()[0]) ? +1 : 0;
    }

    public int isSong2Available() {
        if (getPlayerID2() == null) {
            return +1;
        }

        if (getPlayerID1() != null && getSongAvailable1() == null) {
            return -1;
        }
        if (getPlayerID2() != null && getSongAvailable2() == null) {
            return -1;
        }
        if (getPlayerID3() != null && getSongAvailable3() == null) {
            return -1;
        }
        if (getPlayerID4() != null && getSongAvailable4() == null) {
            return -1;
        }

        return  (getPlayerID1() == null || getSongAvailable1()[1]) &&
                (getPlayerID2() == null || getSongAvailable2()[1]) &&
                (getPlayerID3() == null || getSongAvailable3()[1]) &&
                (getPlayerID4() == null || getSongAvailable4()[1]) ? +1 : 0;
    }

    public int isSong3Available() {
        if (getPlayerID3() == null) {
            return +1;
        }

        if (getPlayerID1() != null && getSongAvailable1() == null) {
            return -1;
        }
        if (getPlayerID2() != null && getSongAvailable2() == null) {
            return -1;
        }
        if (getPlayerID3() != null && getSongAvailable3() == null) {
            return -1;
        }
        if (getPlayerID4() != null && getSongAvailable4() == null) {
            return -1;
        }

        return  (getPlayerID1() == null || getSongAvailable1()[2]) &&
                (getPlayerID2() == null || getSongAvailable2()[2]) &&
                (getPlayerID3() == null || getSongAvailable3()[2]) &&
                (getPlayerID4() == null || getSongAvailable4()[2]) ? +1 : 0;
    }

    public int isSong4Available() {
        if (getPlayerID4() == null) {
            return +1;
        }

        if (getPlayerID1() != null && getSongAvailable1() == null) {
            return -1;
        }
        if (getPlayerID2() != null && getSongAvailable2() == null) {
            return -1;
        }
        if (getPlayerID3() != null && getSongAvailable3() == null) {
            return -1;
        }
        if (getPlayerID4() != null && getSongAvailable4() == null) {
            return -1;
        }

        return  (getPlayerID1() == null || getSongAvailable1()[3]) &&
                (getPlayerID2() == null || getSongAvailable2()[3]) &&
                (getPlayerID3() == null || getSongAvailable3()[3]) &&
                (getPlayerID4() == null || getSongAvailable4()[3]) ? +1 : 0;
    }

    public static ArenaRoom fromJson(final String json) {
        try {
            return new ObjectMapper().readValue(json, new TypeReference<ArenaRoom>() {});
        } catch (Exception e) {
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }
    }

    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isPlayerConfirmed() {
        return playerConfirmed;
    }

    public void setPlayerConfirmed(boolean playerConfirmed) {
        this.playerConfirmed = playerConfirmed;
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

    public String getPlayerIP1() {
        return playerIP1;
    }

    public void setPlayerIP1(String playerIP1) {
        this.playerIP1 = playerIP1;
    }

    public String getPlayerIP2() {
        return playerIP2;
    }

    public void setPlayerIP2(String playerIP2) {
        this.playerIP2 = playerIP2;
    }

    public String getPlayerIP3() {
        return playerIP3;
    }

    public void setPlayerIP3(String playerIP3) {
        this.playerIP3 = playerIP3;
    }

    public String getPlayerIP4() {
        return playerIP4;
    }

    public void setPlayerIP4(String playerIP4) {
        this.playerIP4 = playerIP4;
    }

    public String getPlayerID1() {
        return playerID1;
    }

    public void setPlayerID1(String playerID1) {
        this.playerID1 = playerID1;
    }

    public String getPlayerID2() {
        return playerID2;
    }

    public void setPlayerID2(String playerID2) {
        this.playerID2 = playerID2;
    }

    public String getPlayerID3() {
        return playerID3;
    }

    public void setPlayerID3(String playerID3) {
        this.playerID3 = playerID3;
    }

    public String getPlayerID4() {
        return playerID4;
    }

    public void setPlayerID4(String playerID4) {
        this.playerID4 = playerID4;
    }

    public String getPlayerName1() {
        return playerName1;
    }

    public void setPlayerName1(String playerName1) {
        this.playerName1 = playerName1;
    }

    public String getPlayerName2() {
        return playerName2;
    }

    public void setPlayerName2(String playerName2) {
        this.playerName2 = playerName2;
    }

    public String getPlayerName3() {
        return playerName3;
    }

    public void setPlayerName3(String playerName3) {
        this.playerName3 = playerName3;
    }

    public String getPlayerName4() {
        return playerName4;
    }

    public void setPlayerName4(String playerName4) {
        this.playerName4 = playerName4;
    }

    public String getPlayerArenaClass1() {
        return playerArenaClass1;
    }

    public void setPlayerArenaClass1(String playerArenaClass1) {
        this.playerArenaClass1 = playerArenaClass1;
    }

    public String getPlayerArenaClass2() {
        return playerArenaClass2;
    }

    public void setPlayerArenaClass2(String playerArenaClass2) {
        this.playerArenaClass2 = playerArenaClass2;
    }

    public String getPlayerArenaClass3() {
        return playerArenaClass3;
    }

    public void setPlayerArenaClass3(String playerArenaClass3) {
        this.playerArenaClass3 = playerArenaClass3;
    }

    public String getPlayerArenaClass4() {
        return playerArenaClass4;
    }

    public void setPlayerArenaClass4(String playerArenaClass4) {
        this.playerArenaClass4 = playerArenaClass4;
    }

    public Boolean getPlayerAllowSkip1() {
        return playerAllowSkip1;
    }

    public void setPlayerAllowSkip1(Boolean playerAllowSkip1) {
        this.playerAllowSkip1 = playerAllowSkip1;
    }

    public Boolean getPlayerAllowSkip2() {
        return playerAllowSkip2;
    }

    public void setPlayerAllowSkip2(Boolean playerAllowSkip2) {
        this.playerAllowSkip2 = playerAllowSkip2;
    }

    public Boolean getPlayerAllowSkip3() {
        return playerAllowSkip3;
    }

    public void setPlayerAllowSkip3(Boolean playerAllowSkip3) {
        this.playerAllowSkip3 = playerAllowSkip3;
    }

    public Boolean getPlayerAllowSkip4() {
        return playerAllowSkip4;
    }

    public void setPlayerAllowSkip4(Boolean playerAllowSkip4) {
        this.playerAllowSkip4 = playerAllowSkip4;
    }

    public Boolean getPlayerReadyPlayMusic1() {
        return playerReadyPlayMusic1;
    }

    public void setPlayerReadyPlayMusic1(Boolean playerReadyPlayMusic1) {
        this.playerReadyPlayMusic1 = playerReadyPlayMusic1;
    }

    public Boolean getPlayerReadyPlayMusic2() {
        return playerReadyPlayMusic2;
    }

    public void setPlayerReadyPlayMusic2(Boolean playerReadyPlayMusic2) {
        this.playerReadyPlayMusic2 = playerReadyPlayMusic2;
    }

    public Boolean getPlayerReadyPlayMusic3() {
        return playerReadyPlayMusic3;
    }

    public void setPlayerReadyPlayMusic3(Boolean playerReadyPlayMusic3) {
        this.playerReadyPlayMusic3 = playerReadyPlayMusic3;
    }

    public Boolean getPlayerReadyPlayMusic4() {
        return playerReadyPlayMusic4;
    }

    public void setPlayerReadyPlayMusic4(Boolean playerReadyPlayMusic4) {
        this.playerReadyPlayMusic4 = playerReadyPlayMusic4;
    }

    public String getSongHash1() {
        return songHash1;
    }

    public void setSongHash1(String songHash1) {
        this.songHash1 = songHash1;
    }

    public String getSongHash2() {
        return songHash2;
    }

    public void setSongHash2(String songHash2) {
        this.songHash2 = songHash2;
    }

    public String getSongHash3() {
        return songHash3;
    }

    public void setSongHash3(String songHash3) {
        this.songHash3 = songHash3;
    }

    public String getSongHash4() {
        return songHash4;
    }

    public void setSongHash4(String songHash4) {
        this.songHash4 = songHash4;
    }

    public boolean[] getSongAvailable1() {
        return songAvailable1;
    }

    public void setSongAvailable1(boolean[] songAvailable1) {
        this.songAvailable1 = songAvailable1;
    }

    public boolean[] getSongAvailable2() {
        return songAvailable2;
    }

    public void setSongAvailable2(boolean[] songAvailable2) {
        this.songAvailable2 = songAvailable2;
    }

    public boolean[] getSongAvailable3() {
        return songAvailable3;
    }

    public void setSongAvailable3(boolean[] songAvailable3) {
        this.songAvailable3 = songAvailable3;
    }

    public boolean[] getSongAvailable4() {
        return songAvailable4;
    }

    public void setSongAvailable4(boolean[] songAvailable4) {
        this.songAvailable4 = songAvailable4;
    }

    public long getRandomSeed1() {
        return randomSeed1;
    }

    public void setRandomSeed1(long randomSeed1) {
        this.randomSeed1 = randomSeed1;
    }

    public long getRandomSeed2() {
        return randomSeed2;
    }

    public void setRandomSeed2(long randomSeed2) {
        this.randomSeed2 = randomSeed2;
    }

    public long getRandomSeed3() {
        return randomSeed3;
    }

    public void setRandomSeed3(long randomSeed3) {
        this.randomSeed3 = randomSeed3;
    }

    public long getRandomSeed4() {
        return randomSeed4;
    }

    public void setRandomSeed4(long randomSeed4) {
        this.randomSeed4 = randomSeed4;
    }

    public int[] getExScore1() {
        return exScore1;
    }

    public void setExScore1(int[] exScore1) {
        this.exScore1 = exScore1;
    }

    public int[] getExScore2() {
        return exScore2;
    }

    public void setExScore2(int[] exScore2) {
        this.exScore2 = exScore2;
    }

    public int[] getExScore3() {
        return exScore3;
    }

    public void setExScore3(int[] exScore3) {
        this.exScore3 = exScore3;
    }

    public int[] getExScore4() {
        return exScore4;
    }

    public void setExScore4(int[] exScore4) {
        this.exScore4 = exScore4;
    }

    public Long getLastUpdateMillis1() {
        return lastUpdateMillis1;
    }

    public void setLastUpdateMillis1(Long lastUpdateMillis1) {
        this.lastUpdateMillis1 = lastUpdateMillis1;
    }

    public Long getLastUpdateMillis2() {
        return lastUpdateMillis2;
    }

    public void setLastUpdateMillis2(Long lastUpdateMillis2) {
        this.lastUpdateMillis2 = lastUpdateMillis2;
    }

    public Long getLastUpdateMillis3() {
        return lastUpdateMillis3;
    }

    public void setLastUpdateMillis3(Long lastUpdateMillis3) {
        this.lastUpdateMillis3 = lastUpdateMillis3;
    }

    public Long getLastUpdateMillis4() {
        return lastUpdateMillis4;
    }

    public void setLastUpdateMillis4(Long lastUpdateMillis4) {
        this.lastUpdateMillis4 = lastUpdateMillis4;
    }

    public long getLastUpdateMillis() {
        return lastUpdateMillis;
    }

    public void setLastUpdateMillis(long lastUpdateMillis) {
        this.lastUpdateMillis = lastUpdateMillis;
    }

    @Override
    public String toString() {
        return "ArenaRoom{" +
                "error='" + error + '\'' +
                ", id='" + id + '\'' +
                ", playerConfirmed=" + playerConfirmed +
                ", playMode='" + playMode + '\'' +
                ", arenaClassNumber=" + arenaClassNumber +
                ", playerIP1='" + playerIP1 + '\'' +
                ", playerIP2='" + playerIP2 + '\'' +
                ", playerIP3='" + playerIP3 + '\'' +
                ", playerIP4='" + playerIP4 + '\'' +
                ", playerID1='" + playerID1 + '\'' +
                ", playerID2='" + playerID2 + '\'' +
                ", playerID3='" + playerID3 + '\'' +
                ", playerID4='" + playerID4 + '\'' +
                ", playerName1='" + playerName1 + '\'' +
                ", playerName2='" + playerName2 + '\'' +
                ", playerName3='" + playerName3 + '\'' +
                ", playerName4='" + playerName4 + '\'' +
                ", playerArenaClass1='" + playerArenaClass1 + '\'' +
                ", playerArenaClass2='" + playerArenaClass2 + '\'' +
                ", playerArenaClass3='" + playerArenaClass3 + '\'' +
                ", playerArenaClass4='" + playerArenaClass4 + '\'' +
                ", playerAllowSkip1=" + playerAllowSkip1 +
                ", playerAllowSkip2=" + playerAllowSkip2 +
                ", playerAllowSkip3=" + playerAllowSkip3 +
                ", playerAllowSkip4=" + playerAllowSkip4 +
                ", playerReadyPlayMusic1=" + playerReadyPlayMusic1 +
                ", playerReadyPlayMusic2=" + playerReadyPlayMusic2 +
                ", playerReadyPlayMusic3=" + playerReadyPlayMusic3 +
                ", playerReadyPlayMusic4=" + playerReadyPlayMusic4 +
                ", songHash1='" + songHash1 + '\'' +
                ", songHash2='" + songHash2 + '\'' +
                ", songHash3='" + songHash3 + '\'' +
                ", songHash4='" + songHash4 + '\'' +
                ", songAvailable1=" + Arrays.toString(songAvailable1) +
                ", songAvailable2=" + Arrays.toString(songAvailable2) +
                ", songAvailable3=" + Arrays.toString(songAvailable3) +
                ", songAvailable4=" + Arrays.toString(songAvailable4) +
                ", randomSeed1=" + randomSeed1 +
                ", randomSeed2=" + randomSeed2 +
                ", randomSeed3=" + randomSeed3 +
                ", randomSeed4=" + randomSeed4 +
                ", exScore1=" + Arrays.toString(exScore1) +
                ", exScore2=" + Arrays.toString(exScore2) +
                ", exScore3=" + Arrays.toString(exScore3) +
                ", exScore4=" + Arrays.toString(exScore4) +
                ", lastUpdateMillis1=" + lastUpdateMillis1 +
                ", lastUpdateMillis2=" + lastUpdateMillis2 +
                ", lastUpdateMillis3=" + lastUpdateMillis3 +
                ", lastUpdateMillis4=" + lastUpdateMillis4 +
                ", lastUpdateMillis=" + lastUpdateMillis +
                '}';
    }
}
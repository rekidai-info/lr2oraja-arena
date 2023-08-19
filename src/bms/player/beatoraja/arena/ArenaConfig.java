package bms.player.beatoraja.arena;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Pattern;

public class ArenaConfig {
    public static final ArenaConfig INSTANCE = new ArenaConfig();

    private final String httpServerUrl;
    private final String mqServerUrl;
    private final String playerName;
    private final String playerID;
    private final String arenaClass;
    private final int arenaClassNumber;

    private ArenaConfig() {
        final Properties properties = new Properties();

        try (final Reader reader = Files.newBufferedReader(Paths.get("arena.properties"), StandardCharsets.UTF_8)) {
            properties.load(reader);
        } catch (IOException e) {
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }

        httpServerUrl = String.class.cast(properties.get("HTTP_SERVER_URL"));
        if (httpServerUrl == null || httpServerUrl.isBlank()) {
            throw new RuntimeException("Set HTTP_SERVER_URL");
        }
        mqServerUrl = String.class.cast(properties.get("MQ_SERVER_URL"));
        if (mqServerUrl == null || mqServerUrl.isBlank()) {
            throw new RuntimeException("Set MQ_SERVER_URL");
        }
        playerName = String.class.cast(properties.get("PLAYER_NAME"));
        if (playerName == null || playerName.isBlank()) {
            throw new RuntimeException("Set PLAYER_NAME");
        }
        playerID = UUID.randomUUID().toString().replaceAll("-", "");
        arenaClass = String.class.cast(properties.get("IIDX_ARENA_CLASS"));
        if (arenaClass == null || arenaClass.isBlank()) {
            throw new RuntimeException("Set IIDX_ARENA_CLASS");
        }
        
        switch (arenaClass == null ? "" : arenaClass) {
            case "S1":
                arenaClassNumber = 25;
                break;
            case "S2":
                arenaClassNumber = 24;
                break;
            case "S3":
                arenaClassNumber = 23;
                break;
            case "S4":
                arenaClassNumber = 22;
                break;
            case "S5":
                arenaClassNumber = 21;
                break;
            case "A1":
                arenaClassNumber = 20;
                break;
            case "A2":
                arenaClassNumber = 19;
                break;
            case "A3":
                arenaClassNumber = 18;
                break;
            case "A4":
                arenaClassNumber = 17;
                break;
            case "A5":
                arenaClassNumber = 16;
                break;
            case "B1":
                arenaClassNumber = 15;
                break;
            case "B2":
                arenaClassNumber = 14;
                break;
            case "B3":
                arenaClassNumber = 13;
                break;
            case "B4":
                arenaClassNumber = 12;
                break;
            case "B5":
                arenaClassNumber = 11;
                break;
            case "C1":
                arenaClassNumber = 10;
                break;
            case "C2":
                arenaClassNumber = 9;
                break;
            case "C3":
                arenaClassNumber = 8;
                break;
            case "C4":
                arenaClassNumber = 7;
                break;
            case "C5":
                arenaClassNumber = 6;
                break;
            case "D1":
                arenaClassNumber = 5;
                break;
            case "D2":
                arenaClassNumber = 4;
                break;
            case "D3":
                arenaClassNumber = 3;
                break;
            case "D4":
                arenaClassNumber = 2;
                break;
            case "D5":
                arenaClassNumber = 1;
                break;
            default:
                int n = 100;
                for (int i = 0; i < arenaClass.length(); ++i) {
                    n += ((int) arenaClass.charAt(i)) * (i + 1) * 10;
                }
                arenaClassNumber = n;
                break;
        }
    }

    public String getHttpServerUrl() {
        return httpServerUrl;
    }

    public String getMQServerUrl() {
        return mqServerUrl;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getPlayerID() {
        return playerID;
    }

    public String getArenaClass() {
        return arenaClass;
    }

    public int getArenaClassNumber() {
        return arenaClassNumber;
    }

    @Override
    public String toString() {
        return "ArenaConfig{" +
                "httpServerUrl='" + httpServerUrl + '\'' +
                ", mqServerUrl='" + mqServerUrl + '\'' +
                ", playerName='" + playerName + '\'' +
                ", playerID='" + playerID + '\'' +
                ", arenaClass='" + arenaClass + '\'' +
                '}';
    }
}
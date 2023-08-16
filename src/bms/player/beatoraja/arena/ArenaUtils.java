package bms.player.beatoraja.arena;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import bms.model.Mode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ArenaUtils {
    private static final int VERSION = 1;
    private static final ExecutorService THREAD_POOL = Executors.newSingleThreadExecutor();

    public static void close() {
    }

    public static void shutdown() {
        THREAD_POOL.shutdown();
        close();
    }

    private static synchronized HttpResponse<String> post(final Map<String, String> params, final String path)
            throws IOException, InterruptedException {
        final HttpClient client = HttpClient.newBuilder().build();
        final String form = params.entrySet().stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
        final BodyPublisher publisher = HttpRequest.BodyPublishers.ofString(form);
        final HttpRequest request = HttpRequest.newBuilder(URI.create(ArenaConfig.INSTANCE.getHttpServerUrl() + path))
                .POST(publisher).headers("Content-Type", "application/x-www-form-urlencoded").build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static ArenaRoom joinArena(final Mode playMode, final String playerID, final String playerName, final String arenaClass, final int arenaClassNumber, final boolean playerAllowSkip) {
        final Map<String, String> params = Map.of("client_version", String.valueOf(VERSION), "play_mode", playMode.name(), "player_id", playerID, "player_name", playerName, "player_arena_class", arenaClass, "arena_class_number", String.valueOf(arenaClassNumber), "player_allow_skip", String.valueOf(playerAllowSkip));

        try {
            final HttpResponse<String> response = post(params, "/arena/join");

            if (response == null) {
                Logger.getGlobal().log(Level.WARNING, "Response is null");
                return null;
            }

            if (response.statusCode() == 200) {
                return ArenaRoom.fromJson(response.body());
            } else {
                Logger.getGlobal().log(Level.WARNING, "Response is not ok" + response);
            }
        } catch (final Exception e) {
            Logger.getGlobal().log(Level.WARNING, e.getLocalizedMessage(), e);
        }

        return null;
    }

    public static ArenaRoom decideMusic(final String roomID, final String playerID, final String songHash, final boolean songAvailable1, final boolean songAvailable2, final boolean songAvailable3, final boolean songAvailable4) {
        final Map<String, String> params = new HashMap<String, String>();

        params.put("client_version", String.valueOf(VERSION));
        params.put("id", roomID);
        params.put("player_id", playerID);
        if (songHash != null) {
            params.put("song_hash", songHash);
        }
        params.put("song_available1", String.valueOf(songAvailable1));
        params.put("song_available2", String.valueOf(songAvailable2));
        params.put("song_available3", String.valueOf(songAvailable3));
        params.put("song_available4", String.valueOf(songAvailable4));

        try {
            final HttpResponse<String> response = post(params, "/arena/decide_music");

            if (response == null) {
                Logger.getGlobal().log(Level.WARNING, "Response is null");
                return null;
            }

            if (response.statusCode() == 200) {
                return ArenaRoom.fromJson(response.body());
            } else {
                Logger.getGlobal().log(Level.WARNING, "Response is not ok" + response);
            }
        } catch (final Exception e) {
            Logger.getGlobal().log(Level.WARNING, e.getLocalizedMessage(), e);
        }

        return null;
    }

    public static ArenaRoom readyPlayMusic(final String roomID, final String playerID, final boolean playerReadyPlayMusic) {
        final Map<String, String> params = Map.of("client_version", String.valueOf(VERSION), "id", roomID, "player_id", playerID, "player_ready_play_music", String.valueOf(playerReadyPlayMusic));

        try {
            final HttpResponse<String> response = post(params, "/arena/ready_play_music");

            if (response == null) {
                Logger.getGlobal().log(Level.WARNING, "Response is null");
                return null;
            }

            if (response.statusCode() == 200) {
                return ArenaRoom.fromJson(response.body());
            } else {
                Logger.getGlobal().log(Level.WARNING, "Response is not ok" + response);
            }
        } catch (final Exception e) {
            Logger.getGlobal().log(Level.WARNING, e.getLocalizedMessage(), e);
        }
        
        return null;
    }

    public static ArenaRoom updateScore(final String roomID, final String playerID, final int songOrder, final int score) {
        final Map<String, String> params = Map.of("client_version", String.valueOf(VERSION),"id", roomID,"player_id", playerID,"song_order", String.valueOf(songOrder), "score", String.valueOf(score));

        try {
            final HttpResponse<String> response = post(params, "/arena/update_score");

            if (response == null) {
                Logger.getGlobal().log(Level.WARNING, "Response is null");
                return null;
            }

            if (response.statusCode() == 200) {
                return ArenaRoom.fromJson(response.body());
            } else {
                Logger.getGlobal().log(Level.WARNING, "Response is not ok" + response);
            }
        } catch (final Exception e) {
            Logger.getGlobal().log(Level.WARNING, e.getLocalizedMessage(), e);
        }

        return null;
    }

    public static ArenaRoom updateLastUpdate(final String roomID, final String playerID) {
        final Map<String, String> params = new HashMap<String, String>();

        params.put("client_version", String.valueOf(VERSION));
        params.put("id", roomID);
        if (playerID != null) {
            params.put("player_id", playerID);
        }

        try {
            final HttpResponse<String> response = post(params, "/arena/update_last_update");

            if (response == null) {
                Logger.getGlobal().log(Level.WARNING, "Response is null");
                return null;
            }

            if (response.statusCode() == 200) {
                return ArenaRoom.fromJson(response.body());
            } else {
                Logger.getGlobal().log(Level.WARNING, "Response is not ok" + response);
            }
        } catch (final Exception e) {
            Logger.getGlobal().log(Level.WARNING, e.getLocalizedMessage(), e);
        }
        
        return null;
    }

    public static Future<?> joinArenaAsync(final Mode playMode, final String playerID, final String playerName, final String arenaClass, final int arenaClassNumber, final boolean playerAllowSkip) {
        return THREAD_POOL.submit(() -> joinArena(playMode, playerID, playerName, arenaClass, arenaClassNumber, playerAllowSkip));
    }

    public static Future<?> decideMusicAsync(final String roomID, final String playerID, final String songHash, final boolean songAvailable1, final boolean songAvailable2, final boolean songAvailable3, final boolean songAvailable4) {
        return THREAD_POOL.submit(() -> decideMusic(roomID, playerID, songHash, songAvailable1, songAvailable2, songAvailable3, songAvailable4));
    }

    public static Future<?> readyPlayMusicAsync(final String roomID, final String playerID, final boolean playerReadyPlayMusic) {
        return THREAD_POOL.submit(() -> readyPlayMusic(roomID, playerID, playerReadyPlayMusic));
    }

    public static Future<?> updateScoreAsync(final String roomID, final String playerID, final int songOrder, final int score) {
        return THREAD_POOL.submit(() -> updateScore(roomID, playerID, songOrder, score));
    }

    public static Future<?> updateLastUpdateAsync(final String roomID, final String playerID) {
        return THREAD_POOL.submit(() -> updateLastUpdate(roomID, playerID));
    }
}

package bms.player.beatoraja.arena;

import bms.player.beatoraja.song.SongData;
import bms.player.beatoraja.song.SongDatabaseAccessor;

import java.util.*;

public class ArenaMatchEachResult {
    public static List<ArenaMatchEachResult> calcResults(final ArenaRoom arenaRoom, final int songs, final SongDatabaseAccessor songdb) {
        final String[] hashes = new String[] { arenaRoom.getSongHash1(), arenaRoom.getSongHash2(), arenaRoom.getSongHash3(), arenaRoom.getSongHash4() };
        final List<ArenaMatchEachResult> result = new ArrayList<ArenaMatchEachResult>();

        for (int order = 0; order <= songs; ++order) {
            if (order >= hashes.length) {
                break;
            }

            final String hash = hashes[songs];

            if (hash == null || hash.isBlank()) {
                continue;
            }

            final SongData[] songDataArray = songdb.getSongDatas(new String[] { hash });
            SongData songData;

            if (songDataArray == null || songDataArray.length < 1) {
                continue;
            } else {
                songData = songDataArray[0];
            }

            if (songData == null) {
                continue;
            }

            final ArenaMatchEachResult eachResult = new ArenaMatchEachResult();

            eachResult.song = songData.getDisplayString();
            eachResult.results = ArenaMatchResult.calcResult(arenaRoom, order);

            result.add(eachResult);
        }

        return result;
    }

    private String song;
    private List<ArenaMatchResult> results;

    public String getSong() {
        return song;
    }

    public List<ArenaMatchResult> getResults() {
        return results;
    }

    @Override
    public String toString() {
        return "ArenaMatchEachResult{" +
                "song='" + song + '\'' +
                ", results=" + results +
                '}';
    }
}

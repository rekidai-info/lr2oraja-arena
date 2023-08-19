package bms.player.beatoraja.arena;

import java.util.*;

public class ArenaMatchResult {
    public static List<ArenaMatchResult> calcResults(final ArenaRoom arenaRoom, final int songs) {
        final List<List<ArenaMatchResult>> list = new ArrayList<List<ArenaMatchResult>>();

        for (int i = 0; i < songs; ++i) {
            list.add(calcResult(arenaRoom, i));
        }
        
        int exScores1 = 0, pts1 = 0;
        int exScores2 = 0, pts2 = 0;
        int exScores3 = 0, pts3 = 0;
        int exScores4 = 0, pts4 = 0;

        for (int i = 0; i < list.size(); ++i) {
            final List<ArenaMatchResult> list2 = list.get(i);

            for (int j = 0; j < list2.size(); ++j) {
                switch (list2.get(j).getNo()) {
                    case 0:
                        exScores1 += list2.get(j).getEXScore();
                        pts1 += list2.get(j).getPt();
                        break;
                    case 1:
                        exScores2 += list2.get(j).getEXScore();
                        pts2 += list2.get(j).getPt();
                        break;
                    case 2:
                        exScores3 += list2.get(j).getEXScore();
                        pts3 += list2.get(j).getPt();
                        break;
                    case 3:
                        exScores4 += list2.get(j).getEXScore();
                        pts4 += list2.get(j).getPt();
                        break;
                }
            }
        }

        final List<ArenaMatchResult> result = new ArrayList<ArenaMatchResult>();

        if (arenaRoom.getPlayerID1() != null) {
            result.add(new ArenaMatchResult(0, arenaRoom.getPlayerID1(), arenaRoom.getPlayerName1(), arenaRoom.getPlayerArenaClass1(), exScores1, pts1));
        }
        if (arenaRoom.getPlayerID2() != null) {
            result.add(new ArenaMatchResult(1, arenaRoom.getPlayerID2(), arenaRoom.getPlayerName2(), arenaRoom.getPlayerArenaClass2(), exScores2, pts2));
        }
        if (arenaRoom.getPlayerID3() != null) {
            result.add(new ArenaMatchResult(2, arenaRoom.getPlayerID3(), arenaRoom.getPlayerName3(), arenaRoom.getPlayerArenaClass3(), exScores3, pts3));
        }
        if (arenaRoom.getPlayerID4() != null) {
            result.add(new ArenaMatchResult(3, arenaRoom.getPlayerID4(), arenaRoom.getPlayerName4(), arenaRoom.getPlayerArenaClass4(), exScores4, pts4));
        }

        Collections.sort(result, (lhs, rhs) -> {
            if (lhs.getPt() < rhs.getPt()) {
                return +1;
            }
            if (lhs.getPt() > rhs.getPt()) {
                return -1;
            }

            if (lhs.getEXScore() < rhs.getEXScore()) {
                return +1;
            }
            if (lhs.getEXScore() > rhs.getEXScore()) {
                return -1;
            }

            return 0;
        });

        return result;
    }

    public static List<ArenaMatchResult> calcResult(final ArenaRoom arenaRoom, int order) {
        final List<ArenaMatchResult> list = new ArrayList<ArenaMatchResult>();

        if (arenaRoom.getPlayerID1() != null && arenaRoom.getExScore1() != null && arenaRoom.getExScore1().length >= order + 1 && arenaRoom.getExScore1()[order] >= 0) {
            final ArenaMatchResult result = new ArenaMatchResult();

            result.setNo(0);
            result.setPlayerID(arenaRoom.getPlayerID1());
            result.setPlayerName(arenaRoom.getPlayerName1());
            result.setArenaClass(arenaRoom.getPlayerArenaClass1());
            result.setEXScore(arenaRoom.getExScore1()[order]);
            result.setPt(0);

            list.add(result);
        }
        if (arenaRoom.getPlayerID2() != null && arenaRoom.getExScore2() != null && arenaRoom.getExScore2().length >= order + 1 && arenaRoom.getExScore2()[order] >= 0) {
            final ArenaMatchResult result = new ArenaMatchResult();

            result.setNo(1);
            result.setPlayerID(arenaRoom.getPlayerID2());
            result.setPlayerName(arenaRoom.getPlayerName2());
            result.setArenaClass(arenaRoom.getPlayerArenaClass2());
            result.setEXScore(arenaRoom.getExScore2()[order]);
            result.setPt(0);

            list.add(result);
        }
        if (arenaRoom.getPlayerID3() != null && arenaRoom.getExScore3() != null && arenaRoom.getExScore3().length >= order + 1 && arenaRoom.getExScore3()[order] >= 0) {
            final ArenaMatchResult result = new ArenaMatchResult();

            result.setNo(2);
            result.setPlayerID(arenaRoom.getPlayerID3());
            result.setPlayerName(arenaRoom.getPlayerName3());
            result.setArenaClass(arenaRoom.getPlayerArenaClass3());
            result.setEXScore(arenaRoom.getExScore3()[order]);
            result.setPt(0);

            list.add(result);
        }
        if (arenaRoom.getPlayerID4() != null && arenaRoom.getExScore4() != null && arenaRoom.getExScore4().length >= order + 1 && arenaRoom.getExScore4()[order] >= 0) {
            final ArenaMatchResult result = new ArenaMatchResult();

            result.setNo(3);
            result.setPlayerID(arenaRoom.getPlayerID4());
            result.setPlayerName(arenaRoom.getPlayerName4());
            result.setArenaClass(arenaRoom.getPlayerArenaClass4());
            result.setEXScore(arenaRoom.getExScore4()[order]);
            result.setPt(0);

            list.add(result);
        }

        Collections.sort(list, (lhs, rhs) -> {
            if (lhs.getEXScore() < rhs.getEXScore()) {
                return +1;
            }
            if (lhs.getEXScore() > rhs.getEXScore()) {
                return -1;
            }

            return 0;
        });

        int prevScore = list.isEmpty() ? 0 : list.get(0).getEXScore();
        int pt = 2;

        for (int i = 0; i < list.size(); ++i) {
            if (prevScore == list.get(i).getEXScore()) {
                list.get(i).setPt(pt);
            } else {
                list.get(i).setPt(--pt);
            }
            prevScore = list.get(i).getEXScore();
            if (pt <= 0)  {
                break;
            }
        }

        return list;
    }

    private int no;
    private String playerID;
    private String playerName;
    private String arenaClass;
    private int exScore;
    private int pt;

    public ArenaMatchResult() {
    }

    public ArenaMatchResult(int no, String playerID, String playerName, String arenaClass, int exScore, int pt) {
        this.no = no;
        this.playerID = playerID;
        this.playerName = playerName;
        this.arenaClass = arenaClass;
        this.exScore = exScore;
        this.pt = pt;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
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

    public String getArenaClass() {
        return arenaClass;
    }

    public void setArenaClass(String arenaClass) {
        this.arenaClass = arenaClass;
    }

    public int getEXScore() {
        return exScore;
    }

    public void setEXScore(int exScore) {
        this.exScore = exScore;
    }

    public int getPt() {
        return pt;
    }

    public void setPt(int pt) {
        this.pt = pt;
    }

    @Override
    public String toString() {
        return "ArenaMatchResult{" +
                "no=" + no +
                ", playerID='" + playerID + '\'' +
                ", playerName='" + playerName + '\'' +
                ", arenaClass='" + arenaClass + '\'' +
                ", exScore=" + exScore +
                ", pt=" + pt +
                '}';
    }
}
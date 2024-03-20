package bms.player.beatoraja.arena;

import static bms.player.beatoraja.SystemSoundManager.SoundType.SELECT;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

import bms.player.beatoraja.arena.font.FontUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import bms.player.beatoraja.MainController;
import bms.player.beatoraja.MainState;
import bms.player.beatoraja.input.BMSPlayerInputProcessor;
import bms.player.beatoraja.input.KeyBoardInputProcesseor.ControlKeys;
import bms.player.beatoraja.select.PreviewMusicProcessor;

public class ArenaMatching extends MainState {
    private static enum MatchingState {
        JOINING, SHOWING_PLAYERS
    };

    private PreviewMusicProcessor preview;
    private boolean cancel;
    private boolean playerAllowSkip;
    private long prevJoinTimeMillis;
    private long waitUntilMillis;
    private BitmapFont titleFont;
    private MatchingState matchingState;

    private void backToNonArenaMode() {
        ArenaUtils.close();
        MQUtils.close();
        resource.clearArenaData();
    }

    public ArenaMatching(final MainController main) {
        super(main);
    }

    @Override
    public void create() {
    	main.getSoundManager().shuffle();
    	preview = new PreviewMusicProcessor(main.getAudioProcessor(), resource.getConfig());
		preview.setDefault(getSound(SELECT));

        cancel = false;
        playerAllowSkip = false;
        prevJoinTimeMillis = 0;
        waitUntilMillis = 0;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("skin/default/VL-Gothic-Regular.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 36;
        parameter.characters = FontUtils.CHARACTERS;
        titleFont = generator.generateFont(parameter);

        backToNonArenaMode();
        matchingState = MatchingState.JOINING;
    }

    @Override
    public void prepare() {
        super.prepare();
        preview.start(null);
    }

    @Override
    public void render() {
        if (cancel) {
            backToNonArenaMode();
            main.changeState(MainStateType.MUSICSELECT);
            return;
        }

        final long nowTimeMillis = System.currentTimeMillis();

        if (waitUntilMillis > 0 && nowTimeMillis > waitUntilMillis) {
            main.changeState(MainStateType.MUSICSELECT);
            return;
        }

        {
            switch (matchingState) {
                case JOINING: 
                    if (prevJoinTimeMillis + Duration.ofSeconds(2).toMillis() < nowTimeMillis) { // 2 秒毎にリクエストを投げる事で、サーバー側で死活判定を行えるようにする
                        final ArenaRoom arenaRoom = ArenaUtils.joinArena(main.getPlayerConfig().getMode(), ArenaConfig.INSTANCE.getPlayerID(), ArenaConfig.INSTANCE.getPlayerName(), ArenaConfig.INSTANCE.getArenaClass(), ArenaConfig.INSTANCE.getArenaClassNumber(), ArenaConfig.INSTANCE.getSkillClass(), playerAllowSkip);

                        if (arenaRoom != null) {
                            if (arenaRoom.getError() == null) {
                                main.getPlayerResource().getArenaData().setArena(true);
                                main.getPlayerResource().getArenaData().setMode(main.getPlayerConfig().getMode());
                                main.getPlayerResource().getArenaData().setArenaRoom(arenaRoom);

                                if (arenaRoom.isPlayerConfirmed()) {
                                    MQUtils.connectReqSocket();
                                    MQUtils.connectSubSocket();
                                    MQUtils.subscribe(arenaRoom.getId());
                                    MQUtils.subscribe(ArenaConfig.INSTANCE.getPlayerID());

                                    waitUntilMillis = nowTimeMillis + Duration.ofSeconds(3).toMillis();
                                    matchingState = MatchingState.SHOWING_PLAYERS;

                                    main.getMessageRenderer().addMessage(String.format("Please select music(%s)", main.getPlayerConfig().getMode().name()), 6000, Color.GOLD, 0);
                                    main.getMessageRenderer().addMessage("Players have been confirmed.", 6000, Color.GOLD, 0);
                                }
                            } else {
                                Logger.getGlobal().log(Level.WARNING, arenaRoom.getError());
                                main.getMessageRenderer().addMessage(arenaRoom.getError(), 2000, Color.RED, 0);
                            }
                        }

                        prevJoinTimeMillis = nowTimeMillis;
                    }
                    
                    break;
                case SHOWING_PLAYERS: 
                    break;
            }
        }

        {
            String suffix = "";

            switch ((int) (nowTimeMillis / 500 % 10 % 5)) {
            case 0:
                suffix = "";
                break;
            case 1:
                suffix = ".";
                break;
            case 2:
                suffix = "..";
                break;
            case 3:
                suffix = "...";
                break;
            case 4:
                suffix = "....";
                break;
            }

            if (titleFont != null) {
                final String prefix = ((matchingState == MatchingState.JOINING) ? "Matching" : "Matched") + '(' + main.getPlayerConfig().getMode().name() + ')';

                main.getSpriteBatch().begin();
                titleFont.setColor(Color.GOLD);
                titleFont.draw(main.getSpriteBatch(), prefix + suffix, 10,
                        main.getConfig().getResolution().height - 100);
                titleFont.draw(main.getSpriteBatch(), ArenaConfig.INSTANCE.getArenaClass() + " " + ArenaConfig.INSTANCE.getSkillClass() + " " + ArenaConfig.INSTANCE.getPlayerName() + " vs.", 10,
                        main.getConfig().getResolution().height - 160);
                final ArenaRoom arenaRoom = main.getPlayerResource().getArenaData().getArenaRoom();
                int y = main.getConfig().getResolution().height - 200;
                if (arenaRoom != null && arenaRoom.getError() == null) {
                    if (arenaRoom.getPlayerName1() != null && !ArenaConfig.INSTANCE.getPlayerID().equals(arenaRoom.getPlayerID1())) {
                        titleFont.draw(main.getSpriteBatch(), arenaRoom.getPlayerArenaClass1() + " " + arenaRoom.getPlayerSkillClass1() + " " + arenaRoom.getPlayerName1(), 10, y);
                        y -= 40;
                    }
                    if (arenaRoom.getPlayerName2() != null && !ArenaConfig.INSTANCE.getPlayerID().equals(arenaRoom.getPlayerID2())) {
                        titleFont.draw(main.getSpriteBatch(), arenaRoom.getPlayerArenaClass2() + " " + arenaRoom.getPlayerSkillClass2() + " " + arenaRoom.getPlayerName2(), 10, y);
                        y -= 40;
                    }
                    if (arenaRoom.getPlayerName3() != null && !ArenaConfig.INSTANCE.getPlayerID().equals(arenaRoom.getPlayerID3())) {
                        titleFont.draw(main.getSpriteBatch(), arenaRoom.getPlayerArenaClass3() + " " + arenaRoom.getPlayerSkillClass3() + " " + arenaRoom.getPlayerName3(), 10, y);
                        y -= 40;
                    }
                    if (arenaRoom.getPlayerName4() != null && !ArenaConfig.INSTANCE.getPlayerID().equals(arenaRoom.getPlayerID4())) {
                        titleFont.draw(main.getSpriteBatch(), arenaRoom.getPlayerArenaClass4() + " " + arenaRoom.getPlayerSkillClass4() + " " + arenaRoom.getPlayerName4(), 10, y);
                        y -= 40;
                    }
                }
                if (playerAllowSkip) {
                    titleFont.draw(main.getSpriteBatch(), "Press ENTER to wait until all 4 players are matched", 10,
                            main.getConfig().getResolution().height - 440);
                } else {
                    titleFont.draw(main.getSpriteBatch(), "Press ENTER to skip matching", 10,
                            main.getConfig().getResolution().height - 440);
                }
                main.getSpriteBatch().end();
            }
        }
    }

    @Override
    public void input() {
        BMSPlayerInputProcessor input = main.getInputProcessor();

        if (input.isControlKeyPressed(ControlKeys.ESCAPE) || (input.startPressed() && input.isSelectPressed())) {
            cancel = true;
        }
        if (input.isControlKeyPressed(ControlKeys.ENTER)) {
            playerAllowSkip = !playerAllowSkip;
        }
    }

    @Override
    public void shutdown() {
    	preview.stop();
    	super.shutdown();
    }

    @Override
    public void dispose() {
        super.dispose();

        if (titleFont != null) {
            titleFont.dispose();
            titleFont = null;
        }
    }
}

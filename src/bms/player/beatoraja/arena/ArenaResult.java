package bms.player.beatoraja.arena;

import bms.player.beatoraja.PlayerResource;

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

import java.time.Duration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ArenaResult extends MainState {
    private boolean cancel;
    private PlayerResource.ArenaData arenaData;
    private BitmapFont titleFont, titleFont2;
    private long createdTimeMillis;
    private long prevUpdateTimeMillis;
    private List<ArenaMatchResult> arenaMatchResult;
    private List<ArenaMatchEachResult> arenaMatchEachResult;

    public ArenaResult(final MainController main) {
        super(main);
    }

    @Override
    public void create() {
        cancel = false;
        arenaData = resource.getArenaData();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("skin/default/VL-Gothic-Regular.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 36;
        parameter.characters = FontUtils.CHARACTERS;
        titleFont = generator.generateFont(parameter);
        parameter.size = 28;
        titleFont2 = generator.generateFont(parameter);
        createdTimeMillis = System.currentTimeMillis();
        prevUpdateTimeMillis = System.currentTimeMillis();
        arenaMatchResult = ArenaMatchResult.calcResults(arenaData.getArenaRoom(), arenaData.getOrderOfSongs());
        arenaMatchEachResult = ArenaMatchEachResult.calcResults(arenaData.getArenaRoom(), arenaData.getOrderOfSongs(), main.getSongDatabase());

        ArenaUtils.close();
        MQUtils.close();
        resource.clearArenaData();
    }

    @Override
    public void prepare() {
        super.prepare();
    }

    @Override
    public void render() {
        if (cancel) {
            main.changeState(MainStateType.MUSICSELECT);
            return;
        }

        final long nowTimeMillis = System.currentTimeMillis();

        if (createdTimeMillis + Duration.ofSeconds(30).toMillis() < nowTimeMillis) {
            main.changeState(MainStateType.MUSICSELECT);
        }

        if (prevUpdateTimeMillis + Duration.ofSeconds(3).toMillis() < nowTimeMillis) {
            final ArenaRoom arenaRoom = ArenaUtils.updateLastUpdate(arenaData.getArenaRoom().getId(), ArenaConfig.INSTANCE.getPlayerID());

            if (arenaRoom != null) {
                if (arenaRoom.getError() == null) {
                    arenaData.setArenaRoom(arenaRoom);
                    arenaMatchResult = ArenaMatchResult.calcResults(arenaData.getArenaRoom(), arenaData.getOrderOfSongs());
                    arenaMatchEachResult = ArenaMatchEachResult.calcResults(arenaData.getArenaRoom(), arenaData.getOrderOfSongs(), main.getSongDatabase());
                } else {
                    Logger.getGlobal().log(Level.WARNING, arenaRoom.getError());
                    main.getMessageRenderer().addMessage(arenaRoom.getError(), 2000, Color.RED, 0);
                }
            }

            prevUpdateTimeMillis = nowTimeMillis;
        }

        if (arenaMatchResult != null && !arenaMatchResult.isEmpty() && arenaMatchEachResult != null && !arenaMatchEachResult.isEmpty()) {
            main.getSpriteBatch().begin();
            titleFont.setColor(Color.GOLD);

            int y = main.getConfig().getResolution().height - 24;

            for (int i = 0; i < arenaMatchResult.size(); ++i) {
                final ArenaMatchResult result = arenaMatchResult.get(i);

                titleFont.draw(main.getSpriteBatch(), String.format("%d. %s %s %s %dpt EXScore=%d", i + 1, result.getArenaClass(), result.getSkillClass(), result.getPlayerName(), result.getPt(), result.getEXScore()), 10, y);
                y -= 40;
            }

            y -= 20;

            for (final ArenaMatchEachResult result : arenaMatchEachResult) {
                titleFont2.draw(main.getSpriteBatch(), result.getSong(), 10, y);
                y -= 30;

                final List<ArenaMatchResult> results = result.getResults();

                if (results != null && !results.isEmpty()) {
                    for (int i = 0; i < results.size(); ++i) {
                        final ArenaMatchResult result2 = results.get(i);

                        titleFont2.draw(main.getSpriteBatch(), String.format("%d. %s %s %s %dpt EXScore=%d", i + 1, result2.getArenaClass(), result2.getSkillClass(), result2.getPlayerName(), result2.getPt(), result2.getEXScore()), 10, y);
                        y -= 30;
                    }
                }
                y -= 20;
            }

            main.getSpriteBatch().end();
        }
    }

    @Override
    public void input() {
        BMSPlayerInputProcessor input = main.getInputProcessor();

        if (input.isControlKeyPressed(ControlKeys.ESCAPE) || input.isControlKeyPressed(ControlKeys.ENTER) || (input.startPressed() && input.isSelectPressed())) {
            cancel = true;
        }
    }

    @Override
    public void dispose() {
        super.dispose();

        if (titleFont2 != null) {
            titleFont2.dispose();
            titleFont2 = null;
        }
        if (titleFont != null) {
            titleFont.dispose();
            titleFont = null;
        }
    }
}

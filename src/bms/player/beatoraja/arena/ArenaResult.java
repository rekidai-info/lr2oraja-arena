package bms.player.beatoraja.arena;

import bms.player.beatoraja.PlayerResource;

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
    private BitmapFont titleFont;
    private long createdTimeMillis;
    private long prevUpdateTimeMillis;
    private List<ArenaMatchResult> arenaMatchResult;

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
        parameter.size = 28;
        titleFont = generator.generateFont(parameter);
        createdTimeMillis = System.currentTimeMillis();
        prevUpdateTimeMillis = System.currentTimeMillis();
        arenaMatchResult = ArenaMatchResult.from(arenaData.getArenaRoom());

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
                    arenaMatchResult = ArenaMatchResult.from(arenaData.getArenaRoom());
                } else {
                    Logger.getGlobal().log(Level.WARNING, arenaRoom.getError());
                    main.getMessageRenderer().addMessage(arenaRoom.getError(), 2000, Color.RED, 0);
                }
            }

            prevUpdateTimeMillis = nowTimeMillis;
        }

        if (arenaMatchResult != null && !arenaMatchResult.isEmpty()) {
            main.getSpriteBatch().begin();
            titleFont.setColor(Color.GOLD);

            for (int i = 0; i < arenaMatchResult.size(); ++i) {
                final ArenaMatchResult result = arenaMatchResult.get(i);

                titleFont.draw(main.getSpriteBatch(), String.format("%d. %s %s %dpt exscore=%d", i + 1, result.getArenaClass(), result.getPlayerName(), result.getPt(), result.getEXScore()), 10, main.getConfig().getResolution().height - 24 - i * 22);
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

        if (titleFont != null) {
            titleFont.dispose();
            titleFont = null;
        }
    }
}

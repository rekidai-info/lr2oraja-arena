package bms.player.beatoraja.select;

import static bms.player.beatoraja.skin.SkinProperty.*;
import static bms.player.beatoraja.SystemSoundManager.SoundType.*;

import java.nio.file.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.zeromq.ZMQ;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.*;

import bms.model.Mode;
import bms.player.beatoraja.*;
import bms.player.beatoraja.Config.SongPreview;
import bms.player.beatoraja.ScoreDatabaseAccessor.ScoreDataCollector;
import bms.player.beatoraja.arena.*;
import bms.player.beatoraja.input.BMSPlayerInputProcessor;
import bms.player.beatoraja.input.KeyCommand;
import bms.player.beatoraja.input.KeyBoardInputProcesseor.ControlKeys;
import bms.player.beatoraja.ir.*;
import bms.player.beatoraja.pattern.Random;
import bms.player.beatoraja.select.bar.*;
import bms.player.beatoraja.skin.SkinType;
import bms.player.beatoraja.song.SongData;
import bms.player.beatoraja.song.SongDatabaseAccessor;

/**
 * 選曲部分。 楽曲一覧とカーソルが指す楽曲のステータスを表示し、選択した楽曲を 曲決定部分に渡す。
 *
 * @author exch
 */
public class MusicSelector extends MainState {

	// TODO　ミラーランダム段位のスコア表示

	private int selectedreplay;

	/**
	 * 楽曲DBアクセサ
	 */
	private SongDatabaseAccessor songdb;

	public static final Mode[] MODE = { null, Mode.BEAT_7K, Mode.BEAT_14K, Mode.POPN_9K, Mode.BEAT_5K, Mode.BEAT_10K, Mode.KEYBOARD_24K, Mode.KEYBOARD_24K_DOUBLE };

	/**
	 * 保存可能な最大リプレイ数
	 */
	public static final int REPLAY = 4;

	private PlayerConfig config;

	/**
	 * 楽曲プレビュー処理
	 */
	private PreviewMusicProcessor preview;

	/**
	 * 楽曲バー描画用
	 */
	private BarRenderer bar;
	
	private final BarManager manager = new BarManager(this);
	
	private MusicSelectInputProcessor musicinput;

	private SearchTextField search;

	/**
	 * 楽曲が選択されてからbmsを読み込むまでの時間(ms)
	 */
	private final int notesGraphDuration = 350;
	/**
	 * 楽曲が選択されてからプレビュー曲を再生するまでの時間(ms)
	 */
	private final int previewDuration = 400;
	
	private final int rankingDuration = 5000;
	private final int rankingReloadDuration = 10 * 60 * 1000;
	
	private long currentRankingDuration = -1;

	private boolean showNoteGraph = false;

	private ScoreDataCache scorecache;
	private ScoreDataCache rivalcache;
	
	private RankingData currentir;
	/**
	 * ランキング表示位置
	 */
	protected int rankingOffset = 0;

	private PlayerInformation rival;
	
	private int panelstate;

	private BMSPlayerMode play = null;

	private SongData playedsong = null;
	private CourseData playedcourse = null;

	private PixmapResourcePool banners;

	private PixmapResourcePool stagefiles;
	
	private static enum SelectState {
		SELECTING, SELECTED
	};
	private SelectState selectState;
	private long createdTimeMillis;
	private boolean decidedMusic;
	private int decideMusicCallCount;
	private long prevSelectMusicTimeMillis;

	private void tryChangeStateToDecide(final SongData songData) {
		if (resource.getArenaData().isArena()) {
			if (selectState != SelectState.SELECTING || playedsong != null) {
				return;
			}

			decidedMusic = true;
			decideMusicCallCount = 0;
			playedsong = songData;

			final ArenaRoom arenaRoom = decideMusic();

			if (arenaRoom != null) {
				if (arenaRoom.getError() == null) {
					resource.getArenaData().setArenaRoom(arenaRoom);
				} else {
					Logger.getGlobal().log(Level.WARNING, arenaRoom.getError());
					main.getMessageRenderer().addMessage(arenaRoom.getError(), 2000, Color.RED, 0);
				}
			}

			main.getMessageRenderer().addMessage("Checking if the selected song is available...",1900, Color.GOLD,0);
		} else {
			main.changeState(MainStateType.DECIDE);
		}
	}

	private ArenaRoom decideMusic() {
		final ArenaRoom arenaRoom = resource.getArenaData().getArenaRoom();
		boolean songAvailable1 = false, songAvailable2 = false, songAvailable3 = false, songAvailable4 = false;

		if (arenaRoom.getSongHash1() != null) {
			if (ArenaConfig.INSTANCE.getPlayerID().equals(arenaRoom.getPlayerID1())) {
				songAvailable1 = true;
			} else {
				songAvailable1 = songdb.getSongDatas(new String[]{arenaRoom.getSongHash1()}).length > 0;
			}
		}
		if (arenaRoom.getSongHash2() != null) {
			if (ArenaConfig.INSTANCE.getPlayerID().equals(arenaRoom.getPlayerID2())) {
				songAvailable2 = true;
			} else {
				songAvailable2 = songdb.getSongDatas(new String[]{arenaRoom.getSongHash2()}).length > 0;
			}
		}
		if (arenaRoom.getSongHash3() != null) {
			if (ArenaConfig.INSTANCE.getPlayerID().equals(arenaRoom.getPlayerID3())) {
				songAvailable3 = true;
			} else {
				songAvailable3 = songdb.getSongDatas(new String[]{arenaRoom.getSongHash3()}).length > 0;
			}
		}
		if (arenaRoom.getSongHash4() != null) {
			if (ArenaConfig.INSTANCE.getPlayerID().equals(arenaRoom.getPlayerID4())) {
				songAvailable4 = true;
			} else {
				songAvailable4 = songdb.getSongDatas(new String[]{arenaRoom.getSongHash4()}).length > 0;
			}
		}

		return ArenaUtils.decideMusic(arenaRoom.getId(), ArenaConfig.INSTANCE.getPlayerID(), !decidedMusic || playedsong == null ? null : playedsong.getSha256(), songAvailable1, songAvailable2, songAvailable3, songAvailable4);
	}

	private SongData getNextSongData() {
		final String hash = resource.getArenaData().getNextSong();
		final SongData[] songData = songdb.getSongDatas(new String[] { hash });

		if (songData == null || songData.length < 1) {
			return null;
		} else {
			return songData[0];
		}
	}

	private void decideSong(final SongData songData) {
		if (songData == null) {
			main.getMessageRenderer().addMessage("Failed to loading BMS : Song not found, or Song has error. Arena mode has been closed.", 1900, Color.RED, 0);
			backToNonArenaMode();
		} else {
			Logger.getGlobal().info((resource.getArenaData().getOrderOfSongs() + 1) + " 曲目の BMS ファイルを読み込みます");
			selectSong(BMSPlayerMode.PLAY);
			playedsong = songData;
			Logger.getGlobal().info("アリーナ " + (resource.getArenaData().getOrderOfSongs() + 1) + " 曲目：" + playedsong.getFullTitle());
			resource.clear();
			if (resource.setBMSFile(Paths.get(playedsong.getPath()), play)) {
				try {
					final boolean isDouble = config.getMode() != null && config.getMode().player == 2;
					String option;

					if (isDouble) {
						option = Random.getRandom(config.getRandom()).name() + "/" + Random.getRandom(config.getRandom2()).name();
						if (config.getDoubleoption() == 1) {
							option += "/FLIP";
						} else {
							option += "/-";
						}
					} else {
						option = Random.getRandom(config.getRandom()).name();
					}
					
					if (option == null || option.isBlank()) {
						option = "-";
					}

					final ArenaRoom arenaRoom = ArenaUtils.updateOption(resource.getArenaData().getArenaRoom().getId(), ArenaConfig.INSTANCE.getPlayerID(), option);

					if (arenaRoom != null) {
						if (arenaRoom.getError() == null) {
							resource.getArenaData().setArenaRoom(arenaRoom);
						} else {
							Logger.getGlobal().log(Level.WARNING, arenaRoom.getError());
							main.getMessageRenderer().addMessage(arenaRoom.getError(), 2000, Color.RED, 0);
						}
					}
				} catch (final Exception e) {
					Logger.getGlobal().log(Level.WARNING, e.getLocalizedMessage());
					main.getMessageRenderer().addMessage(e.getLocalizedMessage(), 2000, Color.RED, 0);
				}

				Logger.getGlobal().info("決定画面に遷移します");
				main.changeState(MainStateType.DECIDE);
				selectState = SelectState.SELECTING;
				decidedMusic = false;
				decideMusicCallCount = 0;
				prevSelectMusicTimeMillis = 0;
			} else {
				main.getMessageRenderer().addMessage("Failed to loading BMS : Song not found, or Song has error. Arena mode has been closed.", 1900, Color.RED, 0);
				backToNonArenaMode();
			}
		}
	}
	
	private void notifyNextSong() {
		main.getMessageRenderer().addMessage("by " + resource.getArenaData().getNextPlayer(), 14000, Color.GOLD, 0);
		main.getMessageRenderer().addMessage("Next: " + playedsong.getDisplayString(), 14000, Color.GOLD, 0);
		main.getMessageRenderer().addMessage("Change play options within 15 seconds", 14000, Color.GOLD, 0);
	}
	
	private void notifyNewOpponent(final NewOpponent newOpponent) {
		if (newOpponent == null) {
			return;
		}

		if (!ArenaConfig.INSTANCE.getPlayerID().equals(newOpponent.getPlayerID())) {
			if (config.getMode() != null && config.getMode().name().equals(newOpponent.getPlayMode())) {
				if (Math.abs(ArenaConfig.INSTANCE.getArenaClassNumber() - newOpponent.getArenaClassNumber()) <= 5) {
					main.getMessageRenderer().addMessage(String.format("%s(%s %s) is now arena matching.", newOpponent.getPlayerName(), newOpponent.getPlayerArenaClass(), newOpponent.getPlayerSkillClass()), 3000, Color.GOLD, 0);
				}
			}
		}
	}

	private void notifyNewOpponents(final List<NewOpponent> newOpponents) {
		if (newOpponents == null || newOpponents.isEmpty()) {
			return;
		}
		
		for (final NewOpponent newOpponent : newOpponents) {
			try {
				notifyNewOpponent(newOpponent);
			} catch (final Exception ignore) {
			}
		}
	}

	private void backToNonArenaMode() {
		selectState = SelectState.SELECTING;
		createdTimeMillis = 0;
		decidedMusic = false;
		decideMusicCallCount = 0;
		prevSelectMusicTimeMillis = 0;
		ArenaUtils.close();
		MQUtils.close();
		resource.clearArenaData();

		MQUtils.connectSubSocket();
		MQUtils.subscribe("newopponent");

		main.changeState(MainStateType.MUSICSELECT);
	}

	public MusicSelector(MainController main, boolean songUpdated) {
		super(main);
		this.config = main.getPlayerResource().getPlayerConfig();

		songdb = main.getSongDatabase();

		final PlayDataAccessor pda = main.getPlayDataAccessor();

		scorecache = new ScoreDataCache() {
			@Override
			protected ScoreData readScoreDatasFromSource(SongData song, int lnmode) {
				return pda.readScoreData(song.getSha256(), song.hasUndefinedLongNote(), lnmode);
			}

			@Override
			protected void readScoreDatasFromSource(ScoreDataCollector collector, SongData[] songs, int lnmode) {
				pda.readScoreDatas(collector, songs, lnmode);
			}
		};
		
		bar = new BarRenderer(this, manager);
		banners = new PixmapResourcePool(resource.getConfig().getBannerPixmapGen());
		stagefiles = new PixmapResourcePool(resource.getConfig().getStagefilePixmapGen());
		musicinput = new MusicSelectInputProcessor(this);

		if (!songUpdated && main.getPlayerResource().getConfig().isUpdatesong()) {
			main.updateSong(null);
		}
	}
	
	public void setRival(PlayerInformation rival) {
		final RivalDataAccessor rivals = main.getRivalDataAccessor();
		final int index = IntStream.range(0, rivals.getRivalCount()).filter(i -> rival == rivals.getRivalInformation(i)).findFirst().orElse(-1);
		this.rival = index != -1 ? rivals.getRivalInformation(index) : null;
		rivalcache = index != -1 ? rivals.getRivalScoreDataCache(index) : null;
		manager.updateBar();
		Logger.getGlobal().info("Rival変更:" + (rival != null ? rival.getName() : "なし"));
	}

	public PlayerInformation getRival() {
		return rival;
	}

	public ScoreDataCache getScoreDataCache() {
		return scorecache;
	}

	public ScoreDataCache getRivalScoreDataCache() {
		return rivalcache;
	}

	public void create() {
		main.getSoundManager().shuffle();

		play = null;
		showNoteGraph = false;
		resource.setPlayerData(main.getPlayDataAccessor().readPlayerData());
		if (playedsong != null) {
			scorecache.update(playedsong, config.getLnmode());
			playedsong = null;
		}
		if (playedcourse != null) {
			for (SongData sd : playedcourse.getSong()) {
				scorecache.update(sd, config.getLnmode());
			}
			playedcourse = null;
		}

		preview = new PreviewMusicProcessor(main.getAudioProcessor(), resource.getConfig());
		preview.setDefault(getSound(SELECT));

		final BMSPlayerInputProcessor input = main.getInputProcessor();
		PlayModeConfig pc = (config.getMusicselectinput() == 0 ? config.getMode7()
				: (config.getMusicselectinput() == 1 ? config.getMode9() : config.getMode14()));
		input.setKeyboardConfig(pc.getKeyboardConfig());
		input.setControllerConfig(pc.getController());
		input.setMidiConfig(pc.getMidiConfig());
		manager.updateBar();

		loadSkin(SkinType.MUSIC_SELECT);

		// search text field
		Rectangle searchRegion = ((MusicSelectSkin) getSkin()).getSearchTextRegion();
		if (searchRegion != null && (getStage() == null ||
				(search != null && !searchRegion.equals(search.getSearchBounds())))) {
			if(search != null) {
				search.dispose();
			}
			search = new SearchTextField(this, resource.getConfig().getResolution());
			setStage(search);
		}

		selectState = SelectState.SELECTING;
		createdTimeMillis = System.currentTimeMillis();
		decidedMusic = false;
		decideMusicCallCount = 0;
		prevSelectMusicTimeMillis = 0;
		if (resource.getArenaData().isArena()) {
			final PlayerResource.ArenaData arenaData = resource.getArenaData();

			arenaData.setOrderOfSongs(arenaData.getOrderOfSongs() + 1);
			if (arenaData.getOrderOfSongs() > 0) {
				selectState = SelectState.SELECTED;
				playedsong = getNextSongData();

				notifyNextSong();
			}
		} else {
			MQUtils.close();
			MQUtils.connectSubSocket();
			MQUtils.subscribe("newopponent");

			notifyNewOpponents(ArenaUtils.waitingPlayers());
		}
	}

	public void prepare() {
		preview.start(null);
	}

	public void render() {
		if (resource.getArenaData().isArena()) {
			if (selectState == SelectState.SELECTING){
				final long nowTimeMillis = System.currentTimeMillis();

				if (prevSelectMusicTimeMillis + Duration.ofSeconds(2).toMillis() < nowTimeMillis) {
					final ArenaRoom arenaRoom = decideMusic();

					if (arenaRoom != null) {
						if (arenaRoom.getError() == null) {
							if (decidedMusic) {
								++decideMusicCallCount;
							}
							if (arenaRoom.getPlayerCount() <= 1) {
								main.getMessageRenderer().addMessage("Your opponent has disconnected. Arena mode has been closed.", 1900, Color.RED, 0);
								backToNonArenaMode();
							} else if (decidedMusic && arenaRoom.isSong1Available() > 0 && arenaRoom.isSong2Available() > 0 && arenaRoom.isSong3Available() > 0 && arenaRoom.isSong4Available() > 0) {
								if (decideMusicCallCount >= 4) {
									main.getMessageRenderer().addMessage("All players have completed their song selection", 1900, Color.GOLD, 0);
									resource.getArenaData().setPlayerNames(new ArrayList<String>(Arrays.asList(new String[]{arenaRoom.getPlayerName1(), arenaRoom.getPlayerName2(), arenaRoom.getPlayerName3(), arenaRoom.getPlayerName4()})));
									resource.getArenaData().setSongHashes(new ArrayList<String>(Arrays.asList(new String[]{arenaRoom.getSongHash1(), arenaRoom.getSongHash2(), arenaRoom.getSongHash3(), arenaRoom.getSongHash4()})));

									playedsong = getNextSongData();
									createdTimeMillis = System.currentTimeMillis();
									selectState = SelectState.SELECTED;

									notifyNextSong();
								}
							} else if (decidedMusic && ArenaConfig.INSTANCE.getPlayerID().equals(arenaRoom.getPlayerID1()) && arenaRoom.isSong1Available() == 0 ||
									ArenaConfig.INSTANCE.getPlayerID().equals(arenaRoom.getPlayerID2()) && arenaRoom.isSong2Available() == 0 ||
									ArenaConfig.INSTANCE.getPlayerID().equals(arenaRoom.getPlayerID3()) && arenaRoom.isSong3Available() == 0 ||
									ArenaConfig.INSTANCE.getPlayerID().equals(arenaRoom.getPlayerID4()) && arenaRoom.isSong4Available() == 0) {
								if (decideMusicCallCount >= 4) {
									main.getMessageRenderer().addMessage("Selected song is NOT available", 1900, Color.RED, 0);
									decidedMusic = false;
									decideMusicCallCount = 0;
									playedsong = null;
								}
							} else if (decidedMusic && ArenaConfig.INSTANCE.getPlayerID().equals(arenaRoom.getPlayerID1()) && arenaRoom.isSong1Available() > 0 ||
									ArenaConfig.INSTANCE.getPlayerID().equals(arenaRoom.getPlayerID2()) && arenaRoom.isSong2Available() > 0 ||
									ArenaConfig.INSTANCE.getPlayerID().equals(arenaRoom.getPlayerID3()) && arenaRoom.isSong3Available() > 0 ||
									ArenaConfig.INSTANCE.getPlayerID().equals(arenaRoom.getPlayerID4()) && arenaRoom.isSong4Available() > 0) {
								if (decideMusicCallCount >= 4) {
									main.getMessageRenderer().addMessage("Selected song is available. Please wait until another player has selected a song.", 1900, Color.GOLD, 0);
								}
							}

							resource.getArenaData().setArenaRoom(arenaRoom);
						} else {
							Logger.getGlobal().log(Level.WARNING, arenaRoom.getError());
							main.getMessageRenderer().addMessage(arenaRoom.getError(), 2000, Color.RED, 0);
						}
					}

					prevSelectMusicTimeMillis = nowTimeMillis;
				}
			} else if (selectState == SelectState.SELECTED) {
				final long nowTimeMillis = System.currentTimeMillis();

				if (createdTimeMillis + Duration.ofSeconds(15).toMillis() < nowTimeMillis) {
					config.setLnmode(0);
					config.setScrollMode(0);
					config.setBpmguide(false);
					if (config.isCustomJudge() &&
							(config.getKeyJudgeWindowRatePerfectGreat() > 100 || config.getKeyJudgeWindowRateGreat() > 100 || config.getKeyJudgeWindowRateGood() > 100 ||
									config.getScratchJudgeWindowRatePerfectGreat() > 100 || config.getScratchJudgeWindowRateGreat() > 100 || config.getScratchJudgeWindowRateGood() > 100)) {
						config.setCustomJudge(false);
					}
					decideSong(playedsong);
				}
			} else {
				throw new RuntimeException("Invalid select state(" + selectState + ")");
			}
		} else {
			try {
				String received = MQUtils.subRecvStr(ZMQ.DONTWAIT);

				while (received != null) {
					final NewOpponent newOpponent = NewOpponent.fromJson(received);

					notifyNewOpponent(newOpponent);
					received = MQUtils.subRecvStr(ZMQ.DONTWAIT);
				}
			} catch (final Exception e) {
				Logger.getGlobal().log(Level.WARNING, e.getLocalizedMessage(), e);
			}
		}

		if (getSkin() == null) {
			return;
		}

		final Bar current = manager.getSelected();
        if(timer.getNowTime() > getSkin().getInput()){
        	timer.switchTimer(TIMER_STARTINPUT, true);
        }
		if(timer.getNowTime(TIMER_SONGBAR_CHANGE) < 0) {
			timer.setTimerOn(TIMER_SONGBAR_CHANGE);
		}
		// draw song information
		resource.setSongdata(current instanceof SongBar ? ((SongBar) current).getSongData() : null);
		resource.setCourseData(current instanceof GradeBar ? ((GradeBar) current).getCourseData() : null);

		// preview music
		if (current instanceof SongBar && resource.getConfig().getSongPreview() != SongPreview.NONE) {
			final SongData song = resource.getSongdata();
			if (song != preview.getSongData() && timer.getNowTime() > timer.getTimer(TIMER_SONGBAR_CHANGE) + previewDuration
					&& play == null) {
				this.preview.start(song);
			}
		}

		// read bms information
		if (timer.getNowTime() > timer.getTimer(TIMER_SONGBAR_CHANGE) + notesGraphDuration && !showNoteGraph && play == null) {
			if (current instanceof SongBar && ((SongBar) current).existsSong()) {
				SongData song = resource.getSongdata();
				new Thread(() ->  {
					song.setBMSModel(resource.loadBMSModel(Paths.get(((SongBar) current).getSongData().getPath()),
							config.getLnmode()));
				}).start();;
			}
			showNoteGraph = true;
		}
		// get ir ranking
		if (currentRankingDuration != -1 && timer.getNowTime() > timer.getTimer(TIMER_SONGBAR_CHANGE) + currentRankingDuration) {
			currentRankingDuration = -1;
			if (current instanceof SongBar && ((SongBar) current).existsSong() && play == null) {
				SongData song = ((SongBar) current).getSongData();
				RankingData irc = main.getRankingDataCache().get(song, config.getLnmode());
				if(irc == null) {
					irc = new RankingData();
					main.getRankingDataCache().put(song, config.getLnmode(), irc);
				}
				irc.load(this, song);
	            currentir = irc;
			}				
			if (current instanceof GradeBar && ((GradeBar) current).existsAllSongs() && play == null) {
				CourseData course = ((GradeBar) current).getCourseData();
				RankingData irc = main.getRankingDataCache().get(course, config.getLnmode());
				if(irc == null) {
					irc = new RankingData();
					main.getRankingDataCache().put(course, config.getLnmode(), irc);
				}
				irc.load(this, course);
	            currentir = irc;
			}				
		}
		final int irstate = currentir != null ? currentir.getState() : -1;
		timer.switchTimer(TIMER_IR_CONNECT_BEGIN, irstate == RankingData.ACCESS);
		timer.switchTimer(TIMER_IR_CONNECT_SUCCESS, irstate == RankingData.FINISH);
		timer.switchTimer(TIMER_IR_CONNECT_FAIL, irstate == RankingData.FAIL);

		if (play != null) {
			if (current instanceof SongBar) {
				SongData song = ((SongBar) current).getSongData();
				if (((SongBar) current).existsSong()) {
					if (resource.getArenaData().isArena()) {
						tryChangeStateToDecide(song);
					} else {
						resource.clear();
						if (resource.setBMSFile(Paths.get(song.getPath()), play)) {
							// TODO 重複コード
							final Queue<DirectoryBar> dir = manager.getDirectory();
							if(dir.size > 0 && !(dir.last() instanceof SameFolderBar)) {
								Array<String> urls = new Array<String>(resource.getConfig().getTableURL());
	
								boolean isdtable = false;
								for (DirectoryBar bar : dir) {
									if (bar instanceof TableBar) {
										String currenturl = ((TableBar) bar).getUrl();
										if (currenturl != null && urls.contains(currenturl, false)) {
											isdtable = true;
											resource.setTablename(bar.getTitle());
										}
									}
									if (bar instanceof HashBar && isdtable) {
										resource.setTablelevel(bar.getTitle());
										break;
									}
								}
							}
							
							if(main.getIRStatus().length > 0 && currentir == null) {
								currentir = new RankingData();
								main.getRankingDataCache().put(song, config.getLnmode(), currentir);
							}
							resource.setRankingData(currentir);
							resource.setRivalScoreData(current.getRivalScore());
							
							playedsong = song;
							main.changeState(MainStateType.DECIDE);
						} else {
							main.getMessageRenderer().addMessage("Failed to loading BMS : Song not found, or Song has error", 1200, Color.RED, 1);
						}
					}
				} else {
	                execute(MusicSelectCommand.OPEN_DOWNLOAD_SITE);
				}
			} else if (current instanceof ExecutableBar) {
				if (resource.getArenaData().isArena()) {
					tryChangeStateToDecide(((ExecutableBar) current).getSongData());
				} else {
					SongData song = ((ExecutableBar) current).getSongData();
					resource.clear();
					if (resource.setBMSFile(Paths.get(song.getPath()), play)) {
						// TODO 重複コード
						final Queue<DirectoryBar> dir = manager.getDirectory();
						if(dir.size > 0 && !(dir.last() instanceof SameFolderBar)) {
							Array<String> urls = new Array<String>(resource.getConfig().getTableURL());
	
							boolean isdtable = false;
							for (DirectoryBar bar : dir) {
								if (bar instanceof TableBar) {
									String currenturl = ((TableBar) bar).getUrl();
									if (currenturl != null && urls.contains(currenturl, false)) {
										isdtable = true;
										resource.setTablename(bar.getTitle());
									}
								}
								if (bar instanceof HashBar && isdtable) {
									resource.setTablelevel(bar.getTitle());
									break;
								}
							}
						}
						
						playedsong = song;
						main.changeState(MainStateType.DECIDE);
					} else {
						main.getMessageRenderer().addMessage("Failed to loading BMS : Song not found, or Song has error", 1200, Color.RED, 1);
					}
				}
			}else if (current instanceof GradeBar) {
				if (!resource.getArenaData().isArena()) {
					if (play.mode == BMSPlayerMode.Mode.PRACTICE) {
						play = BMSPlayerMode.PLAY;
					}
					readCourse(play);
				}
			} else if (current instanceof RandomCourseBar) {
				if (!resource.getArenaData().isArena()) {
					if (play.mode == BMSPlayerMode.Mode.PRACTICE) {
						play = BMSPlayerMode.PLAY;
					}
					readRandomCourse(play);
				}
			} else if (current instanceof DirectoryBar) {
				if(!resource.getArenaData().isArena() && play.mode == BMSPlayerMode.Mode.AUTOPLAY) {
					final Path[] paths = Stream.of(((DirectoryBar) current).getChildren())
						.filter(bar -> (bar instanceof SongBar && ((SongBar) bar).getSongData() != null && ((SongBar) bar).getSongData().getPath() != null))
						.map(bar -> Paths.get(((SongBar) bar).getSongData().getPath())).toArray(Path[]::new);
					if(paths.length > 0) {
						resource.clear();
						resource.setAutoPlaySongs(paths, false);
						if(resource.nextSong()) {
							main.changeState(MainStateType.DECIDE);
						}
					}
				}
			}
			play = null;
		}
	}

	public void input() {
		final BMSPlayerInputProcessor input = main.getInputProcessor();

		if (!resource.getArenaData().isArena()) {
			if (input.getControlKeyState(ControlKeys.NUM6)) {
				main.changeState(MainStateType.CONFIG);
			} else if (input.isActivated(KeyCommand.OPEN_SKIN_CONFIGURATION)) {
				main.changeState(MainStateType.SKINCONFIG);
			}
		}

		musicinput.input();
	}

	public void shutdown() {
		preview.stop();
		if (search != null) {
			search.unfocus(this);
		}
		banners.disposeOld();
		stagefiles.disposeOld();
	}
	
	public void select(Bar current) {
		if (current instanceof DirectoryBar) {
			if (manager.updateBar(current)) {
				play(FOLDER_OPEN);
			}
			execute(MusicSelectCommand.RESET_REPLAY);
		} else {
			play = BMSPlayerMode.PLAY;
		}
	}

	public int getSelectedReplay() {
		return  selectedreplay;
	}

	public void setSelectedReplay(int index) {
		selectedreplay = index;
	}

	public void execute(MusicSelectCommand command) {
		command.function.accept(this);
	}

	private void readCourse(BMSPlayerMode mode) {
		final GradeBar gradeBar = (GradeBar) manager.getSelected();
		if (!gradeBar.existsAllSongs()) {
			Logger.getGlobal().info("段位の楽曲が揃っていません");
			return;
		}

		if (!_readCourse(mode, gradeBar)) {
			main.getMessageRenderer().addMessage("Failed to loading Course : Some of songs not found", 1200, Color.RED, 1);
			Logger.getGlobal().info("段位の楽曲が揃っていません");
		}
	}

	private void readRandomCourse(BMSPlayerMode mode) {
		final RandomCourseBar randomCourseBar = (RandomCourseBar) manager.getSelected();
		if (!randomCourseBar.existsAllSongs()) {
			Logger.getGlobal().info("ランダムコースの楽曲が揃っていません");
			return;
		}

		randomCourseBar.getCourseData().lotterySongDatas(main);
		final GradeBar gradeBar = new GradeBar(randomCourseBar.getCourseData().createCourseData());
		if (!gradeBar.existsAllSongs()) {
			main.getMessageRenderer().addMessage("Failed to loading Random Course : Some of songs not found", 1200, Color.RED, 1);
			Logger.getGlobal().info("ランダムコースの楽曲が揃っていません");
			return;
		}

		if (_readCourse(mode, gradeBar)) {
			manager.addRandomCourse(gradeBar, manager.getDirectoryString());
			manager.updateBar();
			manager.setSelected(gradeBar);
		} else {
			main.getMessageRenderer().addMessage("Failed to loading Random Course : Some of songs not found", 1200, Color.RED, 1);
			Logger.getGlobal().info("ランダムコースの楽曲が揃っていません");
		}
	}

	private boolean _readCourse(BMSPlayerMode mode, GradeBar gradeBar) {
		resource.clear();
		final SongData[] songs = gradeBar.getSongDatas();
		final Path[] files = Stream.of(songs).map(song -> Paths.get(song.getPath())).toArray(Path[]::new);
		if (resource.setCourseBMSFiles(files)) {
			if (mode.mode == BMSPlayerMode.Mode.PLAY || mode.mode == BMSPlayerMode.Mode.AUTOPLAY) {
				for (CourseData.CourseDataConstraint constraint : gradeBar.getCourseData().getConstraint()) {
					switch (constraint) {
						case CLASS:
							config.setRandom(0);
							config.setRandom2(0);
							config.setDoubleoption(0);
							break;
						case MIRROR:
							if (config.getRandom() == 1) {
								config.setRandom2(1);
								config.setDoubleoption(1);
							} else {
								config.setRandom(0);
								config.setRandom2(0);
								config.setDoubleoption(0);
							}
							break;
						case RANDOM:
							if (config.getRandom() > 5) {
								config.setRandom(0);
							}
							if (config.getRandom2() > 5) {
								config.setRandom2(0);
							}
							break;
						case LN:
							config.setLnmode(0);
							break;
						case CN:
							config.setLnmode(1);
							break;
						case HCN:
							config.setLnmode(2);
							break;
						default:
							break;
					}
				}
			}
			gradeBar.getCourseData().setSong(resource.getCourseBMSModels());
			resource.setCourseData(gradeBar.getCourseData());
			resource.setBMSFile(files[0], mode);
			playedcourse = gradeBar.getCourseData();

			if(main.getIRStatus().length > 0 && currentir == null) {
				currentir = new RankingData();
				main.getRankingDataCache().put(gradeBar.getCourseData(), config.getLnmode(), currentir);
			}
			
			RankingData songrank = main.getRankingDataCache().get(songs[0], config.getLnmode());
			if(main.getIRStatus().length > 0 && songrank == null) {
				songrank = new RankingData();
				main.getRankingDataCache().put(songs[0], config.getLnmode(), songrank);
			}
			resource.setRankingData(songrank);
			resource.setRivalScoreData(null);

			main.changeState(MainStateType.DECIDE);
			return true;
		}
		return false;
	}

	public int getSort() {
		return config.getSort();
	}

	public void setSort(int sort) {
		config.setSort(sort);
	}

	public void dispose() {
		super.dispose();
		bar.dispose();
		banners.dispose();
		stagefiles.dispose();
		if (search != null) {
			search.dispose();
			search = null;
		}
	}

	public int getPanelState() {
		return panelstate;
	}

	public void setPanelState(int panelstate) {
		if (this.panelstate != panelstate) {
			if (this.panelstate != 0) {
				timer.setTimerOn(TIMER_PANEL1_OFF + this.panelstate - 1);
				timer.setTimerOff(TIMER_PANEL1_ON + this.panelstate - 1);
			}
			if (panelstate != 0) {
				timer.setTimerOn(TIMER_PANEL1_ON + panelstate - 1);
				timer.setTimerOff(TIMER_PANEL1_OFF + panelstate - 1);
			}
		}
		this.panelstate = panelstate;
	}

	public SongDatabaseAccessor getSongDatabase() {
		return songdb;
	}

	public boolean existsConstraint(CourseData.CourseDataConstraint constraint) {
		CourseData.CourseDataConstraint[] cons;
		if ((manager.getSelected() instanceof GradeBar)) {
			cons = ((GradeBar) manager.getSelected()).getCourseData().getConstraint();
		} else if (manager.getSelected() instanceof RandomCourseBar) {
			cons = ((RandomCourseBar) manager.getSelected()).getCourseData().getConstraint();
		} else {
			return false;
		}

		for (CourseData.CourseDataConstraint con : cons) {
			if(con == constraint) {
				return true;
			}
		}
		return false;
	}

	public Bar getSelectedBar() {
		return manager.getSelected();
	}

	public BarRenderer getBarRender() {
		return bar;
	}

	public BarManager getBarManager() {
		return manager;
	}

	public PixmapResourcePool getBannerResource() {
		return banners;
	}
	public PixmapResourcePool getStagefileResource() {
		return stagefiles;
	}

	public void selectedBarMoved() {
		execute(MusicSelectCommand.RESET_REPLAY);
		loadSelectedSongImages();

		timer.setTimerOn(TIMER_SONGBAR_CHANGE);
		if(preview.getSongData() != null && (!(manager.getSelected() instanceof SongBar) ||
				((SongBar) manager.getSelected()).getSongData().getFolder().equals(preview.getSongData().getFolder()) == false))
		preview.start(null);
		showNoteGraph = false;

		final Bar current = manager.getSelected();
		if(main.getIRStatus().length > 0) {
			if(current instanceof SongBar && ((SongBar) current).existsSong()) {
				currentir = main.getRankingDataCache().get(((SongBar) current).getSongData(), config.getLnmode());
				currentRankingDuration = (currentir != null ? Math.max(rankingReloadDuration - (System.currentTimeMillis() - currentir.getLastUpdateTime()) ,0) : 0) + rankingDuration;
			} else if(current instanceof GradeBar && ((GradeBar) current).existsAllSongs()) {
				currentir = main.getRankingDataCache().get(((GradeBar) current).getCourseData(), config.getLnmode());
				currentRankingDuration = (currentir != null ? Math.max(rankingReloadDuration - (System.currentTimeMillis() - currentir.getLastUpdateTime()) ,0) : 0) + rankingDuration;
			} else {
				currentir = null;
				currentRankingDuration = -1;			
			}
		} else {
			currentir = null;
			currentRankingDuration = -1;			
		}
	}

	public void loadSelectedSongImages() {
		// banner
		// stagefile
		final Bar current = manager.getSelected();
		resource.getBMSResource().setBanner(
				current instanceof SongBar ? ((SongBar) current).getBanner() : null);
		resource.getBMSResource().setStagefile(
				current instanceof SongBar ? ((SongBar) current).getStagefile() : null);
	}

	public void selectSong(BMSPlayerMode mode) {
		play = mode;
	}

	public PlayConfig getSelectedBarPlayConfig() {
		Bar current = manager.getSelected();
		PlayConfig pc = null;
		if (current instanceof SongBar && ((SongBar)current).existsSong()) {
			SongBar song = (SongBar) current;
			pc = main.getPlayerConfig().getPlayConfig(song.getSongData().getMode()).getPlayconfig();
		} else if(current instanceof GradeBar && ((GradeBar)current).existsAllSongs()) {
			GradeBar grade = (GradeBar)current;
			for(SongData song : grade.getSongDatas()) {
				PlayConfig pc2 = main.getPlayerConfig().getPlayConfig(song.getMode()).getPlayconfig();
				if(pc == null) {
					pc = pc2;
				}
				if(pc != pc2) {
					pc = null;
					break;
				}
			}
		} else {
			pc = main.getPlayerConfig().getPlayConfig(config.getMode()).getPlayconfig();
		}
		return pc;
	}
	
	public RankingData getCurrentRankingData() {
		return currentir;
	}
	
	public long getCurrentRankingDuration() {
		return currentRankingDuration;
	}
	
	public int getRankingOffset() {
		return rankingOffset;
	}
	
	public float getRankingPosition() {
		final int rankingMax = currentir != null ? Math.max(1, currentir.getTotalPlayer()) : 1;
		return (float)rankingOffset / rankingMax;		
	}
	
	public void setRankingPosition(float value) {
		if (value >= 0 && value < 1) {
			final int rankingMax = currentir != null ? Math.max(1, currentir.getTotalPlayer()) : 1;
			rankingOffset = (int) (rankingMax * value);
		}
	}
}

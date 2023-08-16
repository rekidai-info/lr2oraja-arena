package bms.player.beatoraja.arena;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class MQUtils {
    private static final ExecutorService THREAD_POOL = Executors.newSingleThreadExecutor();
    private static final AtomicInteger REQ_COUNT = new AtomicInteger(0);
    private static final ZContext context = new ZContext(4);

    private static ZMQ.Socket reqSocket;
    private static ZMQ.Socket subSocket;
    
    public static void close() {
        closeReqSocket();
        closeSubSocket();
    }

    public static void shutdown() {
        THREAD_POOL.shutdown();
        close();
    }

    public static void closeReqSocket() {
        if (reqSocket != null) {
            reqSocket.close();
            reqSocket = null;
        }
    }

    public static void closeSubSocket() {
        if (subSocket != null) {
            subSocket.close();
            subSocket = null;
        }
    }

    public static void createReqSocket() {
        if (reqSocket != null) {
            closeReqSocket();
        }

        reqSocket = context.createSocket(SocketType.REQ);
    }

    public static void createSubSocket() {
        if (subSocket != null) {
            closeSubSocket();
        }

        subSocket = context.createSocket(SocketType.SUB);
    }

    public static boolean connectReqSocket() {
        if (reqSocket == null) {
            createReqSocket();
        }

        return reqSocket.connect(ArenaConfig.INSTANCE.getMQServerUrl() + ":5555");
    }

    public static boolean connectSubSocket() {
        if (subSocket == null) {
            createSubSocket();
        }

        return subSocket.connect(ArenaConfig.INSTANCE.getMQServerUrl() + ":5556");
    }

    public static boolean subscribe(final String topic) {
        if (subSocket == null) {
            connectSubSocket();
        }

        Logger.getGlobal().log(Level.INFO, "Subscribed: " + topic);

        return subSocket.subscribe(topic.getBytes(StandardCharsets.UTF_8));
    }

    public static String subRecvStr(final int flags) {
        if (subSocket == null) {
            return null;
        }

        return subSocket.recvStr(flags);
    }

    public static boolean reqSend(final String data, final int flags) {
        if (reqSocket == null) {
            createReqSocket();
        }

        Logger.getGlobal().log(Level.FINE, "REQ send: " + data);

        final boolean result = reqSocket.send(data, flags);
        
        if (result) {
            REQ_COUNT.incrementAndGet();
            THREAD_POOL.submit(() -> {
                while (!THREAD_POOL.isShutdown() && REQ_COUNT.get() > 0) {
                    try {
                        final String received = reqRecvStr(0);

                        REQ_COUNT.decrementAndGet();
                        Logger.getGlobal().log(Level.FINE, "REQ received: " + received);
                    } catch (final Exception e) {
                        Logger.getGlobal().log(Level.WARNING, "REQ cannot received: " + e.getLocalizedMessage(), e);
                    }
                }
            });
        }
        
        return result;
    }

    public static String reqRecvStr(final int flags) {
        if (reqSocket == null) {
            return null;
        }

        return reqSocket.recvStr(flags);
    }
    
    public static boolean sendScore(final ArenaRoom arenaRoom, final int exScore, int flags) {
        final List<String> targets = new ArrayList<String>();

        if (arenaRoom.getPlayerID1() != null && !arenaRoom.getPlayerID1().equals(ArenaConfig.INSTANCE.getPlayerID())) {
            targets.add(arenaRoom.getPlayerID1());
        }
        if (arenaRoom.getPlayerID2() != null && !arenaRoom.getPlayerID2().equals(ArenaConfig.INSTANCE.getPlayerID())) {
            targets.add(arenaRoom.getPlayerID2());
        }
        if (arenaRoom.getPlayerID3() != null && !arenaRoom.getPlayerID3().equals(ArenaConfig.INSTANCE.getPlayerID())) {
            targets.add(arenaRoom.getPlayerID3());
        }
        if (arenaRoom.getPlayerID4() != null && !arenaRoom.getPlayerID4().equals(ArenaConfig.INSTANCE.getPlayerID())) {
            targets.add(arenaRoom.getPlayerID4());
        }

        final SendScore sendScore = new SendScore();

        sendScore.setTarget(String.join(",", targets));
        sendScore.setPlayerID(ArenaConfig.INSTANCE.getPlayerID());
        sendScore.setExScore(exScore);

        return reqSend(sendScore.toJson(), flags);
    }
}

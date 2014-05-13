package com.yoshio3.websockets;

import com.yoshio3.websockets.encoders.JSONMessageEncoder;
import com.yoshio3.websockets.encoders.JSONWrappedObject;
import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import javax.inject.Inject;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author Yoshio Terada
 */
@ServerEndpoint(value = "/asyncResult", encoders = {JSONMessageEncoder.class})
public class AsyncResultEndpoint {

    private static final Logger logger
            = Logger.getLogger(AsyncResultEndpoint.class.getPackage().getName());

    @Inject
    ConcurrentTaskExecutor execTask;

    @OnOpen
    public void initOpen(Session session) {
        //ブロッキングを先に実行すると完了までブロックするため
        //ノンブロッキングを先に実行
        execTask.executeNonBlockingTasks2(session);
        execTask.executeBlockingTask(session);
    }

    @OnMessage
    public void receivedMessage(String message, Session session) {
        switch (message) {
            case "re-execute":
                execNonBlocking(session);
                execBlocking(session);
                break;
            case "blocking":
                execBlocking(session);
                break;
            case "nonblocking":
                execNonBlocking(session);
                break;
            default:
                break;
        }
    }

    private void execBlocking(Session session) {
        // Java SE 7 までの Future & Callable を利用したブロッキング・タスクを実行
        Instant start2 = Instant.now();
        IntStream.range(0, 5).forEach(i -> {
            execTask.executeBlockingTask(session);
        });
        //ブロッキングなので処理結果が帰ってくるまで継続処理は不可能
        Instant end2 = Instant.now();
        Duration time2 = Duration.between(start2, end2);
        //メッセージは全ての処理が完了した後に表示される
        execTask.sendMsgToClient(session, new JSONWrappedObject("error", -1, "BLOCKING 実行時間(ミリ秒) : " + time2.toMillis(), "se7"));
    }

    private void execNonBlocking(Session session) {
        // Java SE 8 の CompletableFuture を利用したノンブロッキング・タスクを実行
        Instant start1 = Instant.now();
        // 各タスクを 5 回づつ実行
        IntStream.range(0, 5).forEach(i -> {
            execTask.executeNonBlockingTasks2(session);
        });
        //ノンブロッキングなのですぐに次の処理を実行可能
        Instant end1 = Instant.now();
        Duration time1 = Duration.between(start1, end1);

        // メッセージは非同期のタスクが実行中に表示される
        execTask.sendMsgToClient(session,
                new JSONWrappedObject("error", -1, "NON BLOCKING 実行時間(ミリ秒) : " + time1.toMillis(), "se8"));

    }
}

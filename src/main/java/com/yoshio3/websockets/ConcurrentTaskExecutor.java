/*
 * Copyright 2014 Yoshio Terada
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yoshio3.websockets;

import com.yoshio3.websockets.encoders.JSONWrappedObject;
import com.yoshio3.websockets.tasks.MyCallableTask;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.Dependent;
import javax.websocket.Session;

/**
 *
 * @author Yoshio Terada
 */
@Dependent
public class ConcurrentTaskExecutor {

    private static final Logger logger
            = Logger.getLogger(ConcurrentTaskExecutor.class.getPackage().getName());

    @Resource
    ManagedExecutorService managedExecsvc;

    /*
     Java SE 7 + Java EE 7 までの並列処理用の実装コード例
     Callable を実装したクラスを並列処理タスクとして実行し
     実行結果を Future#getで取得
     get() はブロッキングするため、全ての get() が完了しないと
     継続処理は実行不可能
     */
    protected void executeBlockingTask(Session session) {

        sendMsgToClient(session, new JSONWrappedObject("info", -1, "ブロッキング処理開始", "se7"));
        // 並列処理を実行後、完了した順に結果を取り出すためのサービス
        ExecutorCompletionService<Integer> execCompService
                = new ExecutorCompletionService<>(managedExecsvc);

        // 実行結果を格納するリスト
        List<Future<Integer>> futures = new ArrayList<>();
        // 並列処理タスクを 10 個作成し実行
        for (int i = 0; i < 10; i++) {
            MyCallableTask task = new MyCallableTask(i);
            futures.add(execCompService.submit(task));
        }
        // SE8 では Stream に置き換えられるが SE 7 のサンプルの為あえて拡張 for 文を使用
        for (Future<Integer> results : futures) {
            try {
                // 処理結果順に結果を取得
                Integer result = execCompService.take().get();
                // クライアント・エンドポイントに結果配信
                sendMsgToClient(session, new JSONWrappedObject("success", result, "none", "se7"));
            } catch (InterruptedException | ExecutionException ex) {
                sendMsgToClient(session, new JSONWrappedObject("error", -1, "エラー発生：" + ex.getMessage(), "se7"));
            }
        }

        sendMsgToClient(session, new JSONWrappedObject("info", -1, "ブロッキング処理完了", "se7"));
    }

    /*
     Java SE 8 + Java EE 7 の並列処理用の実装コード例
     CompletableFuture を使用すると、処理結果をパイプラインで
     つなぎ、並列処理の実行結果をノンブロッキングで処理可能
     */
    protected void executeNonBlockingTasks(Session session) {
        sendMsgToClient(session, new JSONWrappedObject("info", -1, "ノンブロッキング処理開始", "se8"));

        IntStream.range(0, 10).forEach(i -> {
            // final int data = i;
            CompletableFuture<Void> tasks = CompletableFuture
                    // 非同期のノンブロッキングタスクを実行
                    .supplyAsync(execMyConcurrentTask(i), managedExecsvc)
                    /* 下記のように直接 Lambda 式を直接記述可能、ただし final data を利用。
                     .supplyAsync(() -> {
                     int randomValue = (int) (Math.random() * 2000);
                     mystopThread(randomValue);
                     if (data == 56) {
                     throw new IllegalStateException("Exception Occur : COUNTER " + data);
                     }
                     return data;
                     })
                     */
                    // 実行結果を受け取った後、ノンブロッキングで
                    // WebSocket クライアントへメッセージ配信
                    .thenAcceptAsync(
                            resulstInt
                            -> sendMsgToClient(session, new JSONWrappedObject("success", resulstInt, "", "se8")), managedExecsvc);
        });

        sendMsgToClient(session, new JSONWrappedObject("info", -1, "ノンブロッキング処理完了", "se8"));
    }

    /*
     MyCallableTask#call() で実装した内容を同じ内容
     並列処理タスク
     ランダムな時間(最長 2 秒間)で処理を待機するタスク
     */
    private Supplier<Integer> execMyConcurrentTask(int i) {
        Supplier<Integer> func = () -> {
            int randomValue = (int) (Math.random() * 2000);
            mystopThread(randomValue);
            if (i == 20) {
                throw new IllegalStateException("Exception Occur : COUNTER " + i);
            }
            return i;
        };
        return func;
    }

    /*
     Java SE 8 + Java EE 7 の並列処理用の実装コード例
     全てのタスク完了を取得し、例外ハンドリングも可能
     */
    protected void executeNonBlockingTasks2(Session session) {

        sendMsgToClient(session, new JSONWrappedObject("info", -1, "ノンブロッキング処理開始", "se8"));
        //全ての実行タスクを格納するリスト
        List<CompletableFuture> futures = new ArrayList<>();
        // 非同期タスクを 100 個作成
        IntStream.range(0, 10).forEach(i -> {
            CompletableFuture<Void> tasks = CompletableFuture
                    // 非同期で並列タスクを実行
                    .supplyAsync(execMyConcurrentTask(i), managedExecsvc)
                    // 実行結果を元に WebSocket クライアントへメッセージ配信
                    .thenAcceptAsync(
                            resulstInt
                            -> sendMsgToClient(session,
                                    new JSONWrappedObject("success", resulstInt, "none", "se8")), managedExecsvc);
            // 実行したタスクを格納リストへ追加
            futures.add(tasks);
        });

        //全てのタスクの実行結果
        CompletableFuture
                // リストから CompletableFuture 配列に変換
                .allOf(futures.toArray(new CompletableFuture[futures.size()]))
                // もし全ての処理が完了したら、例外発生時以外、結果を WebSocket クライアントへ
                // 全タスクの実行が完了した事を通知するための配信
                .whenComplete((Void result, Throwable exception) -> {
                    //タスク実行時の例外をハンドリング
                    if (exception != null) {
                        String reason = exception.getMessage();
                        sendMsgToClient(session, new JSONWrappedObject("error", -1, reason, "se8"));
                    }
                    sendMsgToClient(session, new JSONWrappedObject("info", -1, "ノンブロッキング処理完了", "se8")
                    );
                });
    }

    /*
     WebSocket のメッセージ配信はマルチスレッドから送信が非対応のため
     送信用には同期配信を実施
     */
    protected synchronized void sendMsgToClient(Session session, JSONWrappedObject result) {
        session.getAsyncRemote().sendObject(result);
    }

    /*
     指定したミリ秒数だけスレッドを停止
     */
    private void mystopThread(int timemil) {
        try {
            Thread.sleep(timemil);
        } catch (InterruptedException ex) {
            logger.severe(() -> ex.getMessage());
        }
    }
}

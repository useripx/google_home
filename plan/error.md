```
bash
2026-05-03 11:13:26.868 10530-10530 m140.dtv                com.googlehome.protect               I  Network fetching: true
2026-05-03 11:13:26.877 10530-10530 m140.est                com.googlehome.protect               I  Found 47 zoom mappings
2026-05-03 11:13:26.880 10530-10530 m140.est                com.googlehome.protect               I  Zoom tables loaded
2026-05-03 11:13:26.886 10530-10530 m140.dtv                com.googlehome.protect               I  requestDrawingConfig for epoch 777 legend ROADMAP
2026-05-03 11:13:26.895 10530-10622 glehome.protect         com.googlehome.protect               W  JNI critical lock held for 36.220ms on Thread[69,tid=10622,Runnable,Thread*=0x7293ef1de1f0,peer=0x3177c60,"androidmapsapi-TilePrep_1"]
2026-05-03 11:13:26.949 10530-10530 HWUI                    com.googlehome.protect               W  Image decoding logging dropped!
2026-05-03 11:13:26.970 10530-10530 m140.dtv                com.googlehome.protect               I  requestDrawingConfig for epoch 777 legend ROADMAP
2026-05-03 11:13:26.973 10530-10622 m140.eyn                com.googlehome.protect               I  styleTableCache inserted: 777 ROADMAP https://www.gstatic.com/maps/res/CompactLegend-Roadmap-EnhancedNavStyleHoldbackForGeoD-8e073db7c513cd274523de564ebeebf9
2026-05-03 11:13:27.009 10530-10622 glehome.protect         com.googlehome.protect               W  JNI critical lock held for 24.823ms on Thread[69,tid=10622,Runnable,Thread*=0x7293ef1de1f0,peer=0x3177c60,"androidmapsapi-TilePrep_1"]
2026-05-03 11:13:27.133 10530-10622 glehome.protect         com.googlehome.protect               W  JNI critical lock held for 105.069ms on Thread[69,tid=10622,Runnable,Thread*=0x7293ef1de1f0,peer=0x3177c60,"androidmapsapi-TilePrep_1"]
2026-05-03 11:13:27.163 10530-10530 Choreographer           com.googlehome.protect               I  Skipped 112 frames!  The application may be doing too much work on its main thread.
2026-05-03 11:13:27.372 10530-10530 FirebaseRepository      com.googlehome.protect               E  Error listening to location (Fix with AI)
                                                                                                    com.google.firebase.firestore.FirebaseFirestoreException: PERMISSION_DENIED: Missing or insufficient permissions.
                                                                                                    	at com.google.firebase.firestore.util.Util.exceptionFromStatus(Util.java:113)
                                                                                                    	at com.google.firebase.firestore.core.EventManager.onError(EventManager.java:247)
                                                                                                    	at com.google.firebase.firestore.core.SyncEngine.removeAndCleanupTarget(SyncEngine.java:642)
                                                                                                    	at com.google.firebase.firestore.core.SyncEngine.handleRejectedListen(SyncEngine.java:478)
                                                                                                    	at com.google.firebase.firestore.core.MemoryComponentProvider$RemoteStoreCallback.handleRejectedListen(MemoryComponentProvider.java:130)
                                                                                                    	at com.google.firebase.firestore.remote.RemoteStore.processTargetError(RemoteStore.java:591)
                                                                                                    	at com.google.firebase.firestore.remote.RemoteStore.handleWatchChange(RemoteStore.java:474)
                                                                                                    	at com.google.firebase.firestore.remote.RemoteStore.access$100(RemoteStore.java:60)
                                                                                                    	at com.google.firebase.firestore.remote.RemoteStore$1.onWatchChange(RemoteStore.java:183)
                                                                                                    	at com.google.firebase.firestore.remote.WatchStream.onNext(WatchStream.java:109)
                                                                                                    	at com.google.firebase.firestore.remote.WatchStream.onNext(WatchStream.java:38)
                                                                                                    	at com.google.firebase.firestore.remote.AbstractStream$StreamObserver.lambda$onNext$1$com-google-firebase-firestore-remote-AbstractStream$StreamObserver(AbstractStream.java:119)
                                                                                                    	at com.google.firebase.firestore.remote.AbstractStream$StreamObserver$$ExternalSyntheticLambda0.run(D8$$SyntheticClass:0)
                                                                                                    	at com.google.firebase.firestore.remote.AbstractStream$CloseGuardedRunner.run(AbstractStream.java:67)
                                                                                                    	at com.google.firebase.firestore.remote.AbstractStream$StreamObserver.onNext(AbstractStream.java:110)
                                                                                                    	at com.google.firebase.firestore.remote.FirestoreChannel$1.onMessage(FirestoreChannel.java:140)
                                                                                                    	at io.grpc.internal.DelayedClientCall$DelayedListener.onMessage(DelayedClientCall.java:473)
                                                                                                    	at io.grpc.internal.ClientCallImpl$ClientStreamListenerImpl$1MessagesAvailable.runInternal(ClientCallImpl.java:660)
                                                                                                    	at io.grpc.internal.ClientCallImpl$ClientStreamListenerImpl$1MessagesAvailable.runInContext(ClientCallImpl.java:647)
                                                                                                    	at io.grpc.internal.ContextRunnable.run(ContextRunnable.java:37)
                                                                                                    	at io.grpc.internal.SerializingExecutor.run(SerializingExecutor.java:133)
                                                                                                    	at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:524)
                                                                                                    	at java.util.concurrent.FutureTask.run(FutureTask.java:317)
                                                                                                    	at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:348)
                                                                                                    	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1156)
                                                                                                    	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:651)
                                                                                                    	at com.google.firebase.firestore.util.AsyncQueue$SynchronizedShutdownAwareExecutor$DelayedStartFactory.run(AsyncQueue.java:235)
                                                                                                    	at java.lang.Thread.run(Thread.java:1119)
                                                                                                    Caused by: io.grpc.StatusException: PERMISSION_DENIED: Missing or insufficient permissions.
                                                                                                    	at io.grpc.Status.asException(Status.java:545)
                                                                                                    	at com.google.firebase.firestore.util.Util.exceptionFromStatus(Util.java:111)
                                                                                                    	... 27 more
2026-05-03 11:13:27.393 10530-10530 AndroidRuntime          com.googlehome.protect               E  FATAL EXCEPTION: main (Fix with AI)
                                                                                                    Process: com.googlehome.protect, PID: 10530
                                                                                                    com.google.firebase.firestore.FirebaseFirestoreException: PERMISSION_DENIED: Missing or insufficient permissions.
                                                                                                    	at com.google.firebase.firestore.util.Util.exceptionFromStatus(Util.java:113)
                                                                                                    	at com.google.firebase.firestore.core.EventManager.onError(EventManager.java:247)
                                                                                                    	at com.google.firebase.firestore.core.SyncEngine.removeAndCleanupTarget(SyncEngine.java:642)
                                                                                                    	at com.google.firebase.firestore.core.SyncEngine.handleRejectedListen(SyncEngine.java:478)
                                                                                                    	at com.google.firebase.firestore.core.MemoryComponentProvider$RemoteStoreCallback.handleRejectedListen(MemoryComponentProvider.java:130)
                                                                                                    	at com.google.firebase.firestore.remote.RemoteStore.processTargetError(RemoteStore.java:591)
                                                                                                    	at com.google.firebase.firestore.remote.RemoteStore.handleWatchChange(RemoteStore.java:474)
                                                                                                    	at com.google.firebase.firestore.remote.RemoteStore.access$100(RemoteStore.java:60)
                                                                                                    	at com.google.firebase.firestore.remote.RemoteStore$1.onWatchChange(RemoteStore.java:183)
                                                                                                    	at com.google.firebase.firestore.remote.WatchStream.onNext(WatchStream.java:109)
                                                                                                    	at com.google.firebase.firestore.remote.WatchStream.onNext(WatchStream.java:38)
                                                                                                    	at com.google.firebase.firestore.remote.AbstractStream$StreamObserver.lambda$onNext$1$com-google-firebase-firestore-remote-AbstractStream$StreamObserver(AbstractStream.java:119)
                                                                                                    	at com.google.firebase.firestore.remote.AbstractStream$StreamObserver$$ExternalSyntheticLambda0.run(D8$$SyntheticClass:0)
                                                                                                    	at com.google.firebase.firestore.remote.AbstractStream$CloseGuardedRunner.run(AbstractStream.java:67)
                                                                                                    	at com.google.firebase.firestore.remote.AbstractStream$StreamObserver.onNext(AbstractStream.java:110)
                                                                                                    	at com.google.firebase.firestore.remote.FirestoreChannel$1.onMessage(FirestoreChannel.java:140)
                                                                                                    	at io.grpc.internal.DelayedClientCall$DelayedListener.onMessage(DelayedClientCall.java:473)
                                                                                                    	at io.grpc.internal.ClientCallImpl$ClientStreamListenerImpl$1MessagesAvailable.runInternal(ClientCallImpl.java:660)
                                                                                                    	at io.grpc.internal.ClientCallImpl$ClientStreamListenerImpl$1MessagesAvailable.runInContext(ClientCallImpl.java:647)
                                                                                                    	at io.grpc.internal.ContextRunnable.run(ContextRunnable.java:37)
                                                                                                    	at io.grpc.internal.SerializingExecutor.run(SerializingExecutor.java:133)
                                                                                                    	at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:524)
                                                                                                    	at java.util.concurrent.FutureTask.run(FutureTask.java:317)
                                                                                                    	at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:348)
                                                                                                    	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1156)
                                                                                                    	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:651)
                                                                                                    	at com.google.firebase.firestore.util.AsyncQueue$SynchronizedShutdownAwareExecutor$DelayedStartFactory.run(AsyncQueue.java:235)
                                                                                                    	at java.lang.Thread.run(Thread.java:1119)
                                                                                                    	Suppressed: kotlinx.coroutines.internal.DiagnosticCoroutineContextException: [StandaloneCoroutine{Cancelling}@bc16aa8, Dispatchers.Main.immediate]
                                                                                                    Caused by: io.grpc.StatusException: PERMISSION_DENIED: Missing or insufficient permissions.
                                                                                                    	at io.grpc.Status.asException(Status.java:545)
                                                                                                    	at com.google.firebase.firestore.util.Util.exceptionFromStatus(Util.java:111)
                                                                                                    	... 27 more
2026-05-03 11:13:27.395 10530-10540 HWUI                    com.googlehome.protect               I  Davey! duration=2150ms; Flags=0, FrameTimelineVsyncId=108659, IntendedVsync=2287965184106, Vsync=2288015184104, InputEventId=0, HandleInputStart=2288018044900, AnimationStart=2288018068800, PerformTraversalsStart=2288067151800, DrawStart=2288067409400, FrameDeadline=2288048517436, FrameStartTime=2288018024100, FrameInterval=16666666, WorkloadTarget=16666666, SyncQueued=2289902482700, SyncStart=2289903398000, IssueDrawCommandsStart=2289903673100, SwapBuffers=2290108478900, FrameCompleted=2290116740900, DequeueBufferDuration=33900, QueueBufferDuration=1390500, GpuCompleted=2290116740900, SwapBuffersCompleted=2290111704300, DisplayPresentTime=0, CommandSubmissionCompleted=2290108478900, 
2026-05-03 11:13:27.398 10530-10636 ProfileInstaller        com.googlehome.protect               D  Installing profile for com.googlehome.protect
2026-05-03 11:13:27.417 10530-10540 HWUI                    com.googlehome.protect               I  Davey! duration=1988ms; Flags=0, FrameTimelineVsyncId=108668, IntendedVsync=2288031850770, Vsync=2289898517362, InputEventId=0, HandleInputStart=2289912161400, AnimationStart=2289912232000, PerformTraversalsStart=2289982437100, DrawStart=2289983078300, FrameDeadline=2290148517352, FrameStartTime=2289910278400, FrameInterval=16666666, WorkloadTarget=16666666, SyncQueued=2289983298300, SyncStart=2290112181900, IssueDrawCommandsStart=2290112953600, SwapBuffers=2290115635300, FrameCompleted=2290149402300, DequeueBufferDuration=11913200, QueueBufferDuration=439800, GpuCompleted=2290149402300, SwapBuffersCompleted=2290142682700, DisplayPresentTime=0, CommandSubmissionCompleted=2290115635300, 
2026-05-03 11:13:27.439 10530-10530 Process                 com.googlehome.protect               I  Sending signal. PID: 10530 SIG: 9
---------------------------- PROCESS ENDED (10530) for package com.googlehome.protect ----------------------------
```

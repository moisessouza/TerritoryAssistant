package ged.mediaplayerremote.domain.executor;

import rx.Scheduler;

public interface PostExecutionThread
{
    Scheduler getScheduler();
}

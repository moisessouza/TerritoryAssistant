package ged.mediaplayerremote.domain.interactor;

import ged.mediaplayerremote.domain.executor.PostExecutionThread;
import ged.mediaplayerremote.domain.executor.ThreadExecutor;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;


/**
 * Base class for all parameterless interactor classes to extend.
 */
public abstract class UseCase {
    private final ThreadExecutor threadExecutor;
    private final PostExecutionThread postExecutionThread;

    private Subscription subscription;


    protected UseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        this.threadExecutor = threadExecutor;
        this.postExecutionThread = postExecutionThread;
    }

    /**
     * Build an {@link rx.Observable} which will be used to return results of an {@link UseCase}.
     */
    protected abstract Observable buildUseCaseObservable();

    /**
     * Execute the use case.
     * @param useCaseSubscriber A {@link Subscriber} object that will await a result of the {@link UseCase}
     */
    @SuppressWarnings("unchecked")
    public void execute(Subscriber useCaseSubscriber) {
        subscription = buildUseCaseObservable().subscribeOn(Schedulers.from(threadExecutor))
                .observeOn(postExecutionThread.getScheduler()).subscribe(useCaseSubscriber);
    }

    public void unsubscribe() {
        if (!isUnsubscribed())
            subscription.unsubscribe();
    }

    public boolean isUnsubscribed() {
        return subscription == null || subscription.isUnsubscribed();
    }

    public boolean isSubscribed() {
        return !isUnsubscribed();
    }

}

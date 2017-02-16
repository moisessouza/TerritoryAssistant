package ged.mediaplayerremote.domain.interactor;

import ged.mediaplayerremote.domain.executor.PostExecutionThread;
import ged.mediaplayerremote.domain.executor.ThreadExecutor;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * Base class to be extended by all interactor classes with 1 parameter.
 */
public abstract class UseCaseParametrized<T> {
    private final ThreadExecutor threadExecutor;
    private final PostExecutionThread postExecutionThread;

    private Subscription subscription;

    protected UseCaseParametrized(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        this.threadExecutor = threadExecutor;
        this.postExecutionThread = postExecutionThread;
    }

    /**
     * Build an {@link rx.Observable} which will be used to return results of an {@link UseCase}.
     */
    protected abstract Observable buildUseCaseObservable(T param);

    /**
     * Execute the use case.
     * @param useCaseSubscriber A {@link Subscriber} object that will await a result of the {@link UseCaseParametrized}
     * @param param An object to be passed as a parameter to the Use Case.
     */
    @SuppressWarnings("unchecked")
    public void execute(Subscriber useCaseSubscriber, T param) {
        subscription = buildUseCaseObservable(param).subscribeOn(Schedulers.from(threadExecutor))
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

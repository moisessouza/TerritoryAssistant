package ged.mediaplayerremote.domain.interactor;

/**
  * Base class for all {@link rx.Subscriber} implementations.
  */
public class DefaultSubscriber<T> extends rx.Subscriber<T> {
    @Override public void onCompleted() {
        // no-op by default.
    }

    @Override public void onError(Throwable e) {
        // no-op by default.
    }

    @Override public void onNext(T t) {
        // no-op by default.
    }
}

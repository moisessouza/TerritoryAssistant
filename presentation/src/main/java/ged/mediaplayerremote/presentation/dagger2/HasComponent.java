package ged.mediaplayerremote.presentation.dagger2;

/**
 * Interface for fragments that have a component for dependency injection.
 */
public interface HasComponent<C>
{
    C getComponent();
}

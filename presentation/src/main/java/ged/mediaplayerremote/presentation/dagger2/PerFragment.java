package ged.mediaplayerremote.presentation.dagger2;

import javax.inject.Scope;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Scoping annotation for Dagger2 dependency injection.
 */
@Scope
@Retention(RUNTIME)
public @interface PerFragment
{
}

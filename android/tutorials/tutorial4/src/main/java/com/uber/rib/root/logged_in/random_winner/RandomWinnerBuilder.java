package com.uber.rib.root.logged_in.random_winner;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.uber.rib.core.InteractorBaseComponent;
import com.uber.rib.core.ViewBuilder;
import com.uber.rib.root.UserName;

import java.lang.annotation.Retention;

import javax.inject.Named;
import javax.inject.Qualifier;
import javax.inject.Scope;

import dagger.Binds;
import dagger.BindsInstance;
import dagger.Provides;

import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Builder for the {@link RandomWinnerScope}. Not a real game. This just picks a random winner than exits.
 */
public class RandomWinnerBuilder
        extends ViewBuilder<RandomWinnerView, RandomWinnerRouter, RandomWinnerBuilder.ParentComponent> {

    public RandomWinnerBuilder(ParentComponent dependency) {
        super(dependency);
    }

    /**
     * Builds a new {@link RandomWinnerRouter}.
     *
     * @param parentViewGroup parent view group that this router's view will be added to.
     * @return a new {@link RandomWinnerRouter}.
     */
    public RandomWinnerRouter build(ViewGroup parentViewGroup) {
        RandomWinnerView view = createView(parentViewGroup);
        RandomWinnerInteractor interactor = new RandomWinnerInteractor();
        Component component = DaggerRandomWinnerBuilder_Component.builder()
                .parentComponent(getDependency())
                .view(view)
                .interactor(interactor)
                .build();
        return component.randomwinnerRouter();
    }

    @Override
    protected RandomWinnerView inflateView(LayoutInflater inflater, ViewGroup parentViewGroup) {
        // Just inflate a silly useless view that does nothing.
        return new RandomWinnerView(parentViewGroup.getContext());
    }

    public interface ParentComponent {
        RandomWinnerInteractor.Listener randomWinnerListener();
        @Named("player_one") UserName playerOne();
        @Named("player_two") UserName playerTwo();
    }

    @dagger.Module
    public abstract static class Module {

        @RandomWinnerScope
        @Binds
        abstract RandomWinnerInteractor.RandomWinnerPresenter presenter(RandomWinnerView view);

        @RandomWinnerScope
        @Provides
        static RandomWinnerRouter router(
            Component component,
            RandomWinnerView view,
            RandomWinnerInteractor interactor) {
            return new RandomWinnerRouter(view, interactor, component);
        }
    }

    @RandomWinnerScope
    @dagger.Component(modules = Module.class,
           dependencies = ParentComponent.class)
    interface Component extends InteractorBaseComponent<RandomWinnerInteractor>, BuilderComponent {

        @dagger.Component.Builder
        interface Builder {
            @BindsInstance
            Builder interactor(RandomWinnerInteractor interactor);
            @BindsInstance
            Builder view(RandomWinnerView view);
            Builder parentComponent(ParentComponent component);
            Component build();
        }
    }

    interface BuilderComponent  {
        RandomWinnerRouter randomwinnerRouter();
    }

    @Scope
    @Retention(CLASS)
    @interface RandomWinnerScope { }

    @Qualifier
    @Retention(CLASS)
    @interface RandomWinnerInternal { }
}

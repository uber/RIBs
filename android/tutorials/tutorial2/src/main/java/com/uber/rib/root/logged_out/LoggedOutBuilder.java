package com.uber.rib.root.logged_out;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.uber.rib.core.InteractorBaseComponent;
import com.uber.rib.core.ViewBuilder;
import com.uber.rib.tutorial1.R;

import java.lang.annotation.Retention;

import javax.inject.Scope;
import javax.inject.Qualifier;

import dagger.Provides;
import dagger.Binds;
import dagger.BindsInstance;

import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Builder for the {@link LoggedOutScope}.
 */
public class LoggedOutBuilder
        extends ViewBuilder<LoggedOutView, LoggedOutRouter, LoggedOutBuilder.ParentComponent> {

    public LoggedOutBuilder(ParentComponent dependency) {
        super(dependency);
    }

    /**
     * Builds a new {@link LoggedOutRouter}.
     *
     * @param parentViewGroup parent view group that this router's view will be added to.
     * @return a new {@link LoggedOutRouter}.
     */
    public LoggedOutRouter build(ViewGroup parentViewGroup) {
        LoggedOutView view = createView(parentViewGroup);
        LoggedOutInteractor interactor = new LoggedOutInteractor();
        Component component = DaggerLoggedOutBuilder_Component.builder()
                .parentComponent(getDependency())
                .view(view)
                .interactor(interactor)
                .build();
        return component.loggedoutRouter();
    }

    @Override
    protected LoggedOutView inflateView(LayoutInflater inflater, ViewGroup parentViewGroup) {
        return (LoggedOutView) inflater.inflate(R.layout.logged_out_rib, parentViewGroup, false);
    }

    public interface ParentComponent {
        // TODO: Define dependencies required from your parent interactor here.
    }

    @dagger.Module
    public abstract static class Module {

        @LoggedOutScope
        @Binds
        abstract LoggedOutInteractor.LoggedOutPresenter presenter(LoggedOutView view);

        @LoggedOutScope
        @Provides
        static LoggedOutRouter router(
            Component component,
            LoggedOutView view,
            LoggedOutInteractor interactor) {
            return new LoggedOutRouter(view, interactor, component);
        }

        // TODO: Create provider methods for dependencies created by this Rib. These should be static.
    }

    @LoggedOutScope
    @dagger.Component(modules = Module.class,
           dependencies = ParentComponent.class)
    interface Component extends InteractorBaseComponent<LoggedOutInteractor>, BuilderComponent {

        @dagger.Component.Builder
        interface Builder {
            @BindsInstance
            Builder interactor(LoggedOutInteractor interactor);
            @BindsInstance
            Builder view(LoggedOutView view);
            Builder parentComponent(ParentComponent component);
            Component build();
        }
    }

    interface BuilderComponent  {
        LoggedOutRouter loggedoutRouter();
    }

    @Scope
    @Retention(CLASS)
    @interface LoggedOutScope { }

    @Qualifier
    @Retention(CLASS)
    @interface LoggedOutInternal { }
}

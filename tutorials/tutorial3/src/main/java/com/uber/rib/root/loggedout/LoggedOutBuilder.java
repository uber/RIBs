/*
 * Copyright (C) 2017. Uber Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uber.rib.root.loggedout;

import static java.lang.annotation.RetentionPolicy.CLASS;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.uber.rib.core.InteractorBaseComponent;
import com.uber.rib.core.ViewBuilder;
import com.uber.rib.tutorial1.R;
import dagger.Binds;
import dagger.BindsInstance;
import dagger.Provides;
import java.lang.annotation.Retention;
import javax.inject.Qualifier;
import javax.inject.Scope;

/** Builder for the {@link LoggedOutScope}. */
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
    Component component =
        DaggerLoggedOutBuilder_Component.builder()
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

    LoggedOutInteractor.Listener listener();
  }

  @dagger.Module
  public abstract static class Module {

    @LoggedOutScope
    @Binds
    abstract LoggedOutInteractor.LoggedOutPresenter presenter(LoggedOutView view);

    @LoggedOutScope
    @Provides
    static LoggedOutRouter router(
        Component component, LoggedOutView view, LoggedOutInteractor interactor) {
      return new LoggedOutRouter(view, interactor, component);
    }

    // TODO: Create provider methods for dependencies created by this Rib. These should be static.
  }

  @LoggedOutScope
  @dagger.Component(modules = Module.class, dependencies = ParentComponent.class)
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

  interface BuilderComponent {

    LoggedOutRouter loggedoutRouter();
  }

  @Scope
  @Retention(CLASS)
  @interface LoggedOutScope {}

  @Qualifier
  @Retention(CLASS)
  @interface LoggedOutInternal {}
}

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
package com.uber.rib.root;

import static java.lang.annotation.RetentionPolicy.CLASS;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.uber.rib.core.InteractorBaseComponent;
import com.uber.rib.core.ViewBuilder;
import com.uber.rib.root.loggedin.LoggedInBuilder;
import com.uber.rib.root.loggedout.LoggedOutBuilder;
import com.uber.rib.root.loggedout.LoggedOutInteractor;
import com.uber.rib.tutorial1.R;
import dagger.Binds;
import dagger.BindsInstance;
import dagger.Provides;
import java.lang.annotation.Retention;
import javax.inject.Scope;

/** Builder for the {@link RootScope}. */
public class RootBuilder extends ViewBuilder<RootView, RootRouter, RootBuilder.ParentComponent> {

  public RootBuilder(ParentComponent dependency) {
    super(dependency);
  }

  /**
   * Builds a new {@link RootRouter}.
   *
   * @param parentViewGroup parent view group that this router's view will be added to.
   * @return a new {@link RootRouter}.
   */
  public RootRouter build(ViewGroup parentViewGroup) {
    RootView view = createView(parentViewGroup);
    RootInteractor interactor = new RootInteractor();
    Component component =
        DaggerRootBuilder_Component.builder()
            .parentComponent(getDependency())
            .view(view)
            .interactor(interactor)
            .build();
    return component.rootRouter();
  }

  @Override
  protected RootView inflateView(LayoutInflater inflater, ViewGroup parentViewGroup) {
    return (RootView) inflater.inflate(R.layout.root_rib, parentViewGroup, false);
  }

  public interface ParentComponent {
    // Define dependencies required from your parent interactor here.
  }

  @dagger.Module
  public abstract static class Module {

    @RootScope
    @Provides
    static LoggedOutInteractor.Listener loggedOutListener(RootInteractor rootInteractor) {
      return rootInteractor.new LoggedOutListener();
    }

    @RootScope
    @Binds
    abstract RootInteractor.RootPresenter presenter(RootView view);

    @RootScope
    @Provides
    static RootRouter router(Component component, RootView view, RootInteractor interactor) {
      return new RootRouter(
          view,
          interactor,
          component,
          new LoggedOutBuilder(component),
          new LoggedInBuilder(component));
    }
  }

  @RootScope
  @dagger.Component(modules = Module.class, dependencies = ParentComponent.class)
  interface Component
      extends InteractorBaseComponent<RootInteractor>,
          LoggedOutBuilder.ParentComponent,
          LoggedInBuilder.ParentComponent,
          BuilderComponent {

    @dagger.Component.Builder
    interface Builder {

      @BindsInstance
      Builder interactor(RootInteractor interactor);

      @BindsInstance
      Builder view(RootView view);

      Builder parentComponent(ParentComponent component);

      Component build();
    }
  }

  interface BuilderComponent {

    RootRouter rootRouter();
  }

  @Scope
  @Retention(CLASS)
  @interface RootScope {}
}

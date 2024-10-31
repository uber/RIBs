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
package com.uber.rib.root.loggedin.offgame;

import static java.lang.annotation.RetentionPolicy.CLASS;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.uber.rib.core.InteractorBaseComponent;
import com.uber.rib.core.ViewBuilder;
import com.uber.rib.root.loggedin.ScoreStream;
import com.uber.rib.tutorial1.R;
import dagger.Binds;
import dagger.BindsInstance;
import dagger.Provides;
import java.lang.annotation.Retention;
import javax.inject.Named;
import javax.inject.Qualifier;
import javax.inject.Scope;

/** Builder for the {@link OffGameScope}. */
public class OffGameBuilder
    extends ViewBuilder<OffGameView, OffGameRouter, OffGameBuilder.ParentComponent> {

  public OffGameBuilder(ParentComponent dependency) {
    super(dependency);
  }

  /**
   * Builds a new {@link OffGameRouter}.
   *
   * @param parentViewGroup parent view group that this router's view will be added to.
   * @return a new {@link OffGameRouter}.
   */
  public OffGameRouter build(ViewGroup parentViewGroup) {
    OffGameView view = createView(parentViewGroup);
    OffGameInteractor interactor = new OffGameInteractor();
    Component component =
        DaggerOffGameBuilder_Component.builder()
            .parentComponent(getDependency())
            .view(view)
            .interactor(interactor)
            .build();
    return component.offgameRouter();
  }

  @Override
  protected OffGameView inflateView(LayoutInflater inflater, ViewGroup parentViewGroup) {
    return (OffGameView) inflater.inflate(R.layout.off_game_rib, parentViewGroup, false);
  }

  public interface ParentComponent {

    @Named("player_one")
    String playerOne();

    @Named("player_two")
    String playerTwo();

    OffGameInteractor.Listener listener();

    ScoreStream scoreStream();
  }

  @dagger.Module
  public abstract static class Module {

    @OffGameScope
    @Binds
    abstract OffGameInteractor.OffGamePresenter presenter(OffGameView view);

    @OffGameScope
    @Provides
    static OffGameRouter router(
        Component component, OffGameView view, OffGameInteractor interactor) {
      return new OffGameRouter(view, interactor, component);
    }
  }

  @OffGameScope
  @dagger.Component(modules = Module.class, dependencies = ParentComponent.class)
  interface Component extends InteractorBaseComponent<OffGameInteractor>, BuilderComponent {

    @dagger.Component.Builder
    interface Builder {

      @BindsInstance
      Builder interactor(OffGameInteractor interactor);

      @BindsInstance
      Builder view(OffGameView view);

      Builder parentComponent(ParentComponent component);

      Component build();
    }
  }

  interface BuilderComponent {

    OffGameRouter offgameRouter();
  }

  @Scope
  @Retention(CLASS)
  @interface OffGameScope {}

  @Qualifier
  @Retention(CLASS)
  @interface OffGameInternal {}
}

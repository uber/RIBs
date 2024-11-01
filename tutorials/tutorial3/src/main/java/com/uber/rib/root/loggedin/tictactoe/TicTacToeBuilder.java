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
package com.uber.rib.root.loggedin.tictactoe;

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

/** Builder for the {@link TicTacToeScope}. */
public class TicTacToeBuilder
    extends ViewBuilder<TicTacToeView, TicTacToeRouter, TicTacToeBuilder.ParentComponent> {

  public TicTacToeBuilder(ParentComponent dependency) {
    super(dependency);
  }

  /**
   * Builds a new {@link TicTacToeRouter}.
   *
   * @param parentViewGroup parent view group that this router's view will be added to.
   * @return a new {@link TicTacToeRouter}.
   */
  public TicTacToeRouter build(ViewGroup parentViewGroup) {
    TicTacToeView view = createView(parentViewGroup);
    TicTacToeInteractor interactor = new TicTacToeInteractor();
    Component component =
        DaggerTicTacToeBuilder_Component.builder()
            .parentComponent(getDependency())
            .view(view)
            .interactor(interactor)
            .build();
    return component.tictactoeRouter();
  }

  @Override
  protected TicTacToeView inflateView(LayoutInflater inflater, ViewGroup parentViewGroup) {
    return (TicTacToeView) inflater.inflate(R.layout.tic_tac_toe_rib, parentViewGroup, false);
  }

  public interface ParentComponent {
    // TODO: Define dependencies required from your parent interactor here.
  }

  @dagger.Module
  public abstract static class Module {

    @TicTacToeScope
    @Binds
    abstract TicTacToeInteractor.TicTacToePresenter presenter(TicTacToeView view);

    @TicTacToeScope
    @Provides
    static TicTacToeRouter router(
        Component component, TicTacToeView view, TicTacToeInteractor interactor) {
      return new TicTacToeRouter(view, interactor, component);
    }
  }

  @TicTacToeScope
  @dagger.Component(modules = Module.class, dependencies = ParentComponent.class)
  interface Component extends InteractorBaseComponent<TicTacToeInteractor>, BuilderComponent {

    @dagger.Component.Builder
    interface Builder {

      @BindsInstance
      Builder interactor(TicTacToeInteractor interactor);

      @BindsInstance
      Builder view(TicTacToeView view);

      Builder parentComponent(ParentComponent component);

      Component build();
    }
  }

  interface BuilderComponent {

    TicTacToeRouter tictactoeRouter();
  }

  @Scope
  @Retention(CLASS)
  @interface TicTacToeScope {}

  @Qualifier
  @Retention(CLASS)
  @interface TicTacToeInternal {}
}

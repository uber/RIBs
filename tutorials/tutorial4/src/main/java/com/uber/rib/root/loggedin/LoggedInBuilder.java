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
package com.uber.rib.root.loggedin;

import static java.lang.annotation.RetentionPolicy.CLASS;

import android.view.ViewGroup;
import com.google.common.collect.Lists;
import com.uber.rib.core.Builder;
import com.uber.rib.core.EmptyPresenter;
import com.uber.rib.core.InteractorBaseComponent;
import com.uber.rib.core.ViewRouter;
import com.uber.rib.root.RootView;
import com.uber.rib.root.UserName;
import com.uber.rib.root.loggedin.offgame.OffGameBuilder;
import com.uber.rib.root.loggedin.offgame.OffGameInteractor;
import com.uber.rib.root.loggedin.randomWinner.RandomWinnerBuilder;
import com.uber.rib.root.loggedin.randomWinner.RandomWinnerInteractor;
import com.uber.rib.root.loggedin.tictactoe.TicTacToeBuilder;
import com.uber.rib.root.loggedin.tictactoe.TicTacToeInteractor;
import dagger.Binds;
import dagger.BindsInstance;
import dagger.Provides;
import java.lang.annotation.Retention;
import java.util.List;
import javax.inject.Named;
import javax.inject.Qualifier;
import javax.inject.Scope;

public class LoggedInBuilder extends Builder<LoggedInRouter, LoggedInBuilder.ParentComponent> {

  public LoggedInBuilder(ParentComponent dependency) {
    super(dependency);
  }

  /**
   * Builds a new {@link LoggedInRouter}.
   *
   * @return a new {@link LoggedInRouter}.
   */
  public LoggedInRouter build(UserName playerOne, UserName playerTwo) {
    LoggedInInteractor interactor = new LoggedInInteractor();
    Component component =
        DaggerLoggedInBuilder_Component.builder()
            .parentComponent(getDependency())
            .interactor(interactor)
            .playerOne(playerOne)
            .playerTwo(playerTwo)
            .build();

    return component.loggedinRouter();
  }

  public interface ParentComponent {

    RootView rootView();
  }

  @dagger.Module
  public abstract static class Module {

    @LoggedInScope
    @Provides
    static EmptyPresenter presenter() {
      return new EmptyPresenter();
    }

    @LoggedInScope
    @Provides
    static LoggedInRouter router(
        Component component, LoggedInInteractor interactor, RootView rootView) {
      return new LoggedInRouter(
          interactor,
          component,
          rootView,
          new OffGameBuilder(component),
          new TicTacToeBuilder(component));
    }

    @LoggedInScope
    @LoggedInInternal
    @Provides
    static MutableScoreStream mutableScoreStream(
        @Named("player_one") UserName playerOne, @Named("player_two") UserName playerTwo) {
      return new MutableScoreStream(playerOne, playerTwo);
    }

    @LoggedInScope
    @Provides
    static OffGameInteractor.Listener listener(LoggedInInteractor interactor) {
      return interactor.new OffGameListener();
    }

    @LoggedInScope
    @Binds
    abstract RandomWinnerInteractor.Listener randomWinnerListener(
        LoggedInInteractor.GameListener listener);

    @LoggedInScope
    @Binds
    abstract TicTacToeInteractor.Listener ticTacToeGameListener(
        LoggedInInteractor.GameListener listener);

    @LoggedInScope
    @Provides
    static LoggedInInteractor.GameListener ticTacToeListener(LoggedInInteractor interactor) {
      return interactor.new GameListener();
    }

    @LoggedInScope
    @Binds
    abstract ScoreStream scoreStream(@LoggedInInternal MutableScoreStream mutableScoreStream);

    @LoggedInInternal
    @Provides
    static List<GameProvider> gameProviders(final Component component) {
      // Decorate the game builders with a "name" key so we can treat them generically elsewhere.
      GameProvider ticTacToeGame =
          new GameProvider() {
            @Override
            public String gameName() {
              return "TicTacToe";
            }

            @Override
            public ViewRouter viewRouter(ViewGroup viewGroup) {
              return new TicTacToeBuilder(component).build(viewGroup);
            }
          };
      GameProvider randomWinnerGame =
          new GameProvider() {
            @Override
            public String gameName() {
              return "RandomWinner";
            }

            @Override
            public ViewRouter viewRouter(ViewGroup viewGroup) {
              return new RandomWinnerBuilder(component).build(viewGroup);
            }
          };
      return Lists.newArrayList(ticTacToeGame, randomWinnerGame);
    }

    @LoggedInScope
    @Binds
    abstract List<? extends GameKey> gameKeys(@LoggedInInternal List<GameProvider> gameProviders);
  }

  @LoggedInScope
  @dagger.Component(modules = Module.class, dependencies = ParentComponent.class)
  public interface Component
      extends InteractorBaseComponent<LoggedInInteractor>,
          BuilderComponent,
          OffGameBuilder.ParentComponent,
          TicTacToeBuilder.ParentComponent,
          RandomWinnerBuilder.ParentComponent {

    @dagger.Component.Builder
    interface Builder {

      @BindsInstance
      Builder interactor(LoggedInInteractor interactor);

      Builder parentComponent(ParentComponent component);

      Component build();

      @BindsInstance
      Builder playerOne(@Named("player_one") UserName playerOne);

      @BindsInstance
      Builder playerTwo(@Named("player_two") UserName playerTwo);
    }
  }

  interface BuilderComponent {
    LoggedInRouter loggedinRouter();
  }

  @Scope
  @Retention(CLASS)
  @interface LoggedInScope {}

  @Qualifier
  @Retention(CLASS)
  @interface LoggedInInternal {}
}

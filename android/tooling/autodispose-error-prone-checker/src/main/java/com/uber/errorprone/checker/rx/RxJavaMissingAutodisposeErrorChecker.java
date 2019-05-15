/*
 * Copyright (C) 2017. Uber Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.uber.errorprone.checker.rx;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableList;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.bugpatterns.BugChecker.MethodInvocationTreeMatcher;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.matchers.Matcher;
import com.google.errorprone.matchers.method.MethodMatchers;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.tools.javac.code.Type;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

import static com.google.errorprone.BugPattern.SeverityLevel.ERROR;
import static com.google.errorprone.matchers.Matchers.instanceMethod;

/**
 * Checker for subscriptions not binding to lifecycles in Routers, Interactors, Presenters and
 * Workers.
 */
@AutoService(BugChecker.class)
@BugPattern(
    name = "RxJavaMissingAutodisposeErrorChecker",
    summary = "Always apply an Autodispose scope before subscribing",
    severity = ERROR
)
public class RxJavaMissingAutodisposeErrorChecker extends BugChecker
    implements MethodInvocationTreeMatcher {

  private static final ImmutableList<MethodMatchers.MethodNameMatcher> METHOD_NAME_MATCHERS;
  private static final ImmutableList<MethodMatchers.MethodNameMatcher> SUBSCRIBE_MATCHERS;
  private static final String SUBSCRIBE = "subscribe";
  private static final String TO = "to";

  static {
    ImmutableList.Builder<MethodMatchers.MethodNameMatcher> builder = new ImmutableList.Builder<>();
    builder
        .add(instanceMethod().onDescendantOf(Single.class.getName()).named(TO))
        .add(instanceMethod().onDescendantOf(Observable.class.getName()).named(TO))
        .add(instanceMethod().onDescendantOf(Completable.class.getName()).named(TO))
        .add(instanceMethod().onDescendantOf(Flowable.class.getName()).named(TO))
        .add(instanceMethod().onDescendantOf(Maybe.class.getName()).named(TO));
    METHOD_NAME_MATCHERS = builder.build();

    ImmutableList.Builder<MethodMatchers.MethodNameMatcher> builder2 =
        new ImmutableList.Builder<>();
    builder2
        .add(instanceMethod().onDescendantOf(Single.class.getName()).named(SUBSCRIBE))
        .add(instanceMethod().onDescendantOf(Observable.class.getName()).named(SUBSCRIBE))
        .add(instanceMethod().onDescendantOf(Completable.class.getName()).named(SUBSCRIBE))
        .add(instanceMethod().onDescendantOf(Flowable.class.getName()).named(SUBSCRIBE))
        .add(instanceMethod().onDescendantOf(Maybe.class.getName()).named(SUBSCRIBE));
    SUBSCRIBE_MATCHERS = builder2.build();
  }

  private static final Matcher<ExpressionTree> TO_CALL_MATCHER =
      new Matcher<ExpressionTree>() {
        @Override
        public boolean matches(ExpressionTree tree, VisitorState state) {
          if (!(tree instanceof MethodInvocationTree)) {
            return false;
          }
          MethodInvocationTree invTree = (MethodInvocationTree) tree;

          final MemberSelectTree memberTree = (MemberSelectTree) invTree.getMethodSelect();
          if (!memberTree.getIdentifier().contentEquals(TO)) {
            return false;
          }

          for (MethodMatchers.MethodNameMatcher nameMatcher : METHOD_NAME_MATCHERS) {
            if (nameMatcher.matches(invTree, state)) {
              ExpressionTree arg = invTree.getArguments().get(0);
              final Type scoper = state.getTypeFromString("com.uber.autodispose.Scoper");
              return ASTHelpers.isSubtype(ASTHelpers.getType(arg), scoper, state);
            }
          }
          return false;
        }
      };

  private static final Matcher<MethodInvocationTree> MATCHER =
      new Matcher<MethodInvocationTree>() {
        @Override
        public boolean matches(MethodInvocationTree tree, VisitorState state) {

          boolean matchFound = false;
          try {
            final MemberSelectTree memberTree = (MemberSelectTree) tree.getMethodSelect();
            if (!memberTree.getIdentifier().contentEquals(SUBSCRIBE)) {
              return false;
            }

            for (MethodMatchers.MethodNameMatcher nameMatcher : SUBSCRIBE_MATCHERS) {
              if (!nameMatcher.matches(tree, state)) {
                continue;
              } else {
                matchFound = true;
                break;
              }
            }
            if (!matchFound) {
              return false;
            }

            ClassTree enclosingClass =
                ASTHelpers.findEnclosingNode(state.getPath(), ClassTree.class);
            Type.ClassType enclosingClassType = ASTHelpers.getType(enclosingClass);
            Type routerType = state.getTypeFromString("com.uber.rib.core.Router");
            Type presenterType = state.getTypeFromString("com.uber.rib.core.Presenter");
            Type interactorType = state.getTypeFromString("com.uber.rib.core.Interactor");
            Type workerType = state.getTypeFromString("com.uber.rib.core.Worker");

            if (!ASTHelpers.isSubtype(enclosingClassType, routerType, state)
                && !ASTHelpers.isSubtype(enclosingClassType, presenterType, state)
                && !ASTHelpers.isSubtype(enclosingClassType, interactorType, state)
                && !ASTHelpers.isSubtype(enclosingClassType, workerType, state)) {
              return false;
            }

            return !TO_CALL_MATCHER.matches(memberTree.getExpression(), state);
          } catch (ClassCastException e) {
            return false;
          }
        }
      };

  @Override
  public Description matchMethodInvocation(MethodInvocationTree tree, VisitorState state) {
    if (MATCHER.matches(tree, state)) {
      return buildDescription(tree).build();
    } else {
      return Description.NO_MATCH;
    }
  }

  @Override
  public String linkUrl() {
    return "https://github.com/uber/RIBs/blob/memory_leaks_module/android/demos/memory-leaks/README.md";
  }
}

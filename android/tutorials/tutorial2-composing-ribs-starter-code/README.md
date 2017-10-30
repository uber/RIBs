# RIB Tutorial 2: Composing RIBs to create features

## Prerequisites
Understand the concepts from tutorial 1 and fully setup Android Studio and the Presidio plugin.

## Goals
The goals of this code lab are to learn the following:
* Child RIB calling up to parent RIB via listener
* Attaching/detaching a child RIB
* Creating a view-less RIB. And avoiding memory leaks in view-less RIBs.
* Mocks and unit testing.

## Overview
In this code lab, we will implement  a basic TicTacToe game that demonstrates basic inter-RIB communication, unit testing, and RIB lifecycles. This tutorial attempts to ignore dependency injection as much as possible.

The application’s RIB structure will look like the following. You are given the yellow nodes to start with. You’ll need to write the white nodes yourself and hook it up.
<img src="https://github.com/uber/RIBs/blob/master/android/tutorials/tutorial2-composing-ribs-starter-code/tutorial_assets/overview.png?raw=true" width="600">

When done with this tutorial the application will contain the following behavior:
* Clicking Login inside LoggedOut will detach the LoggedOut RIB and attach the LoggedIn RIB
* By default the LoggedIn state will attach the OffGame RIB 
* Clicking “Start Game” will detach OffGame and attach TicTacToe

The application will look like the following once you are done
<img src="https://github.com/uber/RIBs/blob/master/android/tutorials/tutorial2-composing-ribs-starter-code/tutorial_assets/result.png?raw=true" width="600">

LoggedOut View (left), OffGame View (middle) and TicTacToe View (right)

## Handling Login Requests
### Step 0: Buck Project
You’ll be writing this code lab inside the tutorial2 module. This module contains the model solution for tutorial1 in addition a bit of extra code. Let’s start by running buck project for lesson102 so that this module can be indexed by intellij.
```text
./gradlew :tutorials:tutorial2:installDebug
```

### Step 1: Communicate LoggedOut Taps to Root
You’ll need to declare a listener interface inside LoggedOutInteractor. The RootInteractor will implement this interface in order to observe events being emitted from LoggedOut. 

Declare the interface inside LoggedOutInteractor. This is declared inside LoggedOut instead of Root because the child should never know the type of its parent.
```java
public interface Listener {
  void login(String userName);
}
```

Next add an inject to the top of your interactor. The inject means that the field will be populated from the builder.
```java
@Inject Listener listener;
```

Next, use the listener. Instead of logging the username you get from the LoggedOutPresenter pass it into the listener. 

```java
presenter
  .loginName()
  .subscribe(new Consumer<String>() {
    @Override
    public void accept(String name) throws Exception {
      if (!isEmpty(name)) {
        listener.login(name);
      }
    }
  });
```

Since the builder needs to get the listener from somewhere, you’ll now need to modify LoggedOutBuilder. Update LoggedOutBuilder’s ParentComponent interface. This is the interface that declares all dependencies that LoggedOut’s parent needs to fulfill.

```java
public interface ParentComponent extends LoggedOutOptionalExtension.ParentComponent {
  LoggedOutInteractor.Listener listener();
}
```

If you try to build the app you’ll see a build failure. This is because the Root scope doesn’t yet fulfill the contract requested by the LoggedOut RIB.

```text
./gradlew :tutorials:tutorial2:installDebug
```

### Step 2: Handling LoggedOut Taps in Root
We’ll configure the Root RIB to receive listener events from LoggedOut and then handle them by detaching LoggedOut and attaching LoggedIn.

First, let’s modify RootInteractor to implement the LoggedOut’s listener. Add the following nested class inside RootInteractor. Just stub out the new Router methods for now. Ie, create empty methods so that the app compiles.

```java
class LoggedOutListener implements LoggedOutInteractor.Listener {
  @Override
  public void login(String userName) {
    // Switch to logged in. Let’s just ignore userName for now. 
    getRouter().detachLoggedOut();
    getRouter().attachLoggedIn();
  }
}
```

Next, we need instantiate this listener somewhere so that LoggedOut can get it from Root. Add the following to the RootBuilder.Module. This code is a strange looking if you’re not familiar with Dagger. We’ll delve deeper into dependency injection in Lesson 103.

```java
@RootScope
@Provides
static LoggedOutInteractor.Listener loggedOutListener(RootInteractor rootInteractor) {
  return rootInteractor.new LoggedOutListener();
}
```

If you try to build now, everything should work fine. Let’s try building.

```text
./gradlew :tutorials:tutorial2:installDebug
```

The app’s login button still won’t work. This is because the RootRouter methods are still stubbed out.

### Step 3: Unit Testing Root
Let’s unit test LoggedOut interactor before we bother creating LoggedIn. There is no reason why the LoggedIn RIB needs to exist in order to unit test the LoggedOut or Root interactors.

Let’s start by running the unit tests.

```text
./gradlew :tutorials:tutorial2:testDebugUnitTest
```

You’ll see a compiler error inside the LoggedOutinteractorTest that you generated in tutorial1. This is because the TestLoggedOutInteractor used inside the LoggedOutInteracter requires a listener. The TestLoggedOutInteractor class is a generated test helper class. It is an extension of LoggedOutInteractor that is easier to test.

To fix the compiler error add a mock LoggedOutInteractor.Listener and pass it into the TestLoggedOutInteractor. You just need to add one @Mock line to do this.

Next write two unit tests to ensure that LoggedOutInteractor calls its listener when it receives valid view input. One of these tests will fail. Fix this by adding empty text check inside the interactor. 

Note: We recommend not to use TextUtils because TextUtils will cause a runtime crash. AOSP classes can’t be used inside non-roboelectric tests. And roboelectric tests are really slow, so we avoid them. 

```java
@Test
public void attach_whenViewEmitsName_shouldCallListener() {
  when(presenter.loginName()).thenReturn(Observable.just("fakename"));

  InteractorHelper.attach(interactor, presenter, router, null);
  verify(listener).login(any(String.class));
}

@Test
public void attach_whenViewEmitsEmptyName_shouldNotCallListener() {
  when(presenter.loginName()).thenReturn(Observable.just(""));

  InteractorHelper.attach(interactor, presenter, router, null);
  // This test will fail because the interactor doesn’t have any logic for handling empty strings. 
  // You’ll need to fix this as discussed above the code snippet.
  verify(listener, never()).login(any(String.class));
}
```

Now run the tests again. They should all pass.
```text
./gradlew :tutorials:tutorial2:testDebugUnitTest
```

## Wiring Up Login & TicTacToe
If you’ve gotten this far you’re doing great!

The rest of this code lab demonstrates viewless RIBs and contains additional practice hooking up listeners.

### Step 4: Attaching LoggedIn
We need to create the LoggedIn scope. Unlike all the other RIBs we’ve created so far this new RIB won’t contain any view or presenter. The LoggedIn scope doesn’t contain any UI. Its UI is entirely represented by the UI of its children.

We’ll create a riblet the same way we did in tutorial1 with one modification. Right click on the logged_in package, go to new, and click “New RIB…”. This time,  uncheck “Create presenter and view” in the presidio dialog.

Now that LoggedIn exists, we can finish our implementation of RootRouter. Create the following methods inside RootRouter. You’ll need to add create some new fields.

```java
void detachLoggedOut() {
  if (loggedOutRouter != null) {
    detachChild(loggedOutRouter);
    getView().removeView(loggedOutRouter.getView());
    loggedOutRouter = null;
  }
}

void attachLoggedIn() {
  attachChild(loggedInBuilder.build());
}
```

Notice that when you attach/detach the loggedOutRouter you need to both manipulate the router and the routers view. This gives us flexibility to create separate router and view hierarchies if we want. Similarly, notice that you don’t need to manipulate the LoggedInRouter’s view because it doesn’t have one.

If you try to build now the build will still fail. This is because the RootRouter is attempting to attach the LoggedInRouter but the RootComponent hasn’t yet declared that it knows how to satisfy the LoggedInComponent’s dependencies. Fix this by declaring that Root’s Component knows how to satisfy LoggedInBuilder’s dependencies.

```java
@RootScope
@dagger.Component(modules = {Module.class, OptionalModule.class},
  dependencies = ParentComponent.class)
interface Component extends
  InteractorBaseComponent<RootInteractor>,
  BuilderComponent,
  LoggedOutBuilder.ParentComponent,
+ LoggedInBuilder.ParentComponent {
```
If you build and install the app you’ll now see that you can login. However, LoggedIn doesn’t do anything yet.

### Step 5: LoggedIn Basics
Now that we’ve attached LoggedIn we want to make it do something. Right now, your RIB hierarchy looks like the following. Every RIB in the RIB tree exists, however OffGame and TicTacToe aren’t attached yet.

This section will be sparse on details for concepts you’ve already exercised above. The main focus for the rest of this code lab is understanding how to write a Router for a viewless RIB.

Implement the LoggedInInteractor. Create method stubs in the LoggedInRouter for now.

```java
@RibInteractor
public class LoggedInInteractor extends Interactor<EmptyPresenter, LoggedInRouter> {

  @Override
  protected void didBecomeActive(@Nullable Bundle savedInstanceState) {
    super.didBecomeActive(savedInstanceState);

    // when first logging in we should be in the OffGame state
    getRouter().attachOffGame();

  }

  class OffGameListener implements OffGameInteractor.Listener {

    @Override
    public void onStartGame() {
      getRouter().detachOffGame();
      getRouter().attachTicTacToe();
    }
  }
}
```

The LoggedIn RIB needs to declare that it knows how to handle it’s children's dependencies. Add the OffGame’s ParentComponent and TicTacToe’s ParentComponent to LoggedIn’s Component. 

You’ll also need to provide the OffGame’s listener inside LoggedInBuilder’s module.

```java
@LoggedInScope
@Provides
static OffGameInteractor.Listener offGameListener(LoggedInInteractor interactor) {
  return interactor.new OffGameListener();
}
```

Before moving forwards let’s verify that everything compiles.

### Step 6: LoggedIn Routing
Next, let’s write LoggedInRouter. If you try to attach OffGameRouter’s view directly inside LoggedInRouter’s view you’ll notice a problem: LoggedInRouter doesn’t have a view. 

Add a RootView to LoggedIn’s parent component.

Note: Normally you should use dependency inversion when passing a parent view into a child like this. Ie, LoggedIn should declare an interface called LoggedInParentView. This makes the RIB more reusable and allows for builder optimizations by splitting the app into multiple modules. But we’re pressed for time.

```java
public interface ParentComponent extends LoggedInOptionalExtension.ParentComponent {
  RootView rootView();
}
```

Update the provider for the LoggedInRouter inside LoggedInBuilder:

```java
@LoggedInScope
@Provides
static LoggedInRouter router(Component component, LoggedInInteractor interactor,
    RootView rootView) {
  return new LoggedInRouter(
      interactor,
      component,
      rootView,
      new OffGameBuilder(component),
      new TicTacToeBuilder(component));
}
```

Now let’s change RootRouter LoggedInRouter to support attaching OffGame. Notice that the offGamebuilder is passed RootView (so that OffGameBuilder’s view can inspect its parent views styling and layout constraints) OffGame’s view is attached into RootView. The OffGame router is attached into LoggedIn but OffGame’s view skips a level and is attached into RootView.

```java
void attachOffGame() {
  offGameRouter = offGameBuilder.build(rootView);
  attachChild(offGameRouter);
  rootView.addView(offGameRouter.getView());
}

void detachOffGame() {
  if (offGameRouter != null) {
    detachChild(offGameRouter);
    rootView.removeView(offGameRouter.getView());
    offGameRouter = null;
  }
}
```

As an exercise, write the attachers and detachers for TicTacToe.

The app now builds and runs but contains a memory leak. When the LoggedInRouter is detached its children RIBs will still have their views inside RootView. So we need the following explicit cleanup step.

```java
@Override
protected void willDetach() {
  super.willDetach();
  detachOffGame();
  detachTicTacToe();
}
```

### Step 7: Play a game of Tic Tac Toe
Build and install the game.
```text
./gradlew :tutorials:tutorial2:installDebug
```

You should now be able to login, see the OffGame RIB to start a game and fully play a full game of Tic Tac Toe.

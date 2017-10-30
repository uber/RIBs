# RIB Tutorial 1: Create a RIB

## Goal

This tutorial aims to illustrate how to create a nearly empty RIB using the Android Studio template.
Completing this tutorial should only take a few minutes. The app will look the following at the end.

<img src="https://github.com/uber/RIBs/blob/assets/tutorial_assets/android/tutorial_1_screenshot.png?raw=true" width="400">

## Exercise
Let's create a logged out screen for a Tic Tac Toe game. In order to do this, 
we'll need to do the following:
* Create a LoggedOutRIB using the Android Studio template
* Make the LoggedOutRIB to be a child of the existing RootRIB
* Modify LoggedOutRIB to show Views and handle clicks

This tutorial is mostly a matter of munging through boilerplate. This boilerplate will become useful as your flesh out larger applications and work farther through the tutorials. Fortunately for you, most of this boilerplate is generated for you.

## Steps
### Step 1
Let’s try installing the tutorial1 starter app to ensure your environment is up and running.
Use Android Studio or execute the following command: `./gradlew :tutorials:tutorial1:installDebug`

Yay! The application you are seeing contains nothing but the basic
scaffolding needed to run a RIB app. It contains a single RootActivity and a 
RootRib. All future code will be written nested under RootRib. RIB apps should avoid 
containing more than one activity since using multiple activities forces 
more state to exist inside a global scope. This reduces your ability to depend on invariants and increases the chances you'll accidentally break other code when making changes.

### Step 2
It is possible to write RIBs by hand. **But you don’t want to.** 
Let’s download the RIB Android Studio Template Plugin so that the boilerplate
can be generated for you.

In Android Studio, open Plugins > Install Plugins From Disk. Then install the
[plugin jar.](https://github.com/uber/RIBs/raw/master/android/tooling/rib-intellij-plugin/deploy/rib-intellij-plugin.jar)

### Step 3
Let’s generate the LoggedOut RIB. First, let's create a new package for each 
RIB. Since this tutorial is being built as a single module, let's nest the “logged_out”
package under “root”. This is a nice organizational structure since the LoggedOutRib will nest 
under the RootRib.

<img src="https://github.com/uber/RIBs/blob/master/android/tutorials/tutorial1-create-a-rib/tutorial_assets/logged_out.png?raw=true" width="600">

Right click the “logged_out” package and select New > New RIB

<img src="https://github.com/uber/RIBs/blob/master/android/tutorials/tutorial1-create-a-rib/tutorial_assets/create_rib.png?raw=true" width="600">

Create the LoggedOut RIB. You’ll now see the following files.

<img src="https://github.com/uber/RIBs/blob/master/android/tutorials/tutorial1-create-a-rib/tutorial_assets/logged_out_files.png?raw=true" width="600">

### Step 4
We want to hook up the LoggedOutRib so that it is attached whenever the RootRib 
is attached. In the future the RootRib will dynamically choose whether to attach 
and reattach the LoggedOutRib. In the meantime, let’s make the following changes to the RootRib:

#### RootBuilder
The RootBuilder’s Component should extend the LoggedOutBuilder.ParentComponent.
This configures dagger2 to generate code that satisfies LoggedOutBuilder’s dependency
injection requirements using classes available inside RootBuilder’s component.
You can’t attach LoggedOut below Root unless Root knows how to satisfy LoggedOut’s
dependencies (even though LoggedOut has no dependencies for the moment).

```java
  @RootScope
  @dagger.Component(
    modules = Module.class,
    dependencies = ParentComponent.class
  )
  interface Component extends 
      InteractorBaseComponent<RootInteractor>,
+      LoggedOutBuilder.ParentComponent,
      BuilderComponent {

    @dagger.Component.Builder
    interface Builder {
      // ...
    }
  }
```

Add LoggedOutBuilder as a constructor parameter to RootRouter. Change RootBuilder to provide the
LoggedOutBuilder when creating RootRouter. Like the following:

```java

    @RootScope
    @Provides
    static RootRouter router(Component component, RootView view, RootInteractor interactor) {
      return new RootRouter(view, interactor, component, new LoggedOutBuilder(component));
    }
```

#### RootRouter
Next, Create an attachLoggedOut() method inside RootRouter. This will allow the RootInteractor
to tell its Router to attach LoggedOut when it thinks this is appropriate. 

Technically, we could just perform this attachment inside RootRouter#didLoad(). But we want
to give the RootInteractor control over when this attachment occurs. In future tutorials RootInteractor
will be making decisions about when to perform the attachment.

```java
  void attachLoggedOut() {
    LoggedOutRouter router = loggedOutBuilder.build(getView());
    attachChild(router);
    getView().addView(router.getView());
  }
```

#### RootInteractor
RootInteractor should call getRouter().attachLoggedOut().

```java
  @Override
  protected void didBecomeActive(@Nullable Bundle savedInstanceState) {
    super.didBecomeActive(savedInstanceState);
    getRouter().attachLoggedOut();
  }
```

Let’s try building and installing the app. *You’ll notice it crashes* because there is more work 
to do (and because we don't have NullAway configured).

### Step 5
Examine the error output in Android Studio’s Android Monitor. You’ll see a NPE inside RootBuilder.
You haven’t yet hooked up your View! Let’s create a `logged_out_rib.xml` file under /res/layout/.
Create the XML layout with the following views:

```xml
<?xml version="1.0" encoding="utf-8"?>
<com.uber.rib.root.logged_out.LoggedOutView
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">
    <Space
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"/>
    <EditText
        android:id="@+id/edit_text"
        android:hint="Enter your name"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_margin="16dp"/>
    <Button
        android:id="@+id/login_button"
        android:text="Login"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_margin="16dp"/>
</com.uber.rib.root.logged_out.LoggedOutView>
```

In order for this view to work, you'll need to update the `LoggedOutView` to inherit from `FrameLayout`.

### Step 6
Let’s make the LoggedOut RIB do something: clicking on the Login button should log user’s name. First, configure the LoggedOutPresenter (nested inside LoggedOutInteractor) to have the following interface. 

```java
interface LoggedOutPresenter {
    Observable<String> loginName();
}
```

Strictly speaking presenter interfaces like this don't need to be used. You could just directly call your view from your interactor. In practice we've found this causes a cleaner seperation between views and interactors, for psychological reasons.

> **Note** RIBs aren't opinionated about whether you pass data from your view to your interactor via Rx or listener interfaces. Both have a variety of tradeoffs. For example one makes swapping views for a given interactor safer while one makes swapping interactors for a given view easier. One incurs less overhead. Which you find easier to test is subjective.

Inside LoggedOutInteractor, observe the LoggedOutPresenter and log its output.

```java
presenter
    .loginName()
    .subscribe(new Consumer<String>() {
        @Override
        public void accept(String name) throws Exception {
            Log.d("MOO", name);
        }
    }); 
```

Before you can build the app, you’ll need to implement the `LoggedOutPresenter` interface inside LoggedOutView. This requires doing two things:
1. Inside onFinishInflate() bind the LoggedOutView’s subviews into private fields. This should not be done in the constructor since the constructor is executed before subviews are attached to LoggedOutView. Alternatively, just call `findViewById` on demand.
2. Map the loginButton’s click into Observable<String> inside the loginName() method. You can take advantage of the observable methods inside UButton to implement this. Use a `map` operator.

Let’s reinstall the app and try tapping on the login button. You should see logs inside Android Monitor (or adb logcat). 

## You’re Done!
You now have a working LoggedOutRib! 

Next lesson we’ll build upon your current work by piping the login information back into the
RootRIB. This next lesson will cover more complex topics like Rx Streams, Unit Testing, DI and
interRIB listeners.

If you’re not happy with the code you wrote in this tutorial work you can start fresh next lesson
 by using the tutorial2 starter code.

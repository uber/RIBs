# RIB Tutorial 1: Create a RIB

##Goal

This tutorial aims to illustrate how to create a nearly empty RIB using the intellij template. 
Completing this tutorial should only take a few minutes.

##Exercise
Let's create a logged out screen for a Tic Tac Toe game. In order to do this, 
we'll need to do the following:
* Create a LoggedOutRIB using the Intellij template
* Make the LoggedOutRIB to be a child of the existing RootRIB
* Modify LoggedOutRIB to show Views and handle clicks

##Steps
####Step 1
Let’s try installing the lesson101 starter app to ensure your environment is up and running.
Use Android Studio or execute the following command:

--image--

Yay! The application you are seeing contains nothing but the basic
scaffolding needed to run a RIB app. It contains a single RootActivity and a 
RootRib. All future code will be written nested under RootRib. RIB apps should avoid 
containing more than one activity since using multiple activities forces 
more state to exist inside global scope. 

####Step 2
It is possible to write RIBs by hand. But you don’t want to. 
Let’s download the RIB Intellij Template Plugin so that the boilerplate
can be generated for you.

--TODO: add link to install the RIB template--

####Step 3
Let’s generate the LoggedOut RIB. First, let's create a new package for each 
RIB. Since this tutorial is being built as a single module, let's nest the “logged_out”
package under “root”. This is a nice organizational structure since the LoggedOutRib will nest 
under the RootRib.

--Image

Right click the “logged_out” package and select New > New RIB

--Image

Create the LoggedOut RIB

--Image

You’ll now see the following files.

--Image

####Step 4
We want to hook up the LoggedOutRib so that it is attached whenever the RootRib 
is attached. In the future the RootRib will dynamically choose whether to attach 
and reattach the LoggedOutRib. In the meantime, let’s make the following changes to the RootRib:

#####RootBuilder
The RootBuilder’s Component should extend the LoggedOutBuilder.ParentComponent.
This configures dagger2 to generate code that satisfies LoggedOutBuilder’s dependency
injection requirements using classes available inside RootBuilder’s component.
You can’t attach LoggedOut below Root unless Root knows how to satisfy LoggedOut’s
dependencies (even though LoggedOut has no dependencies for the moment).

--Image

Add LoggedOutBuilder as a constructor parameter to RootRouter. Change RootBuilder to provide the
LoggedOutBuilder when creating RootRouter. Like the following:

--Image

#####RootRouter
Next, Create an attachLoggedOut() method inside RootRouter. This will allow the RootInteractor
to tell its Router to attach LoggedOut when it thinks this is appropriate. 

Technically, we could just perform this attachment inside RootRouter#didLoad(). But we want
to give the RootInteractor control over when this attachment occurs. In the future RootInteractor
will be making decisions.

--Image

#####RootInteractor
RootInteractor should call getRouter().attachLoggedOut().

--Image

Let’s try building and installing the app. *You’ll notice it crashes* because there is more work 
to do (and because we don't have NullAway configured).

####Step 5
Examine the error output in IntelliJ’s Android Monitor. You’ll see a NPE inside RootBuilder.
You haven’t yet hooked up your View! Let’s create a logged_out_rib.xml file under /res/layout/.
Create the XML layout with the following views:


// Based off https://docs.google.com/document/d/165PEnt939yfQG7Nj0o6qWxyMby3nlTObJVaPv9jV0Dk/edit#

###You’re Done!
You now have a working LoggedOutRib! 

Next lesson we’ll build upon your current work by piping the login information back into the
RootRIB. This next lesson will cover more complex topics like Rx Streams, Unit Testing, DI and
interRIB listeners.

If you’re not happy with the code you wrote in this tutorial work you can start fresh next lesson
 by using the tutorial2 starter code.
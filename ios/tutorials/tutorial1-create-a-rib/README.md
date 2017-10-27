# RIB Tutorial 1: Create a RIB

## Goals of this exercise

The goal of this exercise is to understand the various pieces of a RIB, and more importantly, how they interact and communicate with each other. At the end of this exercise, we should have an app that launches into a screen where the user can type in player names and tap on a login button. Tapping the button should then print player names to the Xcode console.  
![image1](https://github.com/uber/ribs/blob/assets/tutorial_assets/ios/tutorial1-create-a-rib/images/image1.jpg)

## Create LoggedOut RIB

1. Select New File menu item on the LoggedOut group.  
![image2](https://github.com/uber/ribs/blob/assets/tutorial_assets/ios/tutorial1-create-a-rib/images/image2.jpg)  
2. Select the Xcode RIB templates.  
![image3](https://github.com/uber/ribs/blob/assets/tutorial_assets/ios/tutorial1-create-a-rib/images/image3.jpg)  
3. Name the new RIB "LoggedOut" and check the "Owns corresponding view" checkbox.  
    ![image4](https://github.com/uber/ribs/blob/assets/tutorial_assets/ios/tutorial1-create-a-rib/images/image4.jpg)  
    1. LoggedOut RIB has its own view to display a "Sign Up" button and player name text fields.  
4. Create the files and make sure it’s in the "TicTacToe" target and in the "LoggedOut" folder.  
![image5](https://github.com/uber/ribs/blob/assets/tutorial_assets/ios/tutorial1-create-a-rib/images/image5.jpg)  
5. Now we can delete the DELETE_ME.swift file. It was only temporarily needed so the project can compile without the LoggedOut RIB we just created.  
![image6](https://github.com/uber/ribs/blob/assets/tutorial_assets/ios/tutorial1-create-a-rib/images/image6.jpg)

### Understanding generated code

![image7](https://github.com/uber/ribs/blob/assets/tutorial_assets/ios/tutorial1-create-a-rib/images/image7.jpg)

* LoggedOutBuilder conforms to LoggedOutBuildable so other RIBs that uses the builder can use a mocked instance that conforms to the buildable protocol.
* LoggedOutInteractor uses the protocol LoggedOutRouting to communicate with its router. This is based on the dependency inversion principle where the interactor declares what it needs, and some other unit, in this case the LoggedOutRouter, provides the implementation. Similar to the buildable protocol, this allows the interactor to be unit tested. LoggedOutPresentable is the same concept that allows the interactor to communicate with the view controller.
* LoggedOutRouter declares what it needs in LoggedOutInteractable to communicate with its interactor. It uses the LoggedOutViewControllable to communicate with the view controller.
* LoggedOutViewController uses LoggedOutPresentableListener to communicate with its interactor following the same dependency inversion principle.

### LoggedOut UI

Below is the UI we want to build. To save time, you can also use the provided UI code [here](https://github.com/uber/ribs/blob/assets/tutorial_assets/ios/tutorial1-create-a-rib/source/source1.swift?raw=true).

![image1](https://github.com/uber/ribs/blob/assets/tutorial_assets/ios/tutorial1-create-a-rib/images/image1.jpg)

### Login Logic

LoggedOutViewController calls to its listener, LoggedOutPresentableListener, to perform the business logic of login, by passing in the player 1 and 2 names.

```swift
protocol LoggedOutPresentableListener: class {
    func login(withPlayer1Name player1Name: String?, player2Name: String?)
}
```

* Notice that both player names are optional, since the user may not have entered anything. We could disable the Login button until both names are entered, but for this exercise, we’ll let the LoggedOutInteractor deal with the business logic of handling nil names.
* If player names are empty, we default to "Player 1" and "Player 2".

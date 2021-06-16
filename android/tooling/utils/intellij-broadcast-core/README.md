# Intellij Broadcast Core Debugging tool

This Android library enables communicating between Intellij plugin(s) and applications running on Android device.

It does so via a custom broadcast intent receiver, which writes serialized chunked response to standard logcat in
response to broadcast commands emitted by IntelliJ plugin. IntelliJ plugin also queries and aggregates logcat logs, to
format and deserialize device's response.

Practically, this enables 2-way communication between Intellij plugin and Android device. We are using it, for example,
to query RIBs hierarchy data from running application and display it in IntelliJ.

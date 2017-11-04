# rib-android-test

This module exists so that we can write roboelectric tests for rib-android without
needed to set a default theme on the rib-android. This makes consuming the rib-android
module easier. No one needs to set `tools:replace="android:theme"`.

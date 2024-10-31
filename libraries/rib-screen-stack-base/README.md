# Rib Screen Stack Base

This module defines the interface for a `ScreenStack` utility. This
utility can be used when you want to push an entirely new "Screen".

Visually, this acts similarly to pushing a new activity. However, the pushed RIB
becomes a child of the existing RIB in the RIB hierarchy. As a result it can
access state from its parent's DI graph.

Depending on the amount of animation customization provided, this utility
can be quite easy to implement.

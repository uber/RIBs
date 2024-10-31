# rib-compiler-test

Annotation processor that makes writing RIB tests easier. To simplify issues with
some build systems this generates the test utilities directory into the src source set
instead of the test source set. Not a big deal: test utilities will get stripped out
by proguard.

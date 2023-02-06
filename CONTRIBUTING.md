# Contributing

Fork this repository. Make your changes, tests it and provide a pull-request. That`s it.

The developers must coordinate changes with lockable-resource-plugin. This is very important, otherwise
it might happens, that this library will be no more compatible. This is our vision (or mission).

## Releasing

Current strategy is really simple. The release is the last state from **master** branch.
The end-user need to checkout and add this repository into Jenkins shared-library.
Therefore zip packages, makes not really sense.

<!--Comment:
  Automated releases shall be an hot topic. Provide more information here, when it works.
-->

## Git workflow

To eliminate weird and often changes in master branch we provide long time **develop** branch.
That means all changes (feature requests, bug fixing) must be merged in to **develop** branch
and not into **master**.
The branch **develop** will be merged in to master on demand.

```
                             R1              R2               Rn
--- master ----------------------------------------- ... ------------
            \--- develop ---/\--- develop ---/\--- develop ---/
              \-feature1-/ /     /
              \-feature2--/     /
                    \-feature3-/
```

## Setup environment

TBD

## Build and test

Be sure, your changes are tested with last public lockable-resource-plugin release.

TBD

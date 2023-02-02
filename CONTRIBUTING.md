# Contributing

Fork this repository. Make your changes, tests it and provide a pull-request. That`s it.

## Git workflow

To keep it simply we do not provide releases. It makes no sense, because the end-user need to checkout add this repository in Jenkins as shared-library. Therefore our release is the last state from **master** branch.

To eliminate weird and often changes in master branch we provide long time **develop** branch.
That means all changes must be merged in to **develop** branch and not to **master**. The branch **develop** will be merged in to master on demand.

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

TBD

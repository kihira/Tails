[![Build Status](https://dev.azure.com/kihira/Tails/_apis/build/status/kihira.Tails?branchName=develop)](https://dev.azure.com/kihira/Tails/_build/latest?definitionId=3&branchName=develop)
# Tails
A small mod that adds in a variety of tails

# Build and setup
The standard build task will generate a shaded and non shaded version, with the non shaded having the `nonshaded` classifier
Before it can be built however, a workspace must be setup.

### Workspace
To setup a workspace for general develop run `gradle setupDecompWorkspace`
This will automatically download all required dependencies

To setup a workspace for CI building, run `gradle setupCIWorkspace`

### Building
The standard build can be run by running `gradle build` which will output a shaded and nonshaded jar including sources.

To only generate a `nonshaded` jar, run `gradle reobfJar`  
To generate only a `sources` jar, run `gradle sourcejar`  
To generate only a `shaded` jar, run `gradle reobfShadowJar`  

# Maven 
A maven repository is available at http://maven.foxes.rocks/

To include this as a dependency in your project, add the following replacing `<version>` with the version you want.
```
repositories {
    maven {
        name = 'Kihira Maven'
        url = 'http://maven.foxes.rocks'
    }
}
dependencies {
    deobfCompile "uk.kihira.tails:Tails:<version>:nonshaded"
}
```

# Submitted PR's
I use a git flow system so master is used for releases, develop for development etc. You can read more about it [here](http://nvie.com/posts/a-successful-git-branching-model/) or a quick cheatsheet [here](https://danielkummer.github.io/git-flow-cheatsheet/)

Basically if you want to submit a language patch, submit it against the develop branch. Read [here](https://help.github.com/articles/using-pull-requests#changing-the-branch-range-and-destination-repository) on how to change branches when submitting a PR

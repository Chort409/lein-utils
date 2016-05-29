# lein-utils

A [Leiningen][] plugin for generating utils.clj file
When starting to develop a new project noticed that have to cope over some utils functions over and over again.
Option one will be just to create a shared project where to dump all utils 
functions but this will bring many functions that project don't need and it will be problem to modify it. 
So the idea was to create a plugin that will download utils functions and made the part of the new project

[Leiningen]: https://github.com/technomancy/leiningen

## Installation

work in progress 

## Usage

```
lein utils <functionname>
```
## Future work
Currently all function have to be submitted to the recourses file to the /sources file in *.md file. The plan is to upgrade it to use functions from .clj files. That will make it easier to commit and to test them.

## How to contribute
Simple submit your pull request with functions that you think might be useful

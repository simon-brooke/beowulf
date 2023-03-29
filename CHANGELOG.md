# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [0.2.1] - 2023-03-??

### Changed
- this is fundamentally a working Lisp. The reader reads S-Expressions fully and M-Expressions at least partially. It is not (yet) a feature complete Lisp 1.5.

### Added
- working EVAL, APPLY, READ and 24 other basic functions, of which at least four are not actually parts of the Lisp 1.5 specification. However, sufficient are present to allow the
vast majority of Lisp 1.5 functions to be defined.

### Known to be missing
- property lists.

[Unreleased]: https://github.com/your-name/beowulf/compare/0.1.1...HEAD
[0.1.1]: https://github.com/your-name/beowulf/compare/0.1.0...0.1.1

# kibit-cljs

*There's a function for that!*

`kibit-cljs` is a clojurescript port of the ['kibit'](https://github.com/jonase/kibit) static code analyzer for Clojure(script).
It's currently a work in progress, but is generally functional for analysing single sexprs at a time (see documentation below).

Note the very old version of Clojurescript being used. The first use I had for this was a ['Lighttable'](http://lighttable.com) plugin, which as of this commit, is still running on the same older version.

## Usage

In leiningen:

```[kibit-cljs "0.1.0"]```

```
(require '[kibit.check :as kibit])

(kibit/check-expr '(+ x 1))

;; => {:expr (+ x 1), :line nil, :column nil, :alt (inc x)}

```

## Tests
Are still being ported. I wouldn't try and run them at the moment.

## License

Copyright © 2012 Jonas Enlund

Distributed under the Eclipse Public License, the same as Clojure.

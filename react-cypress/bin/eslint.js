const execa = require('execa')
const cliColor = require('cli-color')
const chokidar = require('chokidar')
const path = require('path')
const { exhaustMapWithTrailing } = require('rxjs-exhaustmap-with-trailing')
const { catchError, concatMap, EMPTY, from, of, ReplaySubject, tap, delay, timer } = require('rxjs')

async function main() {
  const changeSubject = new ReplaySubject(1);

  const chokidarWatcher = chokidar.watch([
    path.join(__dirname, "..", "cypress", "e2e"),
    path.join(__dirname, "..", "cypress", "page"),
    path.join(__dirname, "..", "cypress", "action"),
  ]);

  chokidarWatcher.on('all', () => {
    changeSubject.next(null);
  });

  await timer(1000).toPromise();

  changeSubject.pipe(
    exhaustMapWithTrailing(() =>
      of(null).pipe(
        delay(100),
        tap(() => {
          console.clear()
        }),
        concatMap(() => from(execa.command("eslint cypress", {
          stdio: "inherit",
          cwd: path.join(__dirname, ".."),
          extendEnv: true,
        }))),
        tap(() => {
          console.log(cliColor.green("\nCompilation complete!"));
        }),
        catchError(() => EMPTY)
      )
    )
  ).subscribe();
}

module.exports = main()
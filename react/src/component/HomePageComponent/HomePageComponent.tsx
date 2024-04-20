import logo from '@/component/Home/image/logo.svg';
import { FormattedMessage } from "react-intl";
import { Link } from "react-router-dom";
import { Button, Link as MaterialLink, CircularProgress } from "@mui/material";
import { keyframes, stylesheet } from 'typestyle';
import { useMobxState, observer, useMount } from 'mobx-react-use-autorun';
import { concatMap, from, of, repeat, timer } from 'rxjs';

const css = stylesheet({
  container: {
    textAlign: "center",
  },
  header: {
    backgroundColor: "#282c34",
    minHeight: "100vh",
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
    fontSize: "calc(10px + 2vmin)",
    color: "white",
  },
  img: {
    height: "40vmin",
    pointerEvents: "none",
    animationName: keyframes({
      "from": {
        transform: "rotate(0deg)"
      },
      "to": {
        transform: "rotate(360deg)",
      }
    }),
    animationDuration: "20s",
    animationIterationCount: "infinite",
    animationTimingFunction: "linear",
  },
  batteryContainer: {
    color: "#61dafb",
    display: "flex",
    flexDirection: "column",
  }
})

export default observer(() => {

  const state = useMobxState({
    randomNumber: null as number | null,
    people: {
      name: "",
    },
  })

  useMount((subscription) => {
    /* Generate random number */
    subscription.add(of(null).pipe(
      concatMap(() => from((async () => {
        while (true) {
          const numberOne = Math.floor(Math.random() * 100 + 1);
          if (state.randomNumber !== numberOne) {
            state.randomNumber = numberOne;
            break;
          }
          await timer(0).toPromise();
        }
      })())),
      repeat({ delay: 1000 }),
    ).subscribe());
  })

  return (
    <div
      className={css.container}
    >
      <header
        className={css.container}
      >
        <img
          src={logo}
          className={css.img} alt="logo" />
        <div className="flex">
          <FormattedMessage id="EditSrcAppTsxAndSaveToReload" defaultMessage="Edit src/App.tsx and save to reload" />
          {"."}
        </div>
        <div
          className={css.batteryContainer}
        >
          {
            state.randomNumber !== null ? (<div>
              <div>
                <FormattedMessage id="RandomNumber" defaultMessage="Random number" />
                {": " + (state.randomNumber!) + "%"}
              </div>
            </div>) : (
              <CircularProgress />
            )
          }
          <div>
            <Link to="/not_found" className="no-underline" >
              <MaterialLink underline="hover" component="div" >
                <Button variant="text" color="primary" style={{ marginTop: "1em", fontSize: "large", paddingTop: "0", paddingBottom: "0" }}>
                  <FormattedMessage id="GoToTheUnknownArea" defaultMessage="Go to the unknown area" />
                </Button>
              </MaterialLink>
            </Link>
          </div>
        </div>
      </header>
    </div>
  );
})
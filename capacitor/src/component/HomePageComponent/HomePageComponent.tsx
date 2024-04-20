import logo from '@/component/HomePageComponent/image/logo.svg';
import { FormattedMessage } from "react-intl";
import { Link } from "react-router-dom";
import { Button, Link as MaterialLink } from "@mui/material";
import { keyframes, stylesheet } from 'typestyle';
import { useBatteryInfo } from '@/component/HomePageComponent/js/useBatteryInfo';
import CircularProgress from '@mui/material/CircularProgress';
import { observer, useMobxState } from 'mobx-react-use-autorun';
import GameDialog from '@/component/Game/GameDialog';
import { App } from '@capacitor/app'

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
  },
  divContainer: {
    display: "flex",
    flexDirection: "column",
  }
})

export default observer(() => {

  const state = useMobxState({
    gameDialog: {
      open: false,
    },
  }, {
    batteryInfo: useBatteryInfo(),
  });

  return (<>
    <div
      className={css.container}
    >
      <header
        className={css.header}
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
            state.batteryInfo ? (<div>
              <div>
                {state.batteryInfo.isCharging ? (
                  <FormattedMessage id="CurrentlyCharging" defaultMessage="当前正在充电" />
                ) : (
                  <FormattedMessage id="CurrentlyNotCharging" defaultMessage="当前没有充电" />
                )}
              </div>
              <div>
                <FormattedMessage id="CurrentBattery" defaultMessage="当前电量" />
                {":" + Math.round(state.batteryInfo.batteryLevel! * 100) + "%"}
              </div>
            </div>) : (
              <CircularProgress />
            )
          }
          <div className={css.divContainer}>
            <Link to="/not_found" replace={true} className="no-underline" >
              <MaterialLink underline="hover" component="div" >
                <Button variant="text" color="primary" style={{ marginTop: "1em", fontSize: "large", paddingTop: "0", paddingBottom: "0" }}>
                  <FormattedMessage id="toUnknownArea" defaultMessage="去未知地区" />
                </Button>
              </MaterialLink>
            </Link>
            <MaterialLink underline="hover" component="div" onClick={async () => {
              state.gameDialog.open = true;
            }} >
              <Button variant="text" color="primary" style={{ marginTop: "1em", fontSize: "large", paddingTop: "0", paddingBottom: "0" }}>
                <FormattedMessage id="EnterTheGameRightNowWithoutDoingTheExitButton" defaultMessage="Enter the game, right now, without doing the exit button" />
              </Button>
            </MaterialLink>
            <Button
              variant="contained"
              style={{
                marginTop: "1em"
              }}
              onClick={async () => {
                await App.exitApp()
              }}
            >
              <FormattedMessage id="ExitTheApp" defaultMessage="Exit the app" />
            </Button>
          </div>
        </div>
      </header>
    </div>
    {state.gameDialog.open && <GameDialog closeDialog={() => {
      state.gameDialog.open = false;
    }} />}
  </>);
})

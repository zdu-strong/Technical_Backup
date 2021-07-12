import logo from './image/logo.svg';
import { FormattedMessage } from "react-intl";
import { Link } from "react-router-dom";
import { Button, Link as LinkAlias } from "@mui/material";
import { keyframes, style } from 'typestyle';
import { useBatteryInfo } from './js/useBatteryInfo';
import CircularProgress from '@mui/material/CircularProgress';
import { observer, useAsLocalSource } from 'mobx-react-use-autorun';

export default observer(() => {

    const source = useAsLocalSource({
        batteryInfo: useBatteryInfo()
    });

    return (
        <div
            className={style({
                textAlign: "center",
            })}
        >
            <header
                className={style({
                    backgroundColor: "#282c34",
                    minHeight: "100vh",
                    display: "flex",
                    flexDirection: "column",
                    alignItems: "center",
                    justifyContent: "center",
                    fontSize: "calc(10px + 2vmin)",
                    color: "white",
                })}
            >
                <img
                    src={logo}
                    className={style({
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
                    })} alt="logo" />
                <div className="flex">
                    <FormattedMessage id="EditSrcAppTsxAndSaveToReload" defaultMessage="Edit src/App.tsx and save to reload" />
                    {"."}
                </div>
                <div
                    className={`${style({
                        color: "#61dafb",
                    })} flex flex-col`}
                >
                    {
                        source.batteryInfo ? (<div>
                            <div>
                                {source.batteryInfo.isCharging ? (
                                    <FormattedMessage id="CurrentlyCharging" defaultMessage="当前正在充电" />
                                ) : (
                                    <FormattedMessage id="CurrentlyNotCharging" defaultMessage="当前没有充电" />
                                )}
                            </div>
                            <div>
                                <FormattedMessage id="CurrentBattery" defaultMessage="当前电量" />
                                {":" + Math.round(source.batteryInfo.batteryLevel! * 100) + "%"}
                            </div>
                        </div>) : (
                            <CircularProgress />
                        )
                    }
                    <div>
                        <Link to="/not_found" className="no-underline" >
                            <LinkAlias underline="hover" component="div" >
                                <Button variant="text" color="primary" style={{ textTransform: "none", marginTop: "1em", fontSize: "large", paddingTop: "0", paddingBottom: "0" }}>
                                    {"去未知地区"}
                                </Button>
                            </LinkAlias>
                        </Link>
                    </div>
                </div>
            </header>
        </div>
    );
})

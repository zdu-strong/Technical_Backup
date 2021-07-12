import logo from './image/logo.svg';
import { FormattedMessage } from "react-intl";
import { Link } from "react-router-dom";
import BootLoadingComponent from "./BootLoadingComponent";
import { Button, Link as LinkAlias } from "@mui/material";
import { keyframes, style } from 'typestyle';
import { observer, useAsLocalSource } from 'mobx-react-use-autorun';
import { useCpuUsage } from './js/useCpuUsage';
import { useReadyForApplication } from './js/useReadyForApplication';
import CircularProgress from '@mui/material/CircularProgress';

export default observer(() => {

    const source = useAsLocalSource({
        cpuUsage: useCpuUsage(),
        ready: useReadyForApplication(),
    })

    return (<>
        {!source.ready && BootLoadingComponent}
        {source.ready && <div
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
                        source.cpuUsage !== null ? (<div className="flex flex-col">
                            <div className="text-center">
                                <FormattedMessage id="CurrentCpuUsage" defaultMessage="当前cpu使用率" />
                                {":" + Math.round(source.cpuUsage!) + "%"}
                            </div>
                        </div>) : (
                            <div className="flex flex-row justify-center">
                                <CircularProgress />
                            </div>
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
        </div>}
    </>);
})
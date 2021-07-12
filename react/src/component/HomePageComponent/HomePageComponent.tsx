import logo from './image/logo.svg';
import { FormattedMessage } from "react-intl";
import { Link } from "react-router-dom";
import { Button, Link as LinkAlias, CircularProgress } from "@mui/material";
import { keyframes, style } from 'typestyle';
import { timeout, useLocalObservable, observer } from 'mobx-react-use-autorun';
import { useInterval } from 'react-use';

export default observer(() => {

    const state = useLocalObservable(() => ({
        randomNumber: null as number | null,
        people: {
            name: "",
        }
    }));

    /* 产生随机数 */
    useInterval(async () => {
        while (true) {
            const numberOne = Math.floor(Math.random() * 100 + 1);
            if (state.randomNumber !== numberOne) {
                state.randomNumber = numberOne;
                break;
            }
            await timeout(0);
        }
    }, 1000)

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
                        state.randomNumber !== null ? (<div>
                            <div>
                                <FormattedMessage id="RandomNumber" defaultMessage="随机数" />
                                {": " + (state.randomNumber!) + "%"}
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
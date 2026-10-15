import logo from '@component/Home/image/logo.svg';
import { FormattedMessage } from "react-intl";
import { Button } from "@mui/material";
import { keyframes, style } from 'typestyle';
import { useMobxState, observer } from 'mobx-react-use-autorun';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faWater } from '@fortawesome/free-solid-svg-icons';
import { Link } from 'react-router-dom';
import { useRandomNumber } from '@component/Home/js/useRandomNumber';

const container = style({
    textAlign: "center",
    display: "flex",
    flex: "1 1 auto",
    flexDirection: "column",
});

const headerCss = style({
    backgroundColor: "#282c34",
    minHeight: "100vh",
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
    fontSize: "calc(10px + 2vmin)",
    color: "white",
});

const imgAnimation = style({
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
});

const batteryContainer = style({
    color: "#61dafb",
    display: "flex",
    flexDirection: "column",
});

export default observer(() => {

    const state = useMobxState({
        people: {
            name: "",
        },
    }, {
        randomNumber: useRandomNumber(),
    })

    return (
        <div
            className={container}
        >
            <header
                className={headerCss}
            >
                <img
                    src={logo}
                    className={imgAnimation} alt="logo" />
                <div className="flex">
                    <FormattedMessage id="EditSrcAppTsxAndSaveToReload" defaultMessage="Edit src/App.tsx and save to reload" />
                    {"."}
                </div>
                <div
                    className={batteryContainer}
                >
                    <div>
                        <div>
                            <FormattedMessage id="RandomNumber" defaultMessage="Random number" />
                            {`:  ${state.randomNumber}`}
                        </div>
                    </div>
                    <div>
                        <Link to="/not_found">
                            <Button
                                variant="contained"
                                startIcon={<FontAwesomeIcon icon={faWater} />}
                            >
                                <FormattedMessage id="GoToTheUnknownArea" defaultMessage="Go to the unknown area" />
                            </Button>
                        </Link>
                    </div>
                </div>
            </header>
        </div>
    );
})
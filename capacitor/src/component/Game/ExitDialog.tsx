import { Button, Dialog, DialogActions, DialogTitle, Fab } from "@mui/material";
import { observer } from "mobx-react-use-autorun";
import { FormattedMessage } from "react-intl";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faXmark } from '@fortawesome/free-solid-svg-icons'
import { App } from '@capacitor/app'
import { Capacitor } from "@capacitor/core";

export default observer((props: { closeDialog: () => void, exit: () => void }) => {

  function endGame() {
    props.closeDialog();
    props.exit();
  }

  async function exitApp() {
    if (Capacitor.getPlatform() === "web") {
      endGame();
      return;
    }
    await App.exitApp();
  }

  return <Dialog
    open={true}
    onClose={props.closeDialog}
    aria-labelledby="alert-dialog-title"
    aria-describedby="alert-dialog-description"
  >
    <DialogTitle
      id="alert-dialog-title"
      style={{
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
      }}
    >
      <div style={{ fontWeight: "bold" }}>
        <FormattedMessage id="Quit" defaultMessage="Quit?" />
      </div>
      <Fab size="small" color="default" onClick={props.closeDialog}>
        <FontAwesomeIcon icon={faXmark} size="xl" />
      </Fab>
    </DialogTitle>
    <DialogActions>
      <Button
        onClick={endGame}
        variant="contained"
        style={{ marginLeft: "1em" }}
        color="primary"
      >
        <FormattedMessage id="EndTheGame" defaultMessage="End Game" />
      </Button>
      <Button
        variant="contained"
        style={{ marginRight: "1em" }}
        onClick={exitApp}
        color="secondary"
      >
        <FormattedMessage id="ExitTheApp" defaultMessage="Exit" />
      </Button>
    </DialogActions>
  </Dialog>
})
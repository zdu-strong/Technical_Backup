import { Button, Dialog, DialogActions, DialogTitle, Fab } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import { FormattedMessage } from "react-intl";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faXmark } from '@fortawesome/free-solid-svg-icons'
import { App } from '@capacitor/app'

export default observer((props: { closeDialog: () => void, exit: () => void }) => {

  const state = useMobxState({
  }, {
    ...props
  })

  return <Dialog
    open={true}
    onClose={state.closeDialog}
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
      <Fab size="small" color="default" onClick={state.closeDialog}>
        <FontAwesomeIcon icon={faXmark} size="xl" />
      </Fab>
    </DialogTitle>
    <DialogActions>
      <Button
        onClick={() => {
          state.closeDialog();
          state.exit();
        }}
        variant="contained"
        style={{ marginLeft: "1em" }}
        color="primary"
      >
        <FormattedMessage id="EndTheGame" defaultMessage="End Game" />
      </Button>
      <Button
        variant="contained"
        style={{ marginRight: "1em" }}
        onClick={() => {
          App.exitApp()
        }}
        color="secondary"
      >
        <FormattedMessage id="ExitTheApp" defaultMessage="Exit" />
      </Button>
    </DialogActions>
  </Dialog>
})
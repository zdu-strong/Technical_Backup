import remote from "@/remote";
import { faXmark } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Button, Dialog, DialogActions, DialogTitle, Fab } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import { FormattedMessage } from "react-intl";

export default observer((props: {
  closeDialog: () => void,
  exit: () => void,
  canvasRef: React.RefObject<HTMLCanvasElement>
}) => {

  const state = useMobxState({}, {
    ...props
  })

  return <Dialog
    open={true}
    onClose={async () => {
      state.closeDialog();
      await Promise.resolve(null);
      state.canvasRef.current!.focus();
    }}
    disableRestoreFocus={true}
  >
    <DialogTitle
      id="alert-dialog-title"
      style={{
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
      }}
    >
      <div style={{ fontWeight: "bold", marginRight: "2em" }}>
        <FormattedMessage id="AreYouSure" defaultMessage="Are you sure?" />
      </div>
      <Fab size="small" color="default" onClick={async () => {
        state.closeDialog();
        await Promise.resolve(null);
        state.canvasRef.current!.focus();
      }}>
        <FontAwesomeIcon icon={faXmark} size="xl" />
      </Fab>
    </DialogTitle>
    <DialogActions className="flex justify-end">
      <Button
        onClick={() => {
          state.closeDialog();
          state.exit();
        }}
        variant="contained"
        style={{ textTransform: "none", marginLeft: "1em" }}
        color="primary"
      >
        <FormattedMessage id="EndTheGame" defaultMessage="End Game" />
      </Button>
      <Button
        variant="contained"
        style={{ textTransform: "none", marginRight: "1em" }}
        onClick={() => {
          remote.app.exit()
        }}
        color="secondary"
      >
        <FormattedMessage id="ExitTheProgram" defaultMessage="Exit" />
      </Button>
    </DialogActions>
  </Dialog>
})
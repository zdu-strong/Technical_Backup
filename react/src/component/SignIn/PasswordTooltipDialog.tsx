import { Dialog, DialogContent, DialogContentText, DialogTitle, Divider, Fab } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faXmark } from '@fortawesome/free-solid-svg-icons'
import { FormattedMessage } from "react-intl";

export default observer((props: {
  closeDialog: () => void,
}) => {

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
    <DialogTitle id="alert-dialog-title" className="flex flex-row justify-between items-center">
      <div>
        <FormattedMessage id="PasswordHint" defaultMessage="Password hint" />
      </div>
      <Fab color="primary" size="small" aria-label="add" onClick={state.closeDialog} >
        <FontAwesomeIcon icon={faXmark} size="xl" />
      </Fab>
    </DialogTitle>
    <Divider />
    <DialogContent>
      <DialogContentText id="alert-dialog-description">
        <FormattedMessage id="JustLikeTheTreasureMapLetUsHideThePasswordInThisWorld" defaultMessage="Just like the treasure map, let's hide the password in this world. For example, select a paragraph as a password from Shakespeare's works." />
      </DialogContentText>
    </DialogContent>
  </Dialog>
})
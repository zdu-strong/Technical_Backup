import { faXmark } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Dialog, DialogContent, DialogTitle, Divider, Fab } from "@mui/material";
import { observer } from "mobx-react-use-autorun";

export default observer((props: {
  id: string,
  searchByPagination: () => void,
  closeDialog: () => void,
}) => {

  function closeDialog(event: {}, reason: "backdropClick" | "escapeKeyDown") {
    if (reason === "backdropClick") {
      return;
    }
    props.closeDialog();
  }

  return <>
    <Dialog
      open={true}
      onClose={closeDialog}
      disableRestoreFocus={true}
      fullWidth={true}
    >
      <DialogTitle className="justify-between items-center flex-row flex-auto flex">
        <div className="flex flex-row items-center" >
          {"用户详情"}
        </div>
        <Fab color="default" id="closeButton" onClick={props.closeDialog}>
          <FontAwesomeIcon icon={faXmark} size="xl" />
        </Fab>
      </DialogTitle>
      <Divider />
      <DialogContent style={{ padding: "1em" }}>
        {"用户详细信息"}
      </DialogContent>
    </Dialog>
  </>
})
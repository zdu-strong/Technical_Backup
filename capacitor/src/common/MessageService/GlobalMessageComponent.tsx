import { Alert, Dialog, DialogContent, DialogTitle, Divider, Fab, IconButton } from "@mui/material"
import { GlobalMessageList, MessageService, MESSAGE_TYPE_ENUM } from "@/common/MessageService"
import linq from "linq";
import { observer } from 'mobx-react-use-autorun';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCircleQuestion, faXmark } from '@fortawesome/free-solid-svg-icons'
import Tooltip from '@mui/material/Tooltip';
import { stylesheet } from "typestyle";
import { FormattedMessage } from "react-intl";

const css = stylesheet({
  alertMessageContent: {
    $nest: {
      '.MuiAlert-message': {
        fontSize: "large",
        fontWeight: 'bold',
        alignItems: "center",
        display: "flex"
      },
      '.MuiAlert-icon': {
        alignItems: "center",
        fontSize: "x-large"
      }
    }
  }
})

export default observer(() => {

  function getMessageType() {
    if (GlobalMessageList.length > 0) {
      const type = linq.from(GlobalMessageList).select(s => s.type).first();
      if (type === MESSAGE_TYPE_ENUM.warning) {
        return MESSAGE_TYPE_ENUM.warning;
      } else if (type === MESSAGE_TYPE_ENUM.info) {
        return MESSAGE_TYPE_ENUM.info;
      } else if (type === MESSAGE_TYPE_ENUM.success) {
        return MESSAGE_TYPE_ENUM.success;
      }
    }
    return MESSAGE_TYPE_ENUM.error;
  }

  function getMessageContentTextColor() {
    if (GlobalMessageList.length > 0) {
      const type = linq.from(GlobalMessageList).select(s => s.type).first();
      if (type === MESSAGE_TYPE_ENUM.warning) {
        return '#ff9800';
      } else if (type === MESSAGE_TYPE_ENUM.info) {
        return '#03a9f4';
      } else if (type === MESSAGE_TYPE_ENUM.success) {
        return '#4caf50';
      }
    }
    return "#ef5350";
  }

  function getTitleTextColor() {
    if (GlobalMessageList.length > 0) {
      const type = linq.from(GlobalMessageList).select(s => s.type).first();
      if (type === MESSAGE_TYPE_ENUM.warning) {
        return '#ed6c02';
      } else if (type === MESSAGE_TYPE_ENUM.info) {
        return '#0288d1';
      } else if (type === MESSAGE_TYPE_ENUM.success) {
        return '#2e7d32';
      }
    }
    return "#d32f2f";
  }

  return <>
    {GlobalMessageList.length > 0 && <Dialog
      open={true}
      onClose={() => {
        MessageService.error([])
      }}
      disableRestoreFocus={true}
      fullWidth={true}
    >
      <DialogTitle className="justify-between items-center flex-row flex-auto flex">
        <div className="flex flex-row items-center" style={{ color: getTitleTextColor() }}>
          {getMessageType() === MESSAGE_TYPE_ENUM.error && 'Error'}
          {getMessageType() === MESSAGE_TYPE_ENUM.success && 'Success'}
          {getMessageType() === MESSAGE_TYPE_ENUM.warning && 'Warning'}
          {getMessageType() === MESSAGE_TYPE_ENUM.info && 'Info'}
          <Tooltip
            title={<div className="flex flex-col">
              <div>
                <FormattedMessage id="TheWayToCloseThePopUpBox" defaultMessage="The way to close the pop -up box" />
                {": "}
              </div>
              <div>
                {"1. "}
                <FormattedMessage id="PressTheESCKey" defaultMessage="Press the ESC key" />
              </div>
              <div>
                {"2. "}
                <FormattedMessage id="ClickTheCloseButton" defaultMessage="Click the close button" />
              </div>
              <div>
                {"3. "}
                <FormattedMessage id="ClickTheBackgroundBoard" defaultMessage="Click the background board" />
              </div>
            </div>}
            arrow={true}
          >
            <IconButton color={getMessageType()} style={{ marginLeft: "4px" }}>
              <FontAwesomeIcon icon={faCircleQuestion} />
            </IconButton>
          </Tooltip>
        </div>
        <Fab color="default" id="closeButton" onClick={() => MessageService.error([])}>
          <FontAwesomeIcon icon={faXmark} size="xl" />
        </Fab>
      </DialogTitle>
      <Divider />
      <DialogContent style={{ padding: "1em" }}>
        {GlobalMessageList.map(messsageObject =>
          <Alert severity={messsageObject.type} className={css.alertMessageContent} key={messsageObject.id} style={{ marginTop: "1em", color: getMessageContentTextColor(), whiteSpace: "pre-wrap", wordBreak: "break-word", wordWrap: "break-word" }}>
            {messsageObject.message}
          </Alert>
        )}
      </DialogContent>
    </Dialog>}
  </>
})
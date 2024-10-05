import { observer, useMobxState } from "mobx-react-use-autorun";
import { Button } from "@mui/material";
import api from "@/api";
import { MessageService } from "@/common/MessageService";
import { FormattedMessage } from "react-intl";
import { faSpinner, faDownload, faTrashCan, faArrowRotateLeft } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { UserMessageModel } from "@/model/UserMessageModel";
import path from "path";
import { GlobalUserInfo } from "@/common/Server";

export default observer((props: {
  message: UserMessageModel
}) => {

  const state = useMobxState({
    loading: false,
  }, {
    ...props
  })

  async function withdrawn() {
    if (state.loading) {
      return;
    }
    try {
      state.loading = true;
      await api.UserMessage.recallMessage(state.message.id);
    } catch (error) {
      MessageService.error(error);
      state.loading = false;
    }
  }

  async function deleteMessage() {
    if (state.loading) {
      return;
    }
    try {
      state.loading = true;
      await api.UserMessage.deleteMessage(state.message.id);
    } catch (error) {
      MessageService.error(error);
      state.loading = false;
    }
  }

  return <div className="flex flex-col"
    style={{
      whiteSpace: "pre-wrap",
      wordBreak: "break-word",
      overflowWrap: "break-word",
    }}
  >
    <div className="flex flex-row justify-between">
      <div className="flex flex-row">
        {state.message.pageNum}
        {":"}
      </div>
      {state.message.user.id === GlobalUserInfo.id && <Button
        variant="outlined"
        onClick={withdrawn}
        style={{ marginRight: "1em" }}
        size="small"
        startIcon={<FontAwesomeIcon icon={state.loading ? faSpinner : faArrowRotateLeft} spin={state.loading} style={{ fontSize: "small" }} />}
      >
        <FormattedMessage id="Withdrawn" defaultMessage="Withdrawn" />
      </Button>}
      {state.message.user.id !== GlobalUserInfo.id && <Button
        variant="outlined"
        onClick={deleteMessage}
        style={{ marginRight: "1em" }}
        size="small"
        startIcon={<FontAwesomeIcon icon={state.loading ? faSpinner : faTrashCan} spin={state.loading} style={{ fontSize: "small" }} />}
      >
        <FormattedMessage id="Delete" defaultMessage="Delete" />
      </Button>}
    </div>
    {!!state.message.url && <div>
      <Button
        variant="contained"
        startIcon={<FontAwesomeIcon icon={faDownload} />}
        href={state.message.url}
        download={true}
      >
        {decodeURIComponent(path.basename(state.message.url))}
      </Button>
    </div>}
    {!state.message.url && <div>
      {state.message.content}
    </div>}
  </div>
})

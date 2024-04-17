import { observer, useMobxState } from "mobx-react-use-autorun";
import { Button } from "@mui/material";
import api from "@/api";
import { MessageService } from "@/common/MessageService";
import { FormattedMessage } from "react-intl";
import { faSpinner, faEyeSlash } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { UserMessageModel } from "@/model/UserMessageModel";

export default observer((props: {
  message: UserMessageModel
}) => {

  const state = useMobxState({
    loading: false
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
      state.message.isRecall = true
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
    <div>
      <Button
        variant="contained"
        onClick={withdrawn}
        startIcon={<FontAwesomeIcon icon={state.loading ? faSpinner : faEyeSlash} spin={state.loading} style={{ fontSize: "small" }} />}
      >
        <FormattedMessage id="Withdrawn" defaultMessage="Withdrawn" />
      </Button>
    </div>
    <div>
      {state.message.content}
    </div>
  </div>
})

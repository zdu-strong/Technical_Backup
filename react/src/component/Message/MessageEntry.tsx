import { GlobalUserInfo } from "@/common/Server";
import MessageChat from "@/component/Message/MessageChat";
import MessageMenu from "@/component/Message/MessageMenu";
import { observer } from "mobx-react-use-autorun";
import { stylesheet } from "typestyle";
import MessageUnlimitedAutoSizer from "@/component/Message/MessageUnlimitedAutoSizer";

const css = stylesheet({
  container: {
    width: "100%",
    height: "100%",
    flex: "1 1 auto",
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
    paddingLeft: "1em",
    paddingRight: "1em",
  },
})

export default observer(() => {

  return <div className={css.container} >
    <MessageMenu userId={GlobalUserInfo.id} username={GlobalUserInfo.username} />
    <MessageUnlimitedAutoSizer key={GlobalUserInfo.accessToken} />
    <MessageChat
      userId={GlobalUserInfo.id!}
      username={GlobalUserInfo.username!}
    />
  </div>
})
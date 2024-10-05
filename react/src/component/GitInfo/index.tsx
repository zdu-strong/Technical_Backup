import { observer, useMobxState } from "mobx-react-use-autorun";
import GitInfo from 'react-git-info/macro'
import { useMount } from "mobx-react-use-autorun";
import { format, parseJSON } from "date-fns";
import api from "@/api";
import { GitPropertiesModel } from "@/model/GitPropertiesModel";
import { FormattedMessage } from "react-intl";
import LoadingOrErrorComponent from "@/common/MessageService/LoadingOrErrorComponent";

export default observer(() => {

  const state = useMobxState({
    clientGitInfo: GitInfo(),
    serverGitInfo: null as any as GitPropertiesModel,
    ready: false,
    error: null as any,
  })

  useMount(async () => {
    try {
      await loadServerGitInfo();
      state.ready = true;
    } catch {
      state.error = new Error("Network Error");
    }
  })

  async function loadServerGitInfo() {
    state.serverGitInfo = await api.Git.getServerGitInfo();
  }

  return <LoadingOrErrorComponent ready={state.ready} error={state.error}>
    {
      state.ready && <>
        <div className="w-full h-full flex justify-center flex-col items-center">
          <div className="flex flex-row">
            <div className="flex flex-row">
              <FormattedMessage id="FrontEndCommitId" defaultMessage="Front-end commit id" />
              {`: `}
            </div>
            <div className="flex flex-row" id="FrontEndCommitIdInfo" style={{ marginLeft: "1em" }}>
              {state.clientGitInfo.commit.hash}
            </div>
          </div>
          <div className="flex flex-row">
            <div className="flex flex-row">
              <FormattedMessage id="BackendCommitId" defaultMessage="Backend commit id" />
              {`: `}
            </div>
            <div className="flex flex-row" id="BackendCommitIdInfo" style={{ marginLeft: "1em" }}>
              {state.serverGitInfo.commitId}
            </div>
          </div>
          <div className="flex flex-row">
            <div className="flex flex-row">
              <FormattedMessage id="FrontEndUpdateTime" defaultMessage="Front-end update time" />
              {`: `}
            </div>
            <div className="flex flex-row" id="FrondEndUpdateDateInfo" style={{ marginLeft: "1em" }}>
              {format(parseJSON(state.clientGitInfo.commit.date), "yyyy-MM-dd HH:mm")}
            </div>
          </div>
          <div className="flex flex-row">
            <div className="flex flex-row">
              <FormattedMessage id="BackendUpdateTime" defaultMessage="Backend update time" />
              {`: `}
            </div>
            <div className="flex flex-row" id="BackendUpdateDateInfo" style={{ marginLeft: "1em" }}>
              {format(state.serverGitInfo.commitDate, "yyyy-MM-dd HH:mm")}
            </div>
          </div>
        </div>
      </>
    }
  </LoadingOrErrorComponent>
})
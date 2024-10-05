import { FriendshipModel } from "@/model/FriendshipModel";
import { observer, useMobxState } from "mobx-react-use-autorun";
import { useMount } from "mobx-react-use-autorun";
import FriendChildComponent from "@/component/Friend/FriendChild";
import api from '@/api'
import LoadingOrErrorComponent from "@/common/MessageService/LoadingOrErrorComponent";
import { FormattedMessage } from "react-intl";

export default observer(() => {

  const state = useMobxState({
    friendshipList: [] as FriendshipModel[],
    ready: false,
    error: null as any,
  })

  useMount(async () => {
    try {
      await getFriendList();
      state.ready = true;
    } catch (error) {
      state.error = error;
    }
  })

  async function getFriendList() {
    const { list } = await api.Friendship.getFriendList();
    state.friendshipList = list;
  }

  return <div className="flex flex-col flex-auto">
    <LoadingOrErrorComponent ready={state.ready} error={state.error}>
      <div>
        <FormattedMessage id="Friends" defaultMessage="Friends" />
      </div>
      {state.friendshipList.map(item => <FriendChildComponent
        friendship={item}
        key={item.id}
        refreshFriendList={getFriendList}
      />)}
    </LoadingOrErrorComponent>
  </div>;
})
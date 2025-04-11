import { FriendshipModel } from "@/model/FriendshipModel";
import { observer, useMobxState } from "mobx-react-use-autorun";
import { useMount } from "mobx-react-use-autorun";
import StrangerChild from "@/component/Stranger/StrangerChild";
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
      await getFriendshipList();
      state.ready = true;
    } catch (error) {
      state.error = error;
    }
  })

  async function getFriendshipList() {
    const { items: list } = await api.Friendship.getStrangerList();
    state.friendshipList = list;
  }

  return <div className="flex flex-col flex-auto">
    <LoadingOrErrorComponent ready={state.ready} error={state.error}>
      <div>
        <FormattedMessage id="Stranger" defaultMessage="Stranger" />
      </div>
      {state.friendshipList.map(item => <StrangerChild
        friendship={item}
        key={item.id}
        refreshFriendshipList={getFriendshipList}
      />)}
    </LoadingOrErrorComponent>
  </div>;
})
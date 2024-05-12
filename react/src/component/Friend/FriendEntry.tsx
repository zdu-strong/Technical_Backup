import { BottomNavigation, BottomNavigationAction } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import FriendList from "@/component/Friend/FriendList";
import StrangerList from "@/component/Stranger/StrangerList";
import { NavigationEnum } from "@/component/Friend/js/NavigationEnum";
import { useNavigate, useSearchParams } from "react-router-dom";
import { FormattedMessage } from "react-intl";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faGear, faUserGroup, faUsersBetweenLines } from "@fortawesome/free-solid-svg-icons";

export default observer(() => {

  const state = useMobxState({
    navigation: NavigationEnum.Friend,
  }, {
    navigate: useNavigate(),
    ...((() => {
      const [URLSearchParams, SetURLSearchParams] = useSearchParams();
      return {
        URLSearchParams,
        SetURLSearchParams
      };
    })()),
  })

  return <>
    <div className="flex flex-col flex-auto">
      <div className="flex flex-row flex-auto">
        {state.navigation === NavigationEnum.Friend && <FriendList />}
        {state.navigation === NavigationEnum.Stranger && <StrangerList />}
        {state.navigation === NavigationEnum.Setting && <div><FormattedMessage id="Setting" defaultMessage="Setting" /></div>}
      </div>
      <div className="flex flex-row">
        <BottomNavigation
          showLabels
          value={state.navigation}
          onChange={(event, newValue) => {
            state.navigation = newValue;
          }}
        >
          <BottomNavigationAction value={NavigationEnum.Friend} label="Friend" icon={<FontAwesomeIcon icon={faUserGroup} style={{ fontSize: "xx-large" }} />} />
          <BottomNavigationAction value={NavigationEnum.Stranger} label="Stranger" icon={<FontAwesomeIcon icon={faUsersBetweenLines} style={{ fontSize: "xx-large" }} />} />
          <BottomNavigationAction value={NavigationEnum.Setting} label="Setting" icon={<FontAwesomeIcon icon={faGear} style={{ fontSize: "xx-large" }} />} />
        </BottomNavigation>
      </div>
    </div>
  </>;
})
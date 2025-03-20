import { observer, useMobxState } from 'mobx-react-use-autorun';
import { useNavigate } from 'react-router-dom'
import { Button } from '@mui/material';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faArrowRightFromBracket, faSpinner, faUser } from '@fortawesome/free-solid-svg-icons';
import api from '@/api';
import { MessageService } from '@/common/MessageService';
import { FormattedMessage } from 'react-intl';
import { GlobalUserInfo } from '@/common/Server';

export default observer(() => {

  const state = useMobxState({
    signOut: {
      loading: false
    },
  }, {
    navigate: useNavigate(),
  })

  async function signOut() {
    if (state.signOut.loading) {
      return;
    }
    try {
      state.signOut.loading = true;
      await api.Authorization.signOut();
    } catch (e) {
      MessageService.error(e);
      state.signOut.loading = false;
    }
  }

  return <div className='flex flex-row items-center'>
    <Button
      variant="contained"
      color="secondary"
      startIcon={<FontAwesomeIcon icon={faUser} />}
      style={{
        marginLeft: "1em",
      }}
    >
      {GlobalUserInfo.username}
    </Button>

    <Button
      variant="contained"
      color="secondary"
      startIcon={<FontAwesomeIcon icon={state.signOut.loading ? faSpinner : faArrowRightFromBracket} spin={state.signOut.loading} />}
      onClick={signOut}
      style={{
        marginLeft: "1em",
        marginRight: "1em",
      }}
    >
      <FormattedMessage id="SignOut" defaultMessage="Sign out" />
    </Button>
  </div>
})
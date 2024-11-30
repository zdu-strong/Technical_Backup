import { observer, useMobxState } from 'mobx-react-use-autorun';
import { ReactNode } from 'react';
import { stylesheet } from 'typestyle';
import { useNavigate } from 'react-router-dom'
import { Button, Divider, IconButton, ListItemIcon, ListItemText, MenuItem, MenuList, Typography } from '@mui/material';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faArrowRightFromBracket, faBars, faCloud, faMessage, faPaste, faSpinner, faUser, faXmark } from '@fortawesome/free-solid-svg-icons';
import api from '@/api';
import { MessageService } from '@/common/MessageService';
import { FormattedMessage } from 'react-intl';
import { GlobalUserInfo, setGlobalUserInfo } from '@/common/Server';
import { faGitAlt } from '@fortawesome/free-brands-svg-icons'

const css = stylesheet({
  mainMenuContainer: {
    display: "flex",
    flex: "1 1 auto",
    flexDirection: "column",
    $nest: {

    }
  }
})

export default observer((props: {
  children: ReactNode
}) => {

  const state = useMobxState({
    signOut: {
      loading: false
    },
  }, {
    navigate: useNavigate(),
  })

  function switchMenuOpen() {
    setGlobalUserInfo({
      ...GlobalUserInfo,
      menuOpen: !GlobalUserInfo.menuOpen,
    })
  }

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

  return <div className={css.mainMenuContainer}>
    <div className='flex flex-row justify-between'>
      <IconButton
        color="primary"
        style={{ margin: "10px" }}
        onClick={switchMenuOpen}
      >
        <FontAwesomeIcon icon={GlobalUserInfo.menuOpen ? faXmark : faBars} />
      </IconButton>
      <div className='flex flex-row items-center'>
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
    </div>
    <Divider />
    <div className='flex flex-row flex-auto'>
      {GlobalUserInfo.menuOpen && <div className='flex flex-row'>
        <MenuList>
          <MenuItem onClick={() => state.navigate("/chat")}>
            <ListItemIcon >
              <FontAwesomeIcon icon={faMessage} />
            </ListItemIcon>
            <ListItemText><FormattedMessage id="Chat" defaultMessage="Chat" /></ListItemText>
            <Typography variant="body2" sx={{ color: 'text.secondary' }}>
              ⌘X
            </Typography>
          </MenuItem>
          <MenuItem onClick={() => state.navigate("/git")}>
            <ListItemIcon>
              <FontAwesomeIcon icon={faGitAlt} />
            </ListItemIcon>
            <ListItemText><FormattedMessage id="Git" defaultMessage="Git" /></ListItemText>
            <Typography variant="body2" sx={{ color: 'text.secondary' }}>
              ⌘C
            </Typography>
          </MenuItem>
          <MenuItem onClick={() => state.navigate("/super_admin/user_role/manage")}>
            <ListItemIcon>
              <FontAwesomeIcon icon={faPaste} />
            </ListItemIcon>
            <ListItemText><FormattedMessage id="UserRoleManage" defaultMessage="User Role Manage" /></ListItemText>
            <Typography variant="body2" sx={{ color: 'text.secondary' }}>
              ⌘V
            </Typography>
          </MenuItem>
          <MenuItem onClick={() => state.navigate("/super_admin/organize_role/manage")}>
            <ListItemIcon>
              <FontAwesomeIcon icon={faPaste} />
            </ListItemIcon>
            <ListItemText><FormattedMessage id="OrganizeRoleManage" defaultMessage="Organize Role Manage" /></ListItemText>
            <Typography variant="body2" sx={{ color: 'text.secondary' }}>
              ⌘V
            </Typography>
          </MenuItem>
          <MenuItem onClick={() => state.navigate("/super_admin/user/manage")}>
            <ListItemIcon>
              <FontAwesomeIcon icon={faPaste} />
            </ListItemIcon>
            <ListItemText><FormattedMessage id="UserManage" defaultMessage="User Manage" /></ListItemText>
            <Typography variant="body2" sx={{ color: 'text.secondary' }}>
              ⌘V
            </Typography>
          </MenuItem>
          <Divider />
          <MenuItem>
            <ListItemIcon>
              <FontAwesomeIcon icon={faCloud} />
            </ListItemIcon>
            <ListItemText><FormattedMessage id="WebClipboard" defaultMessage="Web Clipboard" /></ListItemText>
          </MenuItem>
        </MenuList>
        <Divider orientation="vertical" />
      </div>}

      <div className="flex flex-col flex-auto">
        {props.children}
      </div>
    </div>
  </div>
})

import Dialog from '@mui/material/Dialog';
import ListItemText from '@mui/material/ListItemText';
import ListItem from '@mui/material/ListItem';
import List from '@mui/material/List';
import Divider from '@mui/material/Divider';
import AppBar from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faXmark } from '@fortawesome/free-solid-svg-icons'
import { MessageMoreActionTranslation } from '@/component/Message/MessageMoreActionTranslation'
import { observer, useMobxState } from 'mobx-react-use-autorun';
import { FormattedMessage } from 'react-intl';

export default observer((props: {
  closeDialog: () => void,
  uploadFile: () => void,
}) => {

  const state = useMobxState({
  }, props);

  return (
    <Dialog
      fullScreen
      open={true}
      onClose={state.closeDialog}
      TransitionComponent={MessageMoreActionTranslation}
    >
      <AppBar sx={{ position: 'relative' }}>
        <Toolbar>

          <Typography sx={{ ml: 2, flex: 1 }} variant="h6" component="div">
            <FormattedMessage id="More" defaultMessage="More" />
          </Typography>
          <IconButton
            edge="start"
            color="inherit"
            onClick={state.closeDialog}
            aria-label="close"
          >
            <FontAwesomeIcon icon={faXmark} size="xl" />
          </IconButton>
        </Toolbar>
      </AppBar>
      <List>
        <ListItem
          onClick={() => state.uploadFile()}
          component="button"
        >
          <ListItemText
            primary={<FormattedMessage id="Upload" defaultMessage="Upload" />}
            secondary={<FormattedMessage id="File" defaultMessage="File" />}
          />
        </ListItem>
        <Divider />
      </List>
    </Dialog>
  );
})

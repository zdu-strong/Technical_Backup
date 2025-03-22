import { Button, IconButton, TextField } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import { FormattedMessage } from "react-intl";
import { stylesheet } from "typestyle";
import AccountTooltipDialog from "@/component/SignIn/AccountTooltipDialog";
import PasswordTooltipDialog from "@/component/SignIn/PasswordTooltipDialog";
import api from "@/api";
import { MessageService } from "@/common/MessageService";
import { Link } from "react-router-dom";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCircleQuestion, faArrowRightToBracket, faSpinner } from '@fortawesome/free-solid-svg-icons'

const css = stylesheet({
  container: {
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    paddingTop: "1em",
    paddingLeft: "5em",
    paddingRight: "5em",
  }
})

export default observer(() => {

  const state = useMobxState({
    username: 'zdu.strong@gmail.com',
    password: 'zdu.strong@gmail.com',
    submitted: false,
    usernameTooltipDialog: {
      open: false,
    },
    passwordTooltipDialog: {
      open: false,
    },
    loading: {
      signIn: false,
    },
    showPasswordInput: false,
    errors: {
      username() {
        if (state.username) {
          if (state.username.replaceAll(new RegExp('^\\s+', 'g'), '').length !== state.username.length) {
            return <FormattedMessage id="ThereShouldBeNoSpacesAtTheBeginningOfTheAccountId" defaultMessage="There should be no spaces at the beginning of the account ID" />
          }
          if (state.username.replaceAll(new RegExp('\\s+$', 'g'), '').length !== state.username.length) {
            return <FormattedMessage id="TheAccountIDCannotHaveASpaceAtTheEnd" defaultMessage="The account ID cannot have a space at the end" />
          }
        }
        if (!state.submitted) {
          return false;
        }
        if (!state.username) {
          return <FormattedMessage id="PleaseFillInTheAccountID" defaultMessage="Please fill in the account ID" />
        }
        return false;
      },
      password() {
        if (state.password) {
          if (state.password.replaceAll(new RegExp('^\\s+', 'g'), '').length !== state.password.length) {
            return <FormattedMessage id="PasswordMustNotHaveSpacesAtTheBeginning" defaultMessage="Password must not have spaces at the beginning" />
          }
          if (state.password.replaceAll(new RegExp('\\s+$', 'g'), '').length !== state.password.length) {
            return <FormattedMessage id="PasswordCannotHaveASpaceAtTheEnd" defaultMessage="Password cannot have a space at the end" />
          }
        }
        if (!state.submitted) {
          return false;
        }
        if (!state.password) {
          return <FormattedMessage id="PleaseFillInThePassword" defaultMessage="Please fill in the password" />
        }
        return false;
      },
      hasError() {
        return Object.keys(state.errors).filter(s => s !== "hasError").some(s => (state.errors as any)[s]());
      }
    },
  })

  async function signIn() {
    if (!state.errors.hasError()) {
      state.showPasswordInput = false;
    }

    if (state.loading.signIn) {
      return;
    }
    state.submitted = true;
    if (state.errors.hasError()) {
      return;
    }
    try {
      state.loading.signIn = true;
      await api.Authorization.signIn(state.username, state.password);
    } catch (e) {
      state.loading.signIn = false;
      MessageService.error(e);
    }
  }

  return <div className={css.container}>
    <div>
      <FormattedMessage id="SignIn" defaultMessage="SignIn" />
    </div>
    <div style={{ marginTop: "1em" }} className="flex flex-col w-full">
      <TextField
        label={<FormattedMessage id="AccountID" defaultMessage="Account ID" />}
        variant="outlined"
        onChange={(e) => {
          state.username = e.target.value;
        }}
        value={state.username}
        autoComplete="off"
        error={!!state.errors.username()}
        helperText={state.errors.username()}
        InputProps={{
          endAdornment: <IconButton
            color="primary"
            onClick={() => state.usernameTooltipDialog.open = true}
          >
            <FontAwesomeIcon icon={faCircleQuestion} />
          </IconButton>
        }}
        autoFocus={true}
      />
    </div>
    <div style={{ marginTop: "1em" }} className="flex flex-col w-full">
      {state.showPasswordInput && <TextField
        label={<FormattedMessage id="Password" defaultMessage="Password" />}
        className="flex flex-auto"
        variant="outlined"
        onChange={(e) => {
          state.password = e.target.value;
        }}
        inputProps={{
          style: {
            resize: "vertical",
          }
        }}
        value={state.password}
        autoComplete="off"
        multiline={true}
        rows={6}
        error={!!state.errors.password()}
        helperText={state.errors.password()}
        InputProps={{
          endAdornment: <IconButton
            color="primary"
            onClick={() => state.passwordTooltipDialog.open = true}
          >
            <FontAwesomeIcon icon={faCircleQuestion} />
          </IconButton>
        }}
        autoFocus={true}
      />}
      {!state.showPasswordInput && <Button
        variant="outlined"
        className="w-full normal-case"
        onClick={() => {
          state.showPasswordInput = true
        }}
      >
        <FormattedMessage id="ThePasswordHasBeenFilledInClickEdit" defaultMessage="The password has been filled in, click Edit" />
      </Button>}
    </div>
    <div style={{ marginTop: "1em" }}>
      <Button
        variant="contained"
        className="normal-case"
        startIcon={<FontAwesomeIcon icon={state.loading.signIn ? faSpinner : faArrowRightToBracket} spin={state.loading.signIn} />}
        onClick={signIn}
      >
        <FormattedMessage id="SignIn" defaultMessage="SignIn" />
      </Button>
    </div>
    <div className="w-full" style={{ marginTop: "2em" }}>
      <Link to="/sign-up">
        <FormattedMessage id="SignUp" defaultMessage="SignUp" />
      </Link>
      <Link to="/" style={{ marginLeft: "2em" }}>
        <FormattedMessage id="ReturnToHomePage" defaultMessage="To home" />
      </Link>
    </div>
    {state.usernameTooltipDialog.open && <AccountTooltipDialog
      closeDialog={() => state.usernameTooltipDialog.open = false}
    />}
    {state.passwordTooltipDialog.open && <PasswordTooltipDialog
      closeDialog={() => state.passwordTooltipDialog.open = false}
    />}
  </div>;
})
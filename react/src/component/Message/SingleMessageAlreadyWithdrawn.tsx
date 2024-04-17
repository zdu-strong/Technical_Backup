import { Alert } from "@mui/material";
import { observer } from "mobx-react-use-autorun";
import { FormattedMessage } from "react-intl";
import { stylesheet } from "typestyle";

const css = stylesheet({
  alertContainer: {
    marginRight: "3em"
  }
})

export default observer(() => {

  return <Alert severity="warning" className={css.alertContainer}>
    <FormattedMessage id="HasWithdrawn" defaultMessage="Has Withdrawn" />
  </Alert>
})

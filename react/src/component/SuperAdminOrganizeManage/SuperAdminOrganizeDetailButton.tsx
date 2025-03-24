import { faCircleInfo } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Button } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import { v1 } from "uuid";
import SuperAdminOrganizeDetailDialog from "@/component/SuperAdminOrganizeManage/SuperAdminOrganizeDetailDialog";
import { FormattedMessage } from "react-intl";

export default observer((props: { id: string, searchByPagination: () => void }) => {

  const state = useMobxState({
    dialog: {
      open: false,
      id: v1()
    }
  })

  function openDialog() {
    state.dialog = {
      open: true,
      id: v1()
    }
  }

  function closeDialog() {
    state.dialog.open = false;
  }

  return <>
    <Button
      onClick={openDialog}
      variant="contained"
      startIcon={<FontAwesomeIcon icon={faCircleInfo} />}
    >
      <FormattedMessage id="Detail" defaultMessage="Detail" />
    </Button>
    {
      state.dialog.open && <SuperAdminOrganizeDetailDialog
        id={props.id}
        searchByPagination={props.searchByPagination}
        key={state.dialog.id}
        closeDialog={closeDialog}
      />
    }
  </>
})
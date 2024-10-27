import { faCircleInfo } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Button } from "@mui/material";
import { observer } from "mobx-react-use-autorun";

export default observer((props: { id: string, searchByPagination: () => void }) => {



  return <Button
    onClick={() => null}
    variant="contained"
    startIcon={<FontAwesomeIcon icon={faCircleInfo} />}
  >
    {"详情"}
  </Button>
})
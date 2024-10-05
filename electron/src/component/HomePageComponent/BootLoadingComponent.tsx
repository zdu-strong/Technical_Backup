import { Divider } from "@mui/material";
import { FormattedMessage } from "react-intl";
import LoadingImage from './image/loading.gif';

export default (
  <div className="flex flex-col flex-auto" style={{ backgroundColor: "#8752a2" }}>
    <Divider />
    <div className="flex flex-row items-center justify-center" style={{ paddingTop: "30vh", padding: "1em" }}>
      <FormattedMessage id="PleaseWaitLoading" defaultMessage="Please wait, loading..." />
    </div>
    <div className="flex flex-auto flex-row justify-center items-center" style={{ padding: "1em" }}>
      <img src={LoadingImage} style={{ maxWidth: "100%", maxHeight: "100%" }} alt="" />
    </div>
  </div>
)


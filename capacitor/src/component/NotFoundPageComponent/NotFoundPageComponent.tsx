import { Button, Paper, Link as MaterialLink } from '@mui/material';
import { FormattedMessage } from 'react-intl';
import { Link } from "react-router-dom";

export default (<div className="flex flex-col flex-auto justify-center items-center">
  <Paper className="flex flex-col justify-center" variant="outlined" style={{ padding: "1em", marginBottom: "1em" }}>
    <div className="flex justify-center" style={{ paddingBottom: "1em" }}>
      <FormattedMessage id="notFound" defaultMessage="没有发现" />
    </div>
    <div>
      <Link to="/" className="no-underline" replace={true}>
        <MaterialLink underline="hover" component="div">
          <Button variant="text" color="primary" style={{ marginTop: "1em", fontSize: "large", paddingTop: "0", paddingBottom: "0" }}>
            <FormattedMessage id="backHomePage" defaultMessage="返回主页" />
          </Button>
        </MaterialLink>
      </Link>
    </div>
  </Paper>
</div>)
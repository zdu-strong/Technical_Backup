import { observer, useMobxState, useMount } from "mobx-react-use-autorun";
import { DataGrid, GridColDef, useGridApiRef } from '@mui/x-data-grid';
import { Box, Button } from "@mui/material";
import { format } from "date-fns";
import { AutoSizer } from "react-virtualized";
import api from "@/api";
import LoadingOrErrorComponent from "@/common/MessageService/LoadingOrErrorComponent";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSearch, faSpinner } from "@fortawesome/free-solid-svg-icons";
import { UserPaginationModel } from "@/model/UserPaginationModel";
import { UserModel } from "@/model/UserModel";
import SuperAdminUserDetailButton from "@/component/SuperAdminUserManage/SuperAdminUserDetailButton";
import { FormattedMessage } from "react-intl";

export default observer(() => {

  const state = useMobxState({
    ready: false,
    loading: true,
    error: null as any,
    userPaginationModel: null as any as UserPaginationModel,
    columns: [
      {
        headerName: 'ID',
        field: 'id',
        width: 290
      },
      {
        renderHeader: () => <FormattedMessage id="Name" defaultMessage="Name" />,
        field: 'username',
        width: 150,
        flex: 1,
      },
      {
        renderHeader: () => <FormattedMessage id="CreateDate" defaultMessage="Create Date" />,
        field: 'createDate',
        renderCell: (row) => {
          return <div>
            {format(row.row.createDate, "yyyy-MM-dd HH:mm:ss")}
          </div>
        },
        width: 150,
      },
      {
        renderHeader: () => <FormattedMessage id="Operation" defaultMessage="Operation" />,
        field: '',
        renderCell: (row) => <SuperAdminUserDetailButton
          id={row.row.id}
          searchByPagination={searchByPagination}
        />,
        width: 150,
      },
    ] as GridColDef<UserModel>[],
  }, {
    dataGridRef: useGridApiRef(),
  });

  useMount(async () => {
    await searchByPagination();
  })

  async function searchByPagination() {
    try {
      state.loading = true;
      state.userPaginationModel = await api.SuperAdminUserQuery.searchByPagination();
      state.loading = false;
      state.ready = true;
    } catch (e) {
      state.error = e;
    } finally {
      state.loading = false;
    }
  }

  return <LoadingOrErrorComponent ready={state.ready} error={!state.ready && state.error}>
    <div className="flex flex-col flex-auto" style={{ paddingLeft: "50px", paddingRight: "50px" }}>
      <div className="flex flex-row" style={{ marginTop: "10px", marginBottom: "10px" }}>
        <Button
          variant="contained"
          onClick={searchByPagination}
          startIcon={<FontAwesomeIcon icon={state.loading ? faSpinner : faSearch} spin={state.loading} />}
        >
          <FormattedMessage id="Refresh" defaultMessage="Refresh" />
        </Button>
      </div>
      <div className="flex flex-auto">
        <AutoSizer>
          {({ width, height }) => <Box width={width} height={height}>
            <DataGrid
              apiRef={state.dataGridRef}
              sortingMode="client"
              rows={state.userPaginationModel.list}
              getRowId={(s) => s.id}
              columns={state.columns}
              autoPageSize
              disableRowSelectionOnClick
              disableColumnMenu
              disableColumnResize
              disableColumnSorting
            />
          </Box>}
        </AutoSizer>
      </div>
    </div>
  </LoadingOrErrorComponent>
})

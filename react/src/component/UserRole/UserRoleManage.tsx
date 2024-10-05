import { observer, useMobxState, useMount } from "mobx-react-use-autorun";
import { DataGrid, GridColDef, useGridApiRef } from '@mui/x-data-grid';
import { Box, Button } from "@mui/material";
import { format } from "date-fns";
import { AutoSizer } from "react-virtualized";
import { SystemRolePaginationModel } from "@/model/SystemRolePaginationModel";
import api from "@/api";
import LoadingOrErrorComponent from "@/common/MessageService/LoadingOrErrorComponent";
import { SystemRoleModel } from "@/model/SystemRoleModel";

const columns: GridColDef<SystemRoleModel>[] = [
  {
    headerName: 'ID',
    field: 'id',
    width: 285
  },
  {
    headerName: 'Name',
    field: 'name',
    width: 150,
    flex: 1,
  },
  {
    headerName: 'Create date',
    field: 'createDate',
    description: 'This column has a value getter and is not sortable.',
    renderCell: (row) => {
      return <div>
        {format(row.row.createDate, "yyyy-MM-dd HH:mm:ss")}
      </div>
    },
    width: 150,
  },
];

export default observer(() => {

  const state = useMobxState({
    ready: false,
    loading: true,
    error: null as any,
    systemRolePaginationModel: null as any as SystemRolePaginationModel,
  }, {
    dataGridRef: useGridApiRef(),
  });

  useMount(async () => {
    await searchByPagination();
  })

  async function searchByPagination() {
    try {
      state.loading = true;
      state.systemRolePaginationModel = await api.SuperAdminQuerySystemRole.searchByPagination();
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
        <Button variant="contained" onClick={searchByPagination}>
          {"Refresh"}
        </Button>
      </div>
      <AutoSizer className="flex flex-col flex-auto">
        {({ width, height }) => <Box width={width} height={height}>
          <DataGrid
            className="flex flex-col flex-auto"
            apiRef={state.dataGridRef}
            sortingMode="client"
            rows={state.systemRolePaginationModel.list}
            getRowId={(s) => s.id}
            columns={columns}
            autoPageSize
            disableRowSelectionOnClick
            disableColumnMenu
            disableColumnResize
            disableColumnSorting
          />
        </Box>}
      </AutoSizer>
    </div>
  </LoadingOrErrorComponent>
})

import { observer, useMobxEffect, useMobxState } from "mobx-react-use-autorun";
import LoadingOrErrorComponent from "@/common/MessageService/LoadingOrErrorComponent";
import { useSearchParams } from "react-router-dom";
import OrganizeRoleManageMain from "./OrganizeRoleManageMain";

export default observer(() => {

  const state = useMobxState({
    companyId: "",
    error: null as any,
  }, {
    ...((() => {
      const [urlSearchParams, setURLSearchParams] = useSearchParams();
      return { urlSearchParams, setURLSearchParams };
    })()),
  });

  useMobxEffect(() => {
    const companyId = state.urlSearchParams.get("companyId");
    if (companyId) {
      state.companyId = companyId;
      state.error = null;
    } else {
      state.error = new Error("Company Id not exists");
    }
  }, [state.urlSearchParams])

  return <LoadingOrErrorComponent ready={!!state.companyId} error={state.error}>
    <OrganizeRoleManageMain companyId={state.companyId} key={state.companyId} />
  </LoadingOrErrorComponent>
})

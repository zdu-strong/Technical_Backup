import 'tailwindcss/utilities.css';
import '@/common/app-base-css/index.css';
import '@fontsource/roboto';
import '@/common/Server';
import '@/common/ScreenOrentation';
import '@/common/StorageManage/StorageManageRun';
import GlobalMessageComponent from '@/common/MessageService/GlobalMessageComponent';
import I18nComponent from '@/common/i18n/I18nComponent';
import Router from '@/router';
import { CssBaseline } from '@mui/material';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFnsV3';
import { observer, useMobxState } from 'mobx-react-use-autorun';
import { I18nEnum, useI18nLocale } from './common/i18n';
import { reportWebVitals } from '@/reportWebVitals';

reportWebVitals();

export default observer(() => {

  const state = useMobxState({
  }, {
    i18nLocale: useI18nLocale(),
  })

  return <div className="w-screen h-screen overflow-auto" style={{ backgroundColor: "white" }}>
    <div style={{ display: "flex", minWidth: "100%", height: "100%" }}>
      <div className='flex flex-col flex-auto' style={{ height: "max-content", minHeight: "100%" }}>
        <CssBaseline />
        <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={I18nEnum[state.i18nLocale].DateLocale}  >
          <I18nComponent>
            <GlobalMessageComponent />
            {Router}
          </I18nComponent>
        </LocalizationProvider>
      </div>
    </div>
  </div>;
})
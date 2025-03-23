import 'tailwindcss/utilities.css';
import '@/common/app-base-css/index.css';
import '@fontsource/roboto';
import '@/common/Server';
import GlobalMessageComponent from '@/common/MessageService/GlobalMessageComponent';
import { I18nEnum, useI18nLocale } from '@/common/i18n';
import I18nComponent from '@/common/i18n/I18nComponent';
import Router from '@/router';
import { CssBaseline } from '@mui/material';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFnsV3';
import { observer, useMobxState } from 'mobx-react-use-autorun';
import { stylesheet } from 'typestyle';
import vhCheck from 'vh-check';
import { reportWebVitals } from '@/reportWebVitals';

reportWebVitals();
vhCheck();

const css = stylesheet({
  appContainer: {
    height: 'calc(100vh - var(--vh-offset, 0px))'
  },
})

export default observer(() => {

  const state = useMobxState({
  }, {
    i18nLocale: useI18nLocale(),
  })

  return <div className={`w-screen overflow-auto ${css.appContainer}`}>
    <div className="flex flex-row min-w-full min-h-full w-max">
      <div className='flex flex-col flex-auto'>
        <CssBaseline />
        <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={I18nEnum[state.i18nLocale].DateLocale}>
          <I18nComponent>
            <GlobalMessageComponent />
            {Router}
          </I18nComponent>
        </LocalizationProvider>
      </div>
    </div>
  </div>;
})

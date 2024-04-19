import { ReactNode } from "react";
import { IntlProvider } from 'react-intl';
import { useI18nJson, useI18nLocale } from '.';
import { observer, useMobxEffect, useMobxState } from 'mobx-react-use-autorun';

export default observer((props: { children: ReactNode }) => {
  const state = useMobxState({
  }, {
    I18nLocale: useI18nLocale(),
    I18nJson: useI18nJson(),
    ...props
  });

  useMobxEffect(() => {
    window.document.getElementsByTagName("html")[0].setAttribute('lang', state.I18nLocale);
  }, [state.I18nLocale]);

  return (
    <IntlProvider messages={state.I18nJson} locale={state.I18nLocale}>
      {state.children}
    </IntlProvider>
  );
})
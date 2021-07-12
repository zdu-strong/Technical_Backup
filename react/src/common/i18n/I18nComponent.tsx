import { ReactNode } from "react";
import { IntlProvider } from 'react-intl';
import { useI18nJson, useI18nLocale } from '.';
import { useAsLocalSource, useAutorun, observer } from 'mobx-react-use-autorun';

export default observer((props: { children: ReactNode }) => {
    const source = useAsLocalSource({
        I18nLocale: useI18nLocale(),
        I18nJson: useI18nJson(),
        ...props
    });

    useAutorun(() => {
        window.document.getElementsByTagName("html")[0].setAttribute('lang', source.I18nLocale);
    }, [source.I18nLocale]);

    return (
        <IntlProvider messages={source.I18nJson} locale={source.I18nLocale}>
            {source.children}
        </IntlProvider>
    );
})
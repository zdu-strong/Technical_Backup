import ReactDom from 'react-dom/client'
import App from '@/App';
import { reportWebVitals } from '@/reportWebVitals';

ReactDom.createRoot(document.getElementById('root')!).render(
  <App />
)

reportWebVitals();
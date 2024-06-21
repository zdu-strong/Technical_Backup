import { BrowserRouter, Route, Routes } from 'react-router-dom';
import NotFound from '@/component/NotFound/NotFound';
import Home from '@/component/Home/Home';
import MessagePage from '@/router/page/MessagePage';
import GitInfo from '@/component/GitInfo';
import SignInPage from '@/router/page/SignInPage';
import SignUpPage from '@/router/page/SignUpPage';

export default (
  <BrowserRouter>
    <Routes>
      <Route index element={MessagePage} />
      <Route path="/chat" element={MessagePage} />
      <Route path="/sign_in" element={SignInPage} />
      <Route path="/sign_up" element={SignUpPage} />
      <Route path="/git" element={<GitInfo />} />
      <Route path="/home" element={<Home />} />
      <Route path="/404" element={NotFound} />
      <Route path="*" element={NotFound} />
    </Routes>
  </BrowserRouter>
)
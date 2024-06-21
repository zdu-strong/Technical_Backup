import { BrowserRouter, Route, Routes } from 'react-router-dom';
import NotFound from '@/component/NotFound/NotFound';
import SignUp from '@/component/SignUp/SignUp';
import Home from '@/component/Home/Home';
import MessagePage from '@/router/page/MessagePage';
import GitInfo from '@/component/GitInfo';
import SignInPage from '@/router/page/SignInPage'

export default (
  <BrowserRouter>
    <Routes>
      <Route index element={MessagePage} />
      <Route path="/chat" element={MessagePage} />
      <Route path="/sign_in" element={SignInPage} />
      <Route path="/sign_up" element={<SignUp />} />
      <Route path="/git" element={<GitInfo />} />
      <Route path="/home" element={<Home />} />
      <Route path="/404" element={NotFound} />
      <Route path="*" element={NotFound} />
    </Routes>
  </BrowserRouter>
)
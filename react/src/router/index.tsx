import { BrowserRouter, Route, Routes } from 'react-router-dom';
import NotFound from '@/component/NotFound/NotFound';
import SignIn from '@/component/SignIn/SignIn';
import SignUp from '@/component/SignUp/SignUp';
import Home from '@/component/Home/Home';
import MessageEntryPage from '@/router/page/MessageEntryPage';
import GitInfo from '@/component/GitInfo';

export default (
  <BrowserRouter>
    <Routes>
      <Route index element={MessageEntryPage} />
      <Route path="/chat" element={MessageEntryPage} />
      <Route path="/sign_in" element={<SignIn />} />
      <Route path="/sign_up" element={<SignUp />} />
      <Route path="/git" element={<GitInfo />} />
      <Route path="/home" element={<Home />} />
      <Route path="/404" element={NotFound} />
      <Route path="*" element={NotFound} />
    </Routes>
  </BrowserRouter>
)
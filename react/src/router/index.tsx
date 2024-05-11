import { BrowserRouter, Route, Routes } from 'react-router-dom';
import NotFoundPageComponent from '@/component/NotFoundPageComponent/NotFoundPageComponent';
import SignIn from '@/component/SignIn/SignIn';
import SignUp from '@/component/SignUp/SignUp';
import HomePageComponent from '@/component/HomePageComponent/HomePageComponent';
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
      <Route path="/home" element={<HomePageComponent />} />
      <Route path="/404" element={NotFoundPageComponent} />
      <Route path="*" element={NotFoundPageComponent} />
    </Routes>
  </BrowserRouter>
)
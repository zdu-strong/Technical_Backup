import { BrowserRouter, Route, Routes } from 'react-router-dom';
import GitInfo from '@/component/GitInfo';
import NotFoundPageComponent from '@/component/NotFoundPageComponent/NotFoundPageComponent';
import SignIn from '@/component/SignIn/SignIn';
import SignUp from '@/component/SignUp/SignUp';
import MessageEntry from '@/component/Message/MessageEntry';
import HomePageComponent from '@/component/HomePageComponent/HomePageComponent';

export default (
  <BrowserRouter>
    <Routes>
      <Route index element={<MessageEntry />} />
      <Route path="/chat" element={<MessageEntry />} />
      <Route path="/sign_in" element={<SignIn />} />
      <Route path="/sign_up" element={<SignUp />} />
      <Route path="/git" element={<GitInfo />} />
      <Route path="/home" element={<HomePageComponent />} />
      <Route path="/404" element={NotFoundPageComponent} />
      <Route path="*" element={NotFoundPageComponent} />
    </Routes>
  </BrowserRouter>
)
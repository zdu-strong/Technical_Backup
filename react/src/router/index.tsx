import { BrowserRouter, Route, Routes } from 'react-router-dom';
import Home from '@/component/Home/Home';
import MessagePage from '@/router/page/MessagePage';
import SignInPage from '@/router/page/SignInPage';
import SignUpPage from '@/router/page/SignUpPage';
import GitPage from '@/router/page/GitPage';
import NotFoundPage from '@/router/page/NotFoundPage';
import UserRoleManagePage from '@/router/page/UserRoleManagePage';
import OrganizeRoleManagePage from '@/router/page/OrganizeRoleManagePage';

export default (
  <BrowserRouter>
    <Routes>
      <Route index element={MessagePage} />
      <Route path="/chat" element={MessagePage} />
      <Route path="/sign_in" element={SignInPage} />
      <Route path="/sign_up" element={SignUpPage} />
      <Route path="/git" element={GitPage} />
      <Route path="/home" element={<Home />} />
      <Route path="/404" element={NotFoundPage} />
      <Route path="/user_role/manage" element={UserRoleManagePage} />
      <Route path="/organize_role/manage" element={OrganizeRoleManagePage} />
      <Route path="*" element={NotFoundPage} />
    </Routes>
  </BrowserRouter>
)
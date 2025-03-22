import { BrowserRouter, Route, Routes } from 'react-router-dom';
import Home from '@/component/Home/Home';
import MessagePage from '@/router/page/MessagePage';
import SignInPage from '@/router/page/SignInPage';
import SignUpPage from '@/router/page/SignUpPage';
import GitPage from '@/router/page/GitPage';
import NotFoundPage from '@/router/page/NotFoundPage';
import SuperAdminRoleManagePage from '@/router/page/SuperAdminRoleManagePage';
import SuperAdminOrganizeManagePage from '@/router/page/SuperAdminOrganizeManagePage';
import SuperAdminUserManagePage from '@/router/page/SuperAdminUserManagePage';

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
      <Route path="/super_admin/role/manage" element={SuperAdminRoleManagePage} />
      <Route path="/super_admin/organize/manage" element={SuperAdminOrganizeManagePage} />
      <Route path="/super_admin/user/manage" element={SuperAdminUserManagePage} />
      <Route path="*" element={NotFoundPage} />
    </Routes>
  </BrowserRouter>
)
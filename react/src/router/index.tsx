import { BrowserRouter, Route, Routes } from 'react-router-dom';
import Home from '@/component/Home/Home';
import MessagePage from '@/router/page/MessagePage';
import SignInPage from '@/router/page/SignInPage';
import SignUpPage from '@/router/page/SignUpPage';
import GitPage from '@/router/page/GitPage';
import NotFoundPage from '@/router/page/NotFoundPage';
import SuperAdminUserRoleManagePage from '@/router/page/SuperAdminUserRoleManagePage';
import SuperAdminOrganizeRoleManagePage from '@/router/page/SuperAdminOrganizeRoleManagePage';
import SuperAdminUserManagePage from '@/router/page/SuperAdminUserManagePage';

export default (
  <BrowserRouter future={{ v7_relativeSplatPath: true, v7_startTransition: true }}>
    <Routes>
      <Route index element={MessagePage} />
      <Route path="/chat" element={MessagePage} />
      <Route path="/sign_in" element={SignInPage} />
      <Route path="/sign_up" element={SignUpPage} />
      <Route path="/git" element={GitPage} />
      <Route path="/home" element={<Home />} />
      <Route path="/404" element={NotFoundPage} />
      <Route path="/super_admin/user_role/manage" element={SuperAdminUserRoleManagePage} />
      <Route path="/super_admin/organize_role/manage" element={SuperAdminOrganizeRoleManagePage} />
      <Route path="/super_admin/user/manage" element={SuperAdminUserManagePage} />
      <Route path="*" element={NotFoundPage} />
    </Routes>
  </BrowserRouter>
)
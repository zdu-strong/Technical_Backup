import { Route, Routes } from 'react-router-dom';
import BrowserRouterOrHashRouter from "@/common/router/BrowserRouterOrHashRouter";
import HomePageComponent from '@/component/HomePageComponent/HomePageComponent';
import NotFoundPageComponent from '@/component/NotFoundPageComponent/NotFoundPageComponent';

export default (
  <BrowserRouterOrHashRouter>
    <Routes>
      <Route index element={<HomePageComponent />} />
      <Route path="*" element={NotFoundPageComponent} />
    </Routes>
  </BrowserRouterOrHashRouter>
)
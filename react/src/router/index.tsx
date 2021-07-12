import { BrowserRouter, Route, Routes } from 'react-router-dom';
import Message from '../component/Message';
import NotFoundPageComponent from '../component/NotFoundPageComponent/NotFoundPageComponent';

export default (
    <BrowserRouter>
        <Routes>
            <Route index element={<Message />} />
            <Route path="/404" element={NotFoundPageComponent} />
            <Route path="*" element={NotFoundPageComponent} />
        </Routes>
    </BrowserRouter>
)
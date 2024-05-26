import React from 'react';
import { Route, Routes } from 'react-router-dom';
import ListPage from './components/ListPage';
import AuthPage from './components/AuthPage';

const RoutesComponent = () => {
  return (
    <Routes>
      <Route path="/" element={<AuthPage />} />
      <Route path="/list" element={<ListPage />} />
    </Routes>
  );
};

export default RoutesComponent;

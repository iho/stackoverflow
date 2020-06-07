import React from "react";
import "./App.css";
import CategoriesListComponent from "./pages/categories";
import CategoryDetailComponent from './pages/category'
import { BrowserRouter as Router, Switch, Route, Link } from "react-router-dom";
import { Container } from "semantic-ui-react";


function App() {
  return (
    <Router>
      <h1>1 </h1>
      <Container >
          <Switch>
            <Route path={'/categories/:id?'}>
              <CategoriesListComponent />
            </Route>
            <Route path={'/category/:id/:pageId?'}>
              <CategoryDetailComponent />
            </Route>
          </Switch>
        </Container>
    </Router>
  );
}

export default App;
